package com.test.teamlog.domain.commentmention.service;

import com.test.teamlog.domain.commentmention.repository.CommentMentionRepository;
import com.test.teamlog.entity.CommentMention;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentMentionService {
    private final CommentMentionRepository commentMentionRepository;

    public List<CommentMention> createAll(List<CommentMention> commentMentionList) {
        return commentMentionRepository.saveAll(commentMentionList);
    }

}
