package com.test.teamlog.domain.post.repository;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.global.entity.AccessModifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByProjectAndLocationIsNotNull(Project project);

    List<Post> findAllByProjectAndAccessModifierAndLocationIsNotNull(Project project, AccessModifier access);

    List<Post> findAllByWriter(User user);

    List<Post> findAllByWriterIn(List<User> following);

    List<Post> findAllByLocationIsNotNullAndAccessModifier(AccessModifier accessModifier);
}
