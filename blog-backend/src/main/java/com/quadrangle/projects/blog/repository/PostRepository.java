package com.quadrangle.projects.blog.repository;

import com.quadrangle.projects.blog.entity.auth.User;
import com.quadrangle.projects.blog.entity.posting.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByAuthor(User user);
}
