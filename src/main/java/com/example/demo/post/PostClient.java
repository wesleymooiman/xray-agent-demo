package com.example.demo.post;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "https://jsonplaceholder.typicode.com", name = "post")
public interface PostClient {

    @GetMapping("/posts")
    List<Post> getPosts();

}

