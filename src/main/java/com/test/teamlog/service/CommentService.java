package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.CommentMentionRepository;
import com.test.teamlog.repository.CommentRepository;
import com.test.teamlog.repository.PostRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMentionRepository commentMentionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시물의 부모 댓글 조회
    public PagedResponse<CommentDTO.CommentInfo> getParentComments(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Comment> parentComments = commentRepository.getParentCommentsByPost(post, pageable);

        List<CommentDTO.CommentInfo> responses = new ArrayList<>();
        for (Comment comment : parentComments) {
            UserDTO.UserSimpleInfo writer = new UserDTO.UserSimpleInfo(comment.getWriter());

            List<String> commentMentions = new ArrayList<>();
            for (CommentMention targetUSer : comment.getCommentMentions()) {
                commentMentions.add(targetUSer.getTargetUser().getId());
            }

            CommentDTO.CommentInfo temp = CommentDTO.CommentInfo.builder()
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
    public PagedResponse<CommentDTO.CommentInfo> getChildComments(Long parentCommentId, int page, int size) {
        Comment comment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentCommentId));

        List<CommentDTO.CommentInfo> responses = new ArrayList<>();

        if(size == 0) {
            Pageable pageable = PageRequest.of(page, 1, Sort.Direction.DESC, "createTime");
            Page<Comment> childComments = commentRepository.getChildCommentsByParentComment(comment, pageable);
            return new PagedResponse<>(responses, 0, 0,
                    childComments.getTotalElements(), 0, true);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<Comment> childComments = commentRepository.getChildCommentsByParentComment(comment, pageable);

        responses = new ArrayList<>();
        for (Comment childComment : childComments) {
            UserDTO.UserSimpleInfo writer = new UserDTO.UserSimpleInfo(childComment.getWriter());

            List<String> commentMentions = new ArrayList<>();
            for (CommentMention targetUSer : childComment.getCommentMentions()) {
                commentMentions.add(targetUSer.getTargetUser().getId());
            }

            CommentDTO.CommentInfo temp = CommentDTO.CommentInfo.builder()
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
            UserDTO.UserSimpleInfo writer = new UserDTO.UserSimpleInfo(comment.getWriter());

            List<String> commentMentions = new ArrayList<>();
            for (CommentMention targetUSer : comment.getCommentMentions()) {
                commentMentions.add(targetUSer.getTargetUser().getId());
            }

            List<CommentDTO.CommentInfo> childComments = new ArrayList<>();
            for (Comment childComment : comment.getChildComments()) {
                UserDTO.UserSimpleInfo user = new UserDTO.UserSimpleInfo(childComment.getWriter());

                List<String> childCommentMentions = new ArrayList<>();
                for (CommentMention targetUSer : childComment.getCommentMentions()) {
                    childCommentMentions.add(targetUSer.getTargetUser().getId());
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
    public ApiResponse createComment(CommentDTO.CommentRequest request, User currentUser) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", request.getPostId()));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", request.getParentCommentId()));
        }

        Comment comment = Comment.builder()
                .writer(currentUser)
                .post(post)
                .contents(request.getContents())
                .parentComment(parentComment)
                .build();

        commentRepository.save(comment);

        List<CommentMention> commentMentions = new ArrayList<>();
        for (String targetId : request.getCommentMentions()) {
            User target = userRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("USER", "id", targetId));

            CommentMention commentMention = CommentMention.builder()
                    .comment(comment)
                    .targetUser(target)
                    .build();
            commentMentions.add(commentMention);
        }

        commentMentionRepository.saveAll(commentMentions);

        return new ApiResponse(Boolean.TRUE, "댓글 생성 성공");
    }

    // 댓글 수정
    @Transactional
    public ApiResponse updateComment(Long id, CommentDTO.CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        comment.setContents(request.getContents());
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
