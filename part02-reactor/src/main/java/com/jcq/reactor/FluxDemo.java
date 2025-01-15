package com.jcq.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;


/**
 * @Description: 测试Flux代码
 * @Author: jucunqi
 * @Date 2025/1/13
 */
public class FluxDemo {

    public static void main(String[] args) {

        // monoTest();
        // fluxTest();
        // doOnXXXTest();
        // testLog();
        // subscribeTest();
        // bufferTest();
        // limitTest();
        // generateTest();
        // createTest();
        // handleTest();
        // threadTest();
        // errorTest();
        // blockTest();
        // parallelTest();
    }

    /**
     * 并发流测试
     */
    private static void parallelTest() {
        Flux.range(1, 1000)
                .buffer(100)        // 100个为一组
                .parallel(8)        // 并发8个线程处理
                .runOn(Schedulers.newParallel("yy"))    // 指定线程池
                .log()
                .subscribe();
    }

    /**
     * 阻塞apiceui
     */
    private static void blockTest() {
        List<Integer> block = Flux.just(1, 2, 3, 4)
                .map(a -> a + 10)
                .collectList()
                .block();
        System.out.println(block);

        Integer integer = Flux.just(1, 2, 3, 4)
                .map(a -> a + 10)
                .blockFirst();

        System.out.println(integer);
    }

    /**
     * 测试异常处理
     */
    private static void errorTest() {
        Flux.just(1, 2, 0, 4)
                .map(a -> 100 / a)
                // .onErrorReturn(1)        // 吃掉异常，返回默认值，流正常完成
                // .onErrorContinue((throwable,value) -> System.out.println("异常： " + throwable.getMessage()))
                .onErrorResume(throwable -> Mono.just(10))  //  吃掉异常，执行其他操作，流正常完成
                .subscribe(v -> System.out.println("v = " + v), throwable -> System.out.println(throwable.getMessage()));
    }

    /**
     * 自定义线程测试
     */
    private static void threadTest() {

        // 响应式编程：全异步，消息，回调机制
        Schedulers.boundedElastic();    // 有界的，弹性线程池
        Schedulers.single();        // 单线程
        Schedulers.immediate();     // 都在同一个当前线程(默认)
        Scheduler scheduler = Schedulers.newParallel("my-parallel");
        Flux<Integer> flux = Flux.range(1, 10)
                .publishOn(scheduler)
                .log();

        flux.subscribe();
    }

    /**
     * handle自定义处理
     */
    private static void handleTest() {
        Flux.range(1, 10)
                .handle((value,sink) -> {
                    System.out.println("接收到value:" + value);
                    sink.next("haha_" + value);
                })
                .subscribe();
    }

    /**
     * 通过create创建序列，create适用与多线程环境，generate适用于单线程环境
     */
    private static void createTest() {
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next("2");
            }
        }).subscribe(System.out::println);
    }


    /**
     * 通过generate创建序列
     */
    private static void generateTest() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Flux.generate(() -> 0,              // 初始值
                        (i, a) -> {
                            a.next(list.get(i));                // 把元素放入通道
                            if (i == list.size() - 1) {
                                a.complete();                   // 完成
                            }
                            return ++i;                         // 下次回调的元素
                        }
                )
                .subscribe(System.out::println);
    }

    /**
     * 测试limitRate
     */
    private static void limitTest() {
        Flux.range(1, 1000)
                .log()
                .limitRate(100)     // 一次预取100个元素  75%预取策略，第一次取100个如果75%已经处理，继续请求新的75%数据
                .subscribe(System.out::println);
    }

    /**
     * 缓冲区
     */
    private static void bufferTest() {
        Flux.range(1, 10).buffer(3).subscribe(v -> System.out.println("v的类型：" + v.getClass() + "的值：" + v));
    }

    /**
     * subscribe api测试
     */
    private static void subscribeTest() {
        Flux.range(1, 10)
                .map(a -> {
                    if (a == 5) {
                        // throw new RuntimeException("5 error");
                    }
                    return "haha-" + a;
                })
                // .subscribe(
                //         System.out::println         // 消费方法
                //         , throwable -> System.out.println(throwable.getMessage())    // 感知异常
                //         , () -> System.out.println("complete")      // 感知正常结束
                // );       // 流只有被订阅了才会执行，否则没有任何操作
                .subscribe(new BaseSubscriber<String>() {       // 自定义消费者
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        System.out.println("被订阅");
                        requestUnbounded();
                    }

                    @Override
                    protected void hookOnNext(String value) {
                        System.out.println("下个元素");
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println("完成信号");
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        System.out.println("异常信号");
                    }

                    @Override
                    protected void hookOnCancel() {
                        System.out.println("结束信号");
                    }

                    @Override
                    protected void hookFinally(SignalType type) {
                        System.out.println("终止信号");
                    }
                });
    }

    /**
     * @Description: 测试log
     * @Author: jucunqi
     * @Date 2025/1/13
     */
    private static void testLog() {
        Flux.range(1, 7)
                .log()
                .filter(a -> a > 3)
                .map(a -> "haha_" + a)
                .subscribe(System.out::println);
    }

    /**
     * doOnXX  API测试
     */
    private static void doOnXXXTest() {
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5, 0, 9, 8)
                .doOnNext(a -> System.out.println("元素到达：" + a))
                .doOnComplete(() -> System.out.println("执行完毕"))
                .map(a -> 10 / a)
                .doOnError(throwable -> System.out.println("执行异常，原因：" + throwable.getMessage()))
                .map(a -> a * 5)
                .doOnNext(a -> System.out.println("元素到达：" + a));
        just.subscribe();
    }

    /**
     * Flux测试
     */
    private static void monoTest() {
        Mono<Integer> just = Mono.just(1);
        just.subscribe(System.out::println);
    }

    private static void fluxTest() throws InterruptedException {
        // Flux：N个数据
        // Flux<Integer> just = Flux.just(1, 2, 3, 4, 5);
        // just.subscribe(a -> System.out.println("just1: " + a));
        // just.subscribe(a -> System.out.println("just2: " + a));
        // 连接流api
        Flux.concat(Flux.just(1, 2, 3), Flux.just(7, 8, 9)).subscribe(System.out::println);
        // System.out.println("=========");
        // Flux.interval(Duration.ofSeconds(1)).subscribe(System.out::println);// 每秒自动生成数据

        Flux<Object> empty = Flux.empty().doOnComplete(() -> {
            System.out.println("emptyFlux running");
        });
        empty.subscribe(System.out::println);
        Thread.sleep(20000);


    }
}
