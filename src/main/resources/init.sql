-- 회원가입
INSERT INTO account (auth_type, identification, introduction, name, password, profile_image_idx)
VALUES ('TEAMLOG', 'duck', NULL, '오리', '$2a$10$jYELzNcCfeOXJjacCnCMxuWMSLUWa6ZGJPuzMhj6qe6o8G/DKHScG', NULL);

-- 프로젝트 생성
insert
into project
(access_modifier, create_time, introduction, master_account_id, name, thumbnail_idx, update_time)
values (0, '2024-03-16T23:07:32.527+0900', 'while (true) {
    log.warn("run");
}', 1, '개발자의 하루', NULL, now());

-- 프로젝트 멤버 추가
insert
into project_member
    (account_id, join_time, project_id)
values (1, now(), 1);

-- 게시글 작성
insert
into post
(access_modifier, address, comment_modifier, contents, create_time, project_id, update_time, writer_account_id)
values (0, '', 0, '첫 코딩 공부는 너무 어!려!워!', '2024-03-16T23:09:59.908+0900', 1, '2024-03-16T23:09:59.908+0900', 1);

-- 게시글 해시태그
insert into post_tag (name, post_id)
values ('오늘도', 1);

insert into post_tag (name, post_id)
values ('뚠뚠', 1);

insert into post_tag (name, post_id)
values ('열심히', 1);

insert into post_tag (name, post_id)
values ('개발을', 1);

insert into post_tag (name, post_id)
values ('하네', 1);

insert
into post_update_history
    (account_id, create_time, post_id)
values (1, now(), 1);


-- 댓글 작성
insert into comment (contents, create_time, parent_comment_id, post_id, update_time, writer_account_id)
values ('@duck 내 이름은 말야!', now(), NULL, 1, now(), 1);

-- 댓글 해시태그
insert into comment_mention (comment_id, target_account_id)
values (1, 1);

-- 태스크
insert into task (create_time, deadline, priority, project_id, status, task_name, update_time)
values (now(), NULL, 0, 1, 0, '퇴근', now())