package com.quadrangle.projects.blog.repository;

import com.quadrangle.projects.blog.entity.posting.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
