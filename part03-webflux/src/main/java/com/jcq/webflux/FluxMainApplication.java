package com.jcq.webflux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 测试webflux
 * @author : jucunqi
 * @since : 2025/1/16
 */
public class FluxMainApplication {

    public static void main(String[] args) throws IOException {
        HttpHandler handler = (ServerHttpRequest request, ServerHttpResponse response) -> {

            URI uri = request.getURI();
            System.out.println(Thread.currentThread() + "请求进来: " + uri);
            //编写请求处理的业务,给浏览器写一个内容 URL + "Hello~!"
            // response.getHeaders(); //获取响应头
            // response.getCookies(); //获取Cookie
            // response.getStatusCode(); //获取响应状态码；
            // response.bufferFactory(); //buffer工厂
            // response.writeWith() //把xxx写出去
            // response.setComplete(); //响应结束

            //创建 响应数据的 DataBuffer
            DataBufferFactory factory = response.bufferFactory();
            String result = "Hello world";
            //数据Buffer
            DataBuffer buffer = factory.wrap(result.getBytes(StandardCharsets.UTF_8));
            // 需要一个 DataBuffer 的发布者
            return response.writeWith(Flux.just(buffer));
        };

        //2、启动一个服务器，监听8080端口，接受数据，拿到数据交给 HttpHandler 进行请求处理
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);

        //3、启动Netty服务器
        HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(adapter) //用指定的处理器处理请求
                .bindNow(); //现在就绑定

        System.out.println("服务器启动完成....监听8080，接受请求");
        int read = System.in.read();
        System.out.println(read);
        System.out.println("服务器停止....");
    }
}
