# 响应式编程

## 1、Reactor核心

### 前置知识

#### 1、Lambda



#### 2、Function

根据出参，入参分类

1、有入参，有出参 --> Function

```java
Function<String, Integer> function = a -> Integer.parseInt(a);
```

2、有入参，无出参

```java
Consumer<String> consumer = a -> System.out.println(a);
```

3、无入参，有出参

```java
Supplier<String> supplier = () -> UUID.randomUUID().toString();
```

4、无入参，无出参

```java
Runnable runnable = () -> System.out.println("xixi");	
```



#### 3、StreamAPI

流式操作，三大步骤

1、创建流

```java
Stream<Integer> integerStream = Stream.of(1, 2, 3);
Stream<Integer> stream = list.stream();
```

2、中间操作(intermediate operation)，可以有多个

```java
filter,map,mapToInt,mapToLong,mapToDouble,flatMap,flatMapToInt,flatMapToLong,flatMapToDouble,mapMulti,mapMultiToInt,mapMultiToLong,mapMultiToDouble,peek...
```

3、终止操作(terminal operation)，只能有一个

```java
forEach,forEachOrdered,toArray,toArray,reduce,collect,toList,min,max,count,anyMatch,findFirst,findAny...
```

**流式操作是否并发？**

```java
  // 流的三大部份
  // 1.创建流 2.N个中间操作 3.一个终止操作
  Stream<Integer> integerStream = Stream.of(1, 2, 3);
  Stream<Object> buildStream = Stream.builder().add(1).add(2).add(3).build();
  Stream<Object> concatStream = Stream.concat(integerStream, buildStream);
  Stream<Integer> stream = list.stream();

  List<Integer> resultList = new ArrayList<>();
  System.out.println("main线程： "+Thread.currentThread().getName());
  // 流是不是并发操作？  答：默认单线程，可以通过parallel开启多线程，但是如果开启多线程，则需要自身注意线程安全问题
  long count = list.stream()
          .parallel()     // 开启多线程 并发流
          .filter(i -> {
              // resultList.add(i);  // 开启多线程，不能这样写，要保证流里面的数据是无状态的，即流里面的数据只在流内部使用																			// 可以计算完成以后返回出去，但是不能在内部又引用外部的数据，可能会出现问题
              System.out.println("filter线程: " + Thread.currentThread().getName());
              return i > 2;
          })
          .count();

  System.out.println(resultList);
```

**注意：** 要保证流里面的数据是无状态的



**中间操作：**

- filter：过滤，挑出我们要的元素

    - takeWhile示例

  ```java
  List<Integer> collect = Stream.of(1, 2, 3, 4, 5, 6)
                  .filter(a -> a > 2)         // 无条件遍历
                  .toList();
  System.out.println(collect);
  
  List<Integer> collect1 = Stream.of(1, 2, 3, 4, 5, 6)
          .takeWhile(a -> a < 2)    // 当条件不满足时，直接返回
          .toList();
  System.out.println(collect1);
  ```



- map：映射，一对一映射

    - mapToInt，MapToDouble..

- flatMap: 打散、散列、展开，一对多映射

...



**终止操作：**

```java
forEach、forEachOrdered、toArray、reduce、collect、toList、min、
max、count、anyMatch、allMatch、noneMatch、findFirst、findAny、iterator
```



#### 4、Reactive Stream

**目的：**

通过全异步的方式，加缓冲区构建一个实时的数据流系统。

kafka，mq能构建大型的分布式响应系统，缺少本地化分布式响应系统方案

jvm推出Reactive Stream，让所有异步线程能够互相监听消息，处理消息，构建实时消息处理流



**Api Component：**

1、Publisher：发布者

2、Subscriber：订阅者

3、Processor：处理器



**响应式编程总结：**

1、**底层**：基于数据缓冲队列+消息驱动模型+异步回调机制

2、**编码**：流式编程+链式调用+生命式API

3、**效果**：优雅全异步+消息实时处理+高吞吐量+占用少量资源



**与传统写法对比：**

传统写法痛点：以前要做一个高并发系统：缓存、异步、队列，手动控制整个逻辑

现在：全自动控制整个逻辑



### Reactor

#### 1、快速上手

**介绍**

Reactor 是一个用于JVM的完全非阻塞的响应式编程框架，具备高效的需求管理（即对 “背压（backpressure）”的控制）能力。它与 Java 8 函数式 API 直接集成，比如 CompletableFuture， Stream， 以及 Duration。它提供了异步序列 API Flux（用于[N]个元素）和 Mono（用于 [0|1]个元素），并完全遵循和实现了“响应式扩展规范”（Reactive Extensions Specification）。

Reactor 的 reactor-ipc 组件还支持非阻塞的进程间通信（inter-process communication, IPC）。 Reactor IPC 为 HTTP（包括 Websockets）、TCP 和 UDP 提供了支持背压的网络引擎，从而适合 应用于微服务架构。并且完整支持响应式编解码（reactive encoding and decoding）。

依赖

```xml
<dependencyManagement> 
    <dependencies>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-bom</artifactId>
            <version>2023.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```xml
<dependencies>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-core</artifactId> 
        
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId> 
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 2、响应式编程

> 响应式编程是一种关注于**数据流（data streams）**和**变化传递（propagation of change）**的**异步编程**方式。 这意味着它可以用既有的编程语言表达静态（如数组）或动态（如事件源）的数据流。



#### 3、核心特性

**1、Mono和Flux**

Mono: 0|1 数据流

Flux: N数据流



响应式流：元素（内容） + 信号（完成/异常）；



**2、subscribe()**

> 自定义流的信号感知回调

