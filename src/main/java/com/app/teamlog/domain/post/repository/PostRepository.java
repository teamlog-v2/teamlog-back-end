package com.app.teamlog.domain.post.repository;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.global.entity.AccessModifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByProjectAndLocationIsNotNull(Project project);

    List<Post> findAllByProjectAndAccessModifierAndLocationIsNotNull(Project project, AccessModifier access);

    List<Post> findAllByWriter(Account account);

    List<Post> findAllByWriterIn(List<Account> following);

    List<Post> findAllByLocationIsNotNullAndAccessModifier(AccessModifier accessModifier);
}
