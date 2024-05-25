package com.app.teamlog.domain.comment.service;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.service.query.AccountQueryService;
import com.app.teamlog.domain.comment.dto.CommentCreateInput;
import com.app.teamlog.domain.comment.dto.CommentCreateResult;
import com.app.teamlog.domain.comment.dto.CommentInfoResponse;
import com.app.teamlog.domain.comment.dto.CommentUpdateInput;
import com.app.teamlog.domain.comment.entity.Comment;
import com.app.teamlog.domain.comment.entity.CommentMention;
import com.app.teamlog.domain.comment.repository.CommentRepository;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.post.service.query.PostQueryService;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.dto.PagedResponse;
import com.app.teamlog.global.exception.BadRequestException;
import com.app.teamlog.global.exception.ResourceForbiddenException;
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
    public List<CommentInfoResponse> getCommentByAccount(Account currentAccount) {
        List<Comment> commentList = commentRepository.findAllByWriter(currentAccount);
        return makeCommentInfoResponseList(currentAccount, commentList);
    }

    // 게시물의 부모 댓글 조회
    public PagedResponse<CommentInfoResponse> readCommentListByPostId(long postId, Pageable pageable, Account currentAccount) {
        Post post = preparePost(postId);

        Page<Comment> commentList = commentRepository.findParentCommentListByPost(post, pageable);

        final List<CommentInfoResponse> responseList = makeCommentInfoResponseList(currentAccount, commentList.getContent());
        return new PagedResponse<>(responseList, commentList.getNumber(), commentList.getSize(),
                commentList.getTotalElements(), commentList.getTotalPages(), commentList.isLast());
    }

    // 대댓글 조회
    public PagedResponse<CommentInfoResponse> readChildCommentList(Long parentCommentId, Pageable pageable, Account currentAccount) {
        Comment comment = prepareComment(parentCommentId);

        Page<Comment> childCommentList = commentRepository.findAllByParentComment(comment, pageable);

        final List<CommentInfoResponse> responseList = makeCommentInfoResponseList(currentAccount, childCommentList.getContent());

        return new PagedResponse<>(responseList, childCommentList.getNumber(), childCommentList.getSize(),
                childCommentList.getTotalElements(), childCommentList.getTotalPages(), childCommentList.isLast());
    }

    private List<CommentInfoResponse> makeCommentInfoResponseList(Account currentAccount, List<Comment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) return Collections.emptyList();

        List<CommentInfoResponse> responseList = new ArrayList<>();

        for (Comment cmt : commentList) {
            Boolean isMyComment
                    = (currentAccount != null && cmt.getWriter().getIdentification().equals(currentAccount.getIdentification())) ? Boolean.TRUE : Boolean.FALSE;

            CommentInfoResponse response = CommentInfoResponse.from(cmt);
            response.setIsMyComment(isMyComment);

            responseList.add(response);
        }

        return responseList;
    }

    // 댓글 생성
    @Transactional
    public CommentCreateResult create(CommentCreateInput input, Account currentAccount) {
        Post post = preparePost(input.getPostId());
        Comment parentComment = input.getParentCommentId() != null ? prepareComment(input.getParentCommentId()) : null;

        Comment comment = input.toComment(currentAccount, post, parentComment);
        comment.addCommentMentions(makeCommentMentionList(input.getCommentMentions(), comment));

        final Comment newComment = commentRepository.save(comment);
        return CommentCreateResult.of(newComment);
    }

    private List<CommentMention> makeCommentMentionList(List<String> commentMentionIdentificationList, Comment comment) {
        if (CollectionUtils.isEmpty(commentMentionIdentificationList)) return Collections.emptyList();

        final List<Account> accountList = accountQueryService.findAllByIdentificationIn(commentMentionIdentificationList);
        final Map<String, Account> accountMap
                = accountList.stream().collect(Collectors.toMap(Account::getIdentification, Function.identity()));

        List<CommentMention> commentMentions = new ArrayList<>();
        List<String> invalidaccountIdentificationList = new ArrayList<>(); // 존재하지 않는 사용자 목록

        for (String identification : commentMentionIdentificationList) {
            if (!accountMap.containsKey(identification)) {
                invalidaccountIdentificationList.add(identification);
                continue;
            }

            CommentMention commentMention = CommentMention.builder()
                    .comment(comment)
                    .targetAccount(accountMap.get(identification))
                    .build();
            commentMentions.add(commentMention);
        }

        if (!invalidaccountIdentificationList.isEmpty()) {
            throw new BadRequestException("존재하지 않는 사용자가 포함되어 있습니다.. identificationList: " + invalidaccountIdentificationList);
        }

        return commentMentions;
    }

    // 댓글 수정
    @Transactional
    public ApiResponse update(Long id, CommentUpdateInput input, Account account) {
        Comment comment = prepareComment(id);
        if (!comment.getWriter().getIdentification().equals(account.getIdentification())) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 댓글 작성자 아님 )");
        }

        List<CommentMention> originalCommentMentions
                = comment.getCommentMentions() != null ? comment.getCommentMentions() : Collections.emptyList(); // 기존 댓글에 멘션된 사용자들
        Set<String> newCommentMentionSet = new HashSet<>(input.getCommentMentions()); // 새로 멘션된 사람들
        List<CommentMention> deletedCommentMentions = new ArrayList<>(); // 수정 후 멘션 목록에서 사라진 사람들

        // 새 멘션 대상과 멘션 대상에서 제외된 사람들 목록 설정
        for (CommentMention commentMention : originalCommentMentions) {
            final String targetaccountIdentification = commentMention.getTargetAccount().getIdentification();
            if (newCommentMentionSet.contains(targetaccountIdentification)) {
                newCommentMentionSet.remove(targetaccountIdentification);
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
    public ApiResponse deleteComment(Long id, Account account) {
        Comment comment = prepareComment(id);

        if (!comment.getWriter().getIdentification().equals(account.getIdentification())) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 댓글 작성자 아님 )");
        }

        commentRepository.delete(comment);
        return new ApiResponse(Boolean.TRUE, "댓글 삭제 성공");
    }

    private Post preparePost(Long postId) {
        return postQueryService.findById(postId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 Post입니다. id: " + postId));
    }

    private Comment prepareComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 Comment입니다. id: " + commentId));
    }
}
