package com.app.teamlog.domain.project.entity;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.app.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.app.teamlog.domain.projectmember.entity.ProjectMember;
import com.app.teamlog.global.entity.AccessModifier;
import com.app.teamlog.global.entity.BaseTimeEntity;
import com.app.teamlog.domain.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "access_modifier", nullable = false, columnDefinition = "smallint")
    private AccessModifier accessModifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_account_id", nullable = false)
    private Account master;

    @OneToOne
    @JoinColumn(name = "thumbnail_idx")
    private FileInfo thumbnail;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectMember> projectMembers = new ArrayList<ProjectMember>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectFollower> projectFollowers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectJoin> projectJoins = new ArrayList<ProjectJoin>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Task> tasks = new ArrayList<>();

    public void delegateMaster(Account master) {
        this.master = master;
    }

    public void update(String name,
                       String introduction, AccessModifier accessModifier) {
        this.name = name;
        this.accessModifier = accessModifier;

        if (StringUtils.hasText(introduction)) {
            this.introduction = introduction;
        }
    }

    public void addProjectMember(ProjectMember projectMember) {
        this.projectMembers.add(projectMember);

        if (projectMember.getProject() != this) {
            projectMember.setProject(this);
        }
    }

    public boolean isProjectMaster(Account account) {
        if (account == null) return false;

        return this.master.getIdentification().equals(account.getIdentification());
    }

    public void updateThumbnail(FileInfo thumbnailFile) {
        this.thumbnail = thumbnailFile;
    }
}
