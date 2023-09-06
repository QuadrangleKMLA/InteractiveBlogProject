package com.quadrangle.projects.blog.controller;

import com.quadrangle.projects.blog.entity.auth.User;
import com.quadrangle.projects.blog.entity.posting.Category;
import com.quadrangle.projects.blog.entity.posting.Comment;
import com.quadrangle.projects.blog.entity.posting.Post;
import com.quadrangle.projects.blog.exception.exceptionClass.ResourceNotFoundException;
import com.quadrangle.projects.blog.repository.CategoryRepository;
import com.quadrangle.projects.blog.repository.CommentRepository;
import com.quadrangle.projects.blog.repository.PostRepository;
import com.quadrangle.projects.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/posting")
public class PostController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PostController(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found"));

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/posts/author/{author}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable("author") String author) {
        User user = userRepository.findByUsername(author)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + author + " not found."));

        List<Post> posts = postRepository.findByAuthor(user);

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{category}")
    public ResponseEntity<List<Post>> getPostsByCategory(@PathVariable("category") String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + categoryName + " not found."));

        List<Post> posts = postRepository.findAll();
        List<Post> categoryPost = new ArrayList<>();

        posts.forEach(post -> {
            if (post.getCategories().contains(category)) {
                categoryPost.add(post);
            }
        });

        if (categoryPost.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(categoryPost, HttpStatus.OK);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Map<String, String> payload) {
        User user = userRepository.findByUsername(payload.get("author"))
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + payload.get("author") + " not found."));

        Post post = new Post(payload.get("title"), payload.get("content"), user);

        return new ResponseEntity<>(postRepository.save(post), HttpStatus.CREATED);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") int id, @RequestBody Map<String, String> payload) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found."));

        post.setTitle(payload.get("title"));
        post.setContent(payload.get("content"));
        post.setUpdatedAt(new Date());

        return new ResponseEntity<>(postRepository.save(post), HttpStatus.OK);
    }

    @PutMapping("/posts/{id}/category")
    public ResponseEntity<Post> updatePostCategory(@PathVariable("id") int id, @RequestBody Map<String, List<String>> payload) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found."));

        Set<Category> categories = post.getCategories();

        List<String> add = payload.get("add");
        List<String> remove = payload.get("remove");

        if (add.isEmpty() && categories.isEmpty() && !remove.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            add.forEach(name -> {
                Category category = categoryRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Category with name: " + name + " not found."));

                categories.add(category);
            });
            remove.forEach(name -> {
                Category category = categoryRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Category with name: " + name + " not found."));

                categories.remove(category);
            });
        }

        post.setCategories(categories);
        post.setUpdatedAt(new Date());

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable("id") int id) {
        postRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/posts/author/{author}")
    public ResponseEntity<HttpStatus> deletePostsByUser(@PathVariable("author") String author) {
        User user = userRepository.findByUsername(author)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + author + " not found."));

        postRepository.deleteAll(user.getPosts());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/posts")
    public ResponseEntity<HttpStatus> deleteAllPosts() {
        postRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable("postId") int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + postId + " not found."));

        List<Comment> comments = post.getComments();

        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("commentId") int id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id: " + id + " not found."));

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PostMapping("/comments/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable("postId") int postId, @RequestBody Map<String, String> payload) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + postId + " not found."));

        User user = userRepository.findByUsername(payload.get("author"))
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + payload.get("author") + " not found."));

        Comment comment = new Comment(payload.get("content"), user, post);

        List<Comment> comments = post.getComments();
        comments.add(comment);
        post.setComments(comments);
        post.setUpdatedAt(new Date());

        List<Comment> userComment = user.getComments();
        userComment.add(comment);
        user.setComments(userComment);

        postRepository.save(post);
        userRepository.save(user);

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable("commentId") int commentId, @RequestBody Map<String, String> payload) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id: " + commentId + " not found."));

        comment.setContent(payload.get("content"));
        comment.setUpdatedAt(new Date());

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<HttpStatus> deleteCommentById(@PathVariable("commentId") int commentId) {
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/comments/post/{postId}")
    public ResponseEntity<HttpStatus> deleteCommentsByPost(@PathVariable("postId") int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + postId + " not found."));

        List<Comment> comments = post.getComments();
        commentRepository.deleteAll(comments);

        post.setComments(new ArrayList<>());
        postRepository.save(post);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/categories/{category}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable("category") String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + categoryName + " not found."));

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> payload) {
        if (categoryRepository.existsByName(payload.get("name"))) {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
        }

        Category category = new Category(payload.get("name"));
        return new ResponseEntity<>(categoryRepository.save(category), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable int categoryId, @RequestBody Map<String, String> payload) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id: " + categoryId + " not found."));

        category.setName(payload.get("name"));
        return new ResponseEntity<>(categoryRepository.save(category), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<HttpStatus> deleteCategoryById(@PathVariable int categoryId) {
        categoryRepository.deleteById(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/categories")
    public ResponseEntity<HttpStatus> deleteAllCategories() {
        categoryRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
