package com.jcq.webflux.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 测试Controller
 *
 * @author : jucunqi
 * @since : 2025/1/16
 */
@RestController
public class TestController {

    // webFlux向下兼容spring mvc
    @GetMapping("test")
    public String test() {
        return "Hello world";
    }

    // 推荐使用mono
    @GetMapping("monoTest")
    public Mono<String> monoTest() {
        return Mono.just("1");
    }

    // 测试sse 推送流，类似gpt那种不停推送数据方式
    @GetMapping(value = "sseTest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sseTest() {
        return Flux.range(1, 10)
                .delayElements(Duration.ofMillis(500))
                .map(a -> "ha->" + a);
    }

    // 测试完整ServerSentEvent api 推送流，类似gpt那种不停推送数据方式
    @GetMapping(value = "fullSseTest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> fullSseTest() {

        return Flux.range(1, 10)
                .delayElements(Duration.ofMillis(500))
                .map(a -> ServerSentEvent.builder("haha->" + a)
                        .event("test")
                        .comment("hehe")
                        .id(a + "")
                        .build()
                );
    }
}