```java
.subscribe(
     System.out::println         // 消费方法
     , throwable -> System.out.println(throwable.getMessage())    // 感知异常
     , () -> System.out.println("complete")      // 感知正常结束
);       // 流只有被订阅了才会执行，否则没有任何操作
```



> 自定义消费者

```java
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
```



**3、流的取消**

消费者调用 cancle() 取消流的订阅；



**4、自定义消费者**

推荐直接编写jdk自带的BaseSubscriber的实现类



**5、背压（back pressure）和请求重塑（reshape requests）**

> buffer

```java
/**
 * 缓冲区
 */
private static void bufferTest() {
    Flux.range(1, 10).buffer(3).subscribe(v -> System.out.println("v的类型：" + v.getClass() + "的值：" + v));
}
```

> limitRate

```java
/**
 * 测试limitRate
 */
private static void limitTest() {
    Flux.range(1,1000)
            .log()
            .limitRate(100)     // 一次预取100个元素  75%预取策略，第一次取100个如果75%已经处理，继续请求新的75%数据
            .subscribe(System.out::println);
}
```



**6、以编程方式创建序列-Sink**

> Sink.next
>
> Sink.complete
>
>

1、同步环境-generate

```java
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
```

2、多线程-create

```java
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
```



**7、handle**

> 自定义流中的处理规则

```java
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
```



**8、自定义线程调度**

> 响应式：响应式编程： 全异步、消息、事件回调

> 默认还是用当前线程，生成整个流、发布流、流操作

```java
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
```



**9、异常处理**

命令式编程：常见的错误处理方式

1. Catch and return a static default value. 捕获异常返回一个静态默认值

```java
try {
  return doSomethingDangerous(10);
}
catch (Throwable error) {
  return "RECOVERED";
}
```

> onErrorReturn: 实现上面效果，错误的时候返回一个值
> ●1、吃掉异常，消费者无异常感知
> ●2、返回一个兜底默认值
> ●3、流正常完成；

2. Catch and execute an alternative path with a fallback method.

吃掉异常，执行一个兜底方法；

```java
try {
  return doSomethingDangerous(10);
}
catch (Throwable error) {
  return doOtherthing(10);
}
```

> onErrorResume
> ●1、吃掉异常，消费者无异常感知
> ●2、调用一个兜底方法
> ●3、流正常完成

```java
Flux.just(1, 2, 0, 4)
        .map(i -> "100 / " + i + " = " + (100 / i)).onErrorResume(err -> Mono.just("哈哈-777"))
        .subscribe(v -> System.out.println("v = " + v),
                err -> System.out.println("err = " + err),
                () -> System.out.println("流结束"));
```



3. Catch and dynamically compute a fallback value. 捕获并动态计算一个返回值

根据错误返回一个新值

```java
try {
  Value v = erroringMethod();
  return MyWrapper.fromValue(v);
}
catch (Throwable error) {
  return MyWrapper.fromError(error);
}
```

```java
.onErrorResume(err -> Flux.error(new BusinessException(err.getMessage()+"：炸了")))
```



> ●1、吃掉异常，消费者有感知
>
> ●2、调用一个自定义方法
>
> ●3、流异常完成

4. Catch, wrap to a BusinessException, and re-throw.

捕获并包装成一个业务异常，并重新抛出

```java
try {
  return callExternalService(k);
}
catch (Throwable error) {
  throw new BusinessException("oops, SLA exceeded", error);
}
```

> 包装重新抛出异常:  推荐用  .onErrorMap
> ●1、吃掉异常，消费者有感知
> ●2、抛新异常
> ●3、流异常完成

```java
.onErrorResume(err -> Flux.error(new BusinessException(err.getMessage()+"：炸了")))

        Flux.just(1, 2, 0, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .onErrorMap(err-> new BusinessException(err.getMessage()+": 又炸了..."))
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流结束"));
```

5. Catch, log an error-specific message, and re-throw.

捕获异常，记录特殊的错误日志，重新抛出

```java	
try {
  return callExternalService(k);
}
catch (RuntimeException error) {
  //make a record of the error
  log("uh oh, falling back, service failed for key " + k);
  throw error;
}
```

```java
Flux.just(1, 2, 0, 4)
        .map(i -> "100 / " + i + " = " + (100 / i))
        .doOnError(err -> {
            System.out.println("err已被记录 = " + err);
        }).subscribe(v -> System.out.println("v = " + v),
                err -> System.out.println("err = " + err),
                () -> System.out.println("流结束"));
```

> ●异常被捕获、做自己的事情
> ●不影响异常继续顺着流水线传播
> ●1、不吃掉异常，只在异常发生的时候做一件事，消费者有感知

6. Use the finally block to clean up resources or a Java 7 “try-with-resource” construct.

```java
        Flux.just(1, 2, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .doOnError(err -> {
                    System.out.println("err已被记录 = " + err);
                })
                .doFinally(signalType -> {
                    System.out.println("流信号："+signalType);
                })
```

7. 忽略当前异常，仅通知记录，继续推进

```java
Flux.just(1,2,3,0,5)
        .map(i->10/i)
        .onErrorContinue((err,val)->{
            System.out.println("err = " + err);
            System.out.println("val = " + val);
            System.out.println("发现"+val+"有问题了，继续执行其他的，我会记录这个问题");
        }) //发生
        .subscribe(v-> System.out.println("v = " + v),
                err-> System.out.println("err = " + err));
```

**10、常用操作**

filter、flatMap、concatMap、flatMapMany、transform、defaultIfEmpty、switchIfEmpty、concat、concatWith、merge、mergeWith、mergeSequential、zip、zipWith...
