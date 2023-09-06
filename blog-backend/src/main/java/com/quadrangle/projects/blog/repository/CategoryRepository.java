package com.quadrangle.projects.blog.repository;

import com.quadrangle.projects.blog.entity.posting.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    Boolean existsByName(String name);
}
