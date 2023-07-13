package com.test.teamlog.domain.comment.service;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.UserService;
import com.test.teamlog.domain.comment.dto.CommentCreateInput;
import com.test.teamlog.domain.comment.dto.CommentInfoResponse;
import com.test.teamlog.domain.comment.dto.CommentUpdateInput;
import com.test.teamlog.domain.comment.repository.CommentRepository;
import com.test.teamlog.domain.commentmention.service.CommentMentionService;
import com.test.teamlog.domain.post.repository.PostRepository;
import com.test.teamlog.entity.Comment;
import com.test.teamlog.entity.CommentMention;
import com.test.teamlog.entity.Post;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CommentMentionService commentMentionService;

    private final UserService userService;
    private final PostRepository postRepository;

    // 유저가 작성한 댓글 조회
    public List<CommentInfoResponse> getCommentByUser(User currentUser) {
        List<Comment> commentList = commentRepository.findAllByWriter(currentUser);
        return  makeCommentInfoResponseList(currentUser, commentList);
    }

    // 게시물의 부모 댓글 조회
    public PagedResponse<CommentInfoResponse> getParentComments(Long postId, int page, int size, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Comment> parentComments = commentRepository.findParentCommentListByPost(post, pageable);

        final List<CommentInfoResponse> responseList = makeCommentInfoResponseList(currentUser, parentComments.getContent());
        return new PagedResponse<>(responseList, parentComments.getNumber(), parentComments.getSize(),
                parentComments.getTotalElements(), parentComments.getTotalPages(), parentComments.isLast());
    }

    // 대댓글 조회
    public PagedResponse<CommentInfoResponse> getChildComments(Long parentCommentId, int page, int size, User currentUser) {
        Comment comment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentCommentId));

        if (size == 0) {
            Pageable pageable = PageRequest.of(page, 1, Sort.Direction.DESC, "createTime");
            Page<Comment> childComments = commentRepository.getChildCommentsByParentComment(comment, pageable);
            return new PagedResponse<>(Collections.emptyList(), 0, 0, childComments.getTotalElements(), 0, true);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Comment> childComments = commentRepository.getChildCommentsByParentComment(comment, pageable);

        final List<CommentInfoResponse> responseList
                = makeCommentInfoResponseList(currentUser, comment, childComments.getContent());

        return new PagedResponse<>(responseList, childComments.getNumber(), childComments.getSize(),
                childComments.getTotalElements(), childComments.getTotalPages(), childComments.isLast());
    }

    private List<CommentInfoResponse> makeCommentInfoResponseList(User currentUser, List<Comment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) return Collections.emptyList();

        return makeCommentInfoResponseList(currentUser, commentList.get(0), commentList);
    }
    private List<CommentInfoResponse> makeCommentInfoResponseList(User currentUser, Comment comment, List<Comment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) return Collections.emptyList();

        List<CommentInfoResponse> responseList = new ArrayList<>();

        for (Comment cmt : commentList) {
            UserRequest.UserSimpleInfo writer = new UserRequest.UserSimpleInfo(cmt.getWriter());
            Boolean isMyComment
                    = currentUser != null && comment.getWriter().getIdentification().equals(currentUser.getIdentification()) ? Boolean.TRUE : Boolean.FALSE;

            final CommentInfoResponse response = CommentInfoResponse.of(cmt);
            response.setIsMyComment(isMyComment);
            response.setWriter(writer);

            responseList.add(response);
        }

        return responseList;
    }

    // 댓글 생성
    @Transactional
    public ApiResponse create(CommentCreateInput input, User currentUser) {
        Post post = postRepository.findById(input.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", input.getPostId()));

        Comment parentComment = input.getParentCommentId() != null ?
                commentRepository.findById(input.getParentCommentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", input.getParentCommentId())) :
                null;

        Comment comment = input.toComment(currentUser, post, parentComment);
        commentRepository.save(comment);

        commentMentionService.createAll(makeCommentMentionList(input.getCommentMentions(), comment));
        return new ApiResponse(Boolean.TRUE, "댓글 생성 성공");
    }

    private List<CommentMention> makeCommentMentionList(List<String> commentMentionIdentificationList, Comment comment) {
        if (CollectionUtils.isEmpty(commentMentionIdentificationList)) return Collections.emptyList();

        final List<User> userList = userService.findAllByIdentificationIn(commentMentionIdentificationList);
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
