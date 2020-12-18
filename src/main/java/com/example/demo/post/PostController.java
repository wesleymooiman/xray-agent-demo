package com.example.demo.post;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostClient postClient;

    @Autowired
    public PostController(PostClient postClient) {
        this.postClient = postClient;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<List<Post>> all() {
        final List<Future<List<Post>>> futureList = IntStream.range(1, 10)
                .mapToObj(i -> CompletableFuture.supplyAsync(postClient::getPosts))
                .collect(Collectors.toList());

        return futureList.stream().map(this::getResultFromFeature).collect(Collectors.toList());
    }

    private List<Post> getResultFromFeature(Future<List<Post>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return List.of();
        }
    }
}
