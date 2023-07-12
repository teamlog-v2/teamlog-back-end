package com.test.teamlog.domain.comment.service;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.UserRepository;
import com.test.teamlog.domain.account.service.UserService;
import com.test.teamlog.domain.comment.dto.CommentCreateInput;
import com.test.teamlog.domain.comment.repository.CommentRepository;
import com.test.teamlog.domain.commentmention.service.CommentMentionService;
import com.test.teamlog.domain.post.repository.PostRepository;
import com.test.teamlog.entity.Comment;
import com.test.teamlog.entity.CommentMention;
import com.test.teamlog.entity.Post;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.CommentDTO;
import com.test.teamlog.payload.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final UserRepository userRepository;

    // 유저가 작성한 댓글 조회
    public List<CommentDTO.CommentInfo> getCommentByUser(User currentUser) {
        List<Comment> commentList = commentRepository.findAllByWriter(currentUser);

        List<CommentDTO.CommentInfo> responses = new ArrayList<>();
        if(commentList.size() != 0) {
            UserRequest.UserSimpleInfo writer = new UserRequest.UserSimpleInfo(currentUser);

            for (Comment comment : commentList) {
                List<String> commentMentions = new ArrayList<>();
                for (CommentMention targetUSer : comment.getCommentMentions()) {
                    commentMentions.add(targetUSer.getTargetUser().getIdentification());
                }

                Boolean isMyComment = Boolean.TRUE;
                if (currentUser==null || !comment.getWriter().getIdentification().equals(currentUser.getIdentification())) isMyComment = Boolean.FALSE;

                CommentDTO.CommentInfo temp = CommentDTO.CommentInfo.builder()
                        .isMyComment(isMyComment)
                        .id(comment.getId())
                        .contents(comment.getContents())
                        .writer(writer)
                        .writeTime(comment.getCreateTime())
                        .commentMentions(commentMentions)
                        .build();
                responses.add(temp);
            }
        }
        return responses;
    }

    // 게시물의 부모 댓글 조회
    public PagedResponse<CommentDTO.CommentInfo> getParentComments(Long postId, int page, int size, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Comment> parentComments = commentRepository.getParentCommentsByPost(post, pageable);

        List<CommentDTO.CommentInfo> responses = new ArrayList<>();
        for (Comment comment : parentComments) {
            UserRequest.UserSimpleInfo writer = new UserRequest.UserSimpleInfo(comment.getWriter());

            List<String> commentMentions = new ArrayList<>();
            for (CommentMention targetUSer : comment.getCommentMentions()) {
                commentMentions.add(targetUSer.getTargetUser().getIdentification());
            }

            Boolean isMyComment = Boolean.TRUE;
            if (currentUser==null || !comment.getWriter().getIdentification().equals(currentUser.getIdentification())) isMyComment = Boolean.FALSE;

            CommentDTO.CommentInfo temp = CommentDTO.CommentInfo.builder()
                    .isMyComment(isMyComment)
                    .id(comment.getId())
                    .contents(comment.getContents())
                    .writer(writer)
                    .writeTime(comment.getCreateTime())
                    .commentMentions(commentMentions)
                    .build();
            responses.add(temp);
        }

        return new PagedResponse<>(responses, parentComments.getNumber(), parentComments.getSize(),
                parentComments.getTotalElements(), parentComments.getTotalPages(), parentComments.isLast());
    }

    // 대댓글 조회
    public PagedResponse<CommentDTO.CommentInfo> getChildComments(Long parentCommentId, int page, int size, User currentUser) {
        Comment comment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentCommentId));

        List<CommentDTO.CommentInfo> responses = new ArrayList<>();

        if (size == 0) {
            Pageable pageable = PageRequest.of(page, 1, Sort.Direction.DESC, "createTime");
            Page<Comment> childComments = commentRepository.getChildCommentsByParentComment(comment, pageable);
            return new PagedResponse<>(responses, 0, 0,
                    childComments.getTotalElements(), 0, true);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Comment> childComments = commentRepository.getChildCommentsByParentComment(comment, pageable);

        responses = new ArrayList<>();
        for (Comment childComment : childComments) {
            UserRequest.UserSimpleInfo writer = new UserRequest.UserSimpleInfo(childComment.getWriter());

            List<String> commentMentions = new ArrayList<>();
            for (CommentMention targetUSer : childComment.getCommentMentions()) {
                commentMentions.add(targetUSer.getTargetUser().getIdentification());
            }

            Boolean isMyComment = Boolean.TRUE;
            if (currentUser==null || !comment.getWriter().getIdentification().equals(currentUser.getIdentification())) isMyComment = Boolean.FALSE;

            CommentDTO.CommentInfo temp = CommentDTO.CommentInfo.builder()
                    .isMyComment(isMyComment)
                    .id(childComment.getId())
                    .contents(childComment.getContents())
                    .writer(writer)
                    .writeTime(childComment.getCreateTime())
                    .commentMentions(commentMentions)
                    .build();
            responses.add(temp);
        }

        return new PagedResponse<>(responses, childComments.getNumber(), childComments.getSize(),
                childComments.getTotalElements(), childComments.getTotalPages(), childComments.isLast());
    }

    // 게시물 댓글 조회
    public List<CommentDTO.CommentResponse> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<Comment> parentComments = commentRepository.findByPostAndParentCommentIsNull(post);
        List<CommentDTO.CommentResponse> responses = new ArrayList<>();

        for (Comment comment : parentComments) {
            UserRequest.UserSimpleInfo writer = new UserRequest.UserSimpleInfo(comment.getWriter());

            List<String> commentMentions = new ArrayList<>();
            for (CommentMention targetUSer : comment.getCommentMentions()) {
                commentMentions.add(targetUSer.getTargetUser().getIdentification());
            }

            List<CommentDTO.CommentInfo> childComments = new ArrayList<>();
            for (Comment childComment : comment.getChildComments()) {
                UserRequest.UserSimpleInfo user = new UserRequest.UserSimpleInfo(childComment.getWriter());

                List<String> childCommentMentions = new ArrayList<>();
                for (CommentMention targetUSer : childComment.getCommentMentions()) {
                    childCommentMentions.add(targetUSer.getTargetUser().getIdentification());
                }

                CommentDTO.CommentInfo childTemp = CommentDTO.CommentInfo.builder()
                        .id(childComment.getId())
                        .contents(childComment.getContents())
                        .writer(user)
                        .writeTime(childComment.getCreateTime())
                        .commentMentions(childCommentMentions)
                        .build();
                childComments.add(childTemp);
            }

            CommentDTO.CommentResponse temp = CommentDTO.CommentResponse.builder()
                    .id(comment.getId())
                    .contents(comment.getContents())
                    .writer(writer)
                    .childComments(childComments)
                    .writeTime(comment.getCreateTime())
                    .commentMentions(commentMentions)
                    .build();
            responses.add(temp);
        }
        return responses;
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

        createCommentMentionList(input.getCommentMentions(), comment);

        return new ApiResponse(Boolean.TRUE, "댓글 생성 성공");
    }

    private void createCommentMentionList(List<String> commentMentionIdentificationList, Comment comment) {
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

        commentMentionService.createAll(commentMentions);
    }

    // 댓글 수정
    @Transactional
    public ApiResponse updateComment(Long id, CommentDTO.CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        comment.setContents(request.getContents());

        List<CommentMention> originalCommentMentions = null;
        if (comment.getCommentMentions() != null) {
            originalCommentMentions = comment.getCommentMentions();
        }

        if (request.getCommentMentions() == null) {
            if (originalCommentMentions != null) comment.removeCommentMentions(originalCommentMentions);
        } else {
            List<String> newCommentMentions = request.getCommentMentions();
            List<String> maintainedCommentMentions = new ArrayList<>();
            if (originalCommentMentions != null) {
                List<CommentMention> deletedCommentMentions = new ArrayList<>();
                for (CommentMention commentMention : originalCommentMentions) {
                    if (newCommentMentions.contains(commentMention.getTargetUser().getIdentification())) {
                        maintainedCommentMentions.add(commentMention.getTargetUser().getIdentification());
                    } else {
                        deletedCommentMentions.add(commentMention);
                    }
                }
                comment.removeCommentMentions(deletedCommentMentions);
            }

            newCommentMentions.removeAll(maintainedCommentMentions); // new
            if (newCommentMentions.size() > 0) {
                List<CommentMention> commentMentions = new ArrayList<>();
                for (String targetId : newCommentMentions) {
                    User target = userRepository.findByIdentification(targetId)
                            .orElseThrow(() -> new ResourceNotFoundException("USER", "id", targetId));

                    CommentMention commentMention = CommentMention.builder()
                            .comment(comment)
                            .targetUser(target)
                            .build();
                    commentMentions.add(commentMention);
                }
                comment.addCommentMentions(commentMentions);
            }
        }
        commentRepository.save(comment);
        return new ApiResponse(Boolean.TRUE, "댓글 수정 성공");
    }

    // 댓글 삭제
    @Transactional
    public ApiResponse deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        commentRepository.delete(comment);
        return new ApiResponse(Boolean.TRUE, "댓글 삭제 성공");
    }
}
