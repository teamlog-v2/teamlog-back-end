package com.app.teamlog.domain.project.repository;

import com.app.teamlog.domain.posttag.entity.PostTag;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<PostTag> findAllPostTagByProjectId(Long projectId);
}
