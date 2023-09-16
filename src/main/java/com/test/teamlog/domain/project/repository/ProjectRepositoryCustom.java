package com.test.teamlog.domain.project.repository;

import com.test.teamlog.domain.posttag.entity.PostTag;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<PostTag> findAllPostTagByProjectId(Long projectId);
}
