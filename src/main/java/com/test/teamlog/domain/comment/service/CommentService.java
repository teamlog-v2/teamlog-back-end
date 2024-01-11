package com.test.teamlog.domain.comment.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.comment.dto.CommentCreateInput;
import com.test.teamlog.domain.comment.dto.CommentCreateResult;
import com.test.teamlog.domain.comment.dto.CommentInfoResponse;
import com.test.teamlog.domain.comment.dto.CommentUpdateInput;
import com.test.teamlog.domain.comment.entity.Comment;
import com.test.teamlog.domain.comment.entity.CommentMention;
import com.test.teamlog.domain.comment.repository.CommentRepository;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.post.service.query.PostQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.dto.PagedResponse;
import com.test.teamlog.global.exception.ResourceForbiddenException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    private final PostQueryService postQueryService;
    private final AccountQueryService accountQueryService;

    // 유저가 작성한 댓글 조회
    public List<CommentInfoResponse> getCommentByUser(User currentUser) {
        List<Comment> commentList = commentRepository.findAllByWriter(currentUser);
        return makeCommentInfoResponseList(currentUser, commentList);
    }

    // 게시물의 부모 댓글 조회
    public PagedResponse<CommentInfoResponse> readCommentListByPostId(long postId, Pageable pageable, User currentUser) {
        Post post = postQueryService.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Page<Comment> commentList = commentRepository.findParentCommentListByPost(post, pageable);

        final List<CommentInfoResponse> responseList = makeCommentInfoResponseList(currentUser, commentList.getContent());
        return new PagedResponse<>(responseList, commentList.getNumber(), commentList.getSize(),
                commentList.getTotalElements(), commentList.getTotalPages(), commentList.isLast());
    }

    // 대댓글 조회
    public PagedResponse<CommentInfoResponse> readChildCommentList(Long parentCommentId, Pageable pageable, User currentUser) {
        Comment comment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentCommentId));

        Page<Comment> childCommentList = commentRepository.findAllByParentComment(comment, pageable);

        final List<CommentInfoResponse> responseList = makeCommentInfoResponseList(currentUser, childCommentList.getContent());

        return new PagedResponse<>(responseList, childCommentList.getNumber(), childCommentList.getSize(),
                childCommentList.getTotalElements(), childCommentList.getTotalPages(), childCommentList.isLast());
    }

    private List<CommentInfoResponse> makeCommentInfoResponseList(User currentUser, List<Comment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) return Collections.emptyList();

        List<CommentInfoResponse> responseList = new ArrayList<>();

        for (Comment cmt : commentList) {
            Boolean isMyComment
                    = (currentUser != null && cmt.getWriter().getIdentification().equals(currentUser.getIdentification())) ? Boolean.TRUE : Boolean.FALSE;

            CommentInfoResponse response = CommentInfoResponse.from(cmt);
            response.setIsMyComment(isMyComment);

            responseList.add(response);
        }

        return responseList;
    }

    // 댓글 생성
    @Transactional
    public CommentCreateResult create(CommentCreateInput input, User currentUser) {
        Post post = postQueryService.findById(input.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", input.getPostId()));

        Comment parentComment = input.getParentCommentId() != null ?
                commentRepository.findById(input.getParentCommentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", input.getParentCommentId())) :
                null;

        Comment comment = input.toComment(currentUser, post, parentComment);
        comment.addCommentMentions(makeCommentMentionList(input.getCommentMentions(), comment));

        final Comment newComment = commentRepository.save(comment);
        return CommentCreateResult.of(newComment);
    }

    private List<CommentMention> makeCommentMentionList(List<String> commentMentionIdentificationList, Comment comment) {
        if (CollectionUtils.isEmpty(commentMentionIdentificationList)) return Collections.emptyList();

        final List<User> userList = accountQueryService.findAllByIdentificationIn(commentMentionIdentificationList);
        final Map<String, User> userMap
                = userList.stream().collect(Collectors.toMap(User::getIdentification, Function.identity()));

        List<CommentMention> commentMentions = new ArrayList<>();
        List<String> invalidUserIdentificationList = new ArrayList<>(); // 존재하지 않는 사용자 목록

        for (String identification : commentMentionIdentificationList) {
            if (!userMap.containsKey(identification)) {
                invalidUserIdentificationList.add(identification);
                continue;
            }

            CommentMention commentMention = CommentMention.builder()
                    .comment(comment)
                    .targetUser(userMap.get(identification))
                    .build();
            commentMentions.add(commentMention);
        }

        if (!invalidUserIdentificationList.isEmpty()) {
            throw new ResourceNotFoundException("User", "identification", invalidUserIdentificationList.toString());
        }

        return commentMentions;
    }

    // 댓글 수정
    @Transactional
    public ApiResponse update(Long id, CommentUpdateInput input, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        if (!comment.getWriter().getIdentification().equals(user.getIdentification())) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 댓글 작성자 아님 )");
        }

        List<CommentMention> originalCommentMentions
                = comment.getCommentMentions() != null ? comment.getCommentMentions() : Collections.emptyList(); // 기존 댓글에 멘션된 사용자들
        Set<String> newCommentMentionSet = new HashSet<>(input.getCommentMentions()); // 새로 멘션된 사람들
        List<CommentMention> deletedCommentMentions = new ArrayList<>(); // 수정 후 멘션 목록에서 사라진 사람들

        // 새 멘션 대상과 멘션 대상에서 제외된 사람들 목록 설정
        for (CommentMention commentMention : originalCommentMentions) {
            final String targetUserIdentification = commentMention.getTargetUser().getIdentification();
            if (newCommentMentionSet.contains(targetUserIdentification)) {
                newCommentMentionSet.remove(targetUserIdentification);
            } else {
                deletedCommentMentions.add(commentMention);
            }
        }

        comment.update(input.getContents());
        comment.removeCommentMentions(deletedCommentMentions);
        comment.addCommentMentions(makeCommentMentionList(new ArrayList<>(newCommentMentionSet), comment));

        commentRepository.save(comment);
        return new ApiResponse(Boolean.TRUE, "댓글 수정 성공");
    }

    // 댓글 삭제
    @Transactional
    public ApiResponse deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        if (!comment.getWriter().getIdentification().equals(user.getIdentification())) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 댓글 작성자 아님 )");
        }

        commentRepository.delete(comment);
        return new ApiResponse(Boolean.TRUE, "댓글 삭제 성공");
    }
}
