package com.example.demo.post;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(AVAILABLE_PROCESSORS);

    private final PostClient postClient;


    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Post> all() {
        final List<CompletableFuture<Post>> completableFutures = IntStream.range(1, 20)
                .mapToObj(this::getPost)
                .collect(Collectors.toList());

        final CompletableFuture<List<Post>> allCompletableFuture = CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .thenApply(future -> completableFutures
                        .stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );

        try {
            return allCompletableFuture.thenApply(greets -> greets.stream()
                    .filter(Objects::nonNull).collect(Collectors.toList())).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private CompletableFuture<Post> getPost(int id) {
        return CompletableFuture.supplyAsync(() -> postClient.getPost(id), scheduledThreadPool).exceptionally(ex -> null);
    }
}
