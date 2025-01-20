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



## 2、Spring Webflux



### 0、组件对比

| **API功能**  | **Servlet-阻塞式Web**                   | **WebFlux-响应式Web**                                        |
| ------------ | --------------------------------------- | ------------------------------------------------------------ |
| 前端控制器   | DispatcherServlet                       | DispatcherHandler                                            |
| 处理器       | Controller                              | WebHandler/Controller                                        |
| 请求、响应   | **ServletRequest**、**ServletResponse** | **ServerWebExchange：ServerHttpRequest、ServerHttpResponse** |
| 过滤器       | Filter（HttpFilter）                    | WebFilter                                                    |
| 异常处理器   | HandlerExceptionResolver                | DispatchExceptionHandler                                     |
| Web配置      | @EnableWebMvc                           | @EnableWebFlux                                               |
| 自定义配置   | WebMvcConfigurer                        | WebFluxConfigurer                                            |
| 返回结果     | 任意                                    | **Mono、Flux**、任意                                         |
| 发送REST请求 | RestTemplate                            | WebClient                                                    |

**Mono： 返回0|1 数据流**

**Flux：返回N数据流**



### 1、WebFlux

底层基于Netty实现的Web容器与请求/响应处理机制

参照：https://docs.spring.io/spring-framework/reference/6.0/web/webflux.html



### 2、引入

```java
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.1.6</version>
</parent>


<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>
```

> **Context 响应式上下文数据传递； 由下游传播给上游；**
>
> **以前： 浏览器 --> Controller --> Service --> Dao： 阻塞式编程**
>
> **现在： Dao（数据源查询对象【数据发布者】） --> Service --> Controller --> 浏览器： 响应式**

​

> **大数据流程： 从一个数据源拿到大量数据进行分析计算；**
>
> **ProductVistorDao.loadData()**
>
> ​                              **.distinct()**
>
> ​    **.map()**
>
> ​    **.filter()**
>
> ​     **.handle()**
>
> **.subscribe();**
>
> **;//加载最新的商品浏览数据**



### 3、Reactor Core

**1、HttpHandler、HttpServer**

```java
**
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
        System.in.read();
        System.out.println("服务器停止....");
    }
}
```



### 4、DispatcherHandler

> SpringMVC： DispatcherServlet；
>
> SpringWebFlux： DispatcherHandler

#### 1、请求处理流程

- HandlerMapping：**请求映射处理器**； 保存每个请求由哪个方法进行处理
- HandlerAdapter：**处理器适配器**；反射执行目标方法
- HandlerResultHandler：**处理器结果**处理器；



SpringMVC： DispatcherServlet 有一个 doDispatch() 方法，来处理所有请求；

WebFlux： DispatcherHandler 有一个 handle(ServerWebExchange exchange) 方法，来处理所有请求；

```java
public Mono<Void> handle(ServerWebExchange exchange) { 
		if (this.handlerMappings == null) {
			return createNotFoundError();
		}
		if (CorsUtils.isPreFlightRequest(exchange.getRequest())) {
			return handlePreFlight(exchange);
		}
		return Flux.fromIterable(this.handlerMappings) //拿到所有的 handlerMappings
				.concatMap(mapping -> mapping.getHandler(exchange)) //找每一个mapping看谁能处理请求
				.next() //直接触发获取元素； 拿到流的第一个元素； 找到第一个能处理这个请求的handlerAdapter
				.switchIfEmpty(createNotFoundError()) //如果没拿到这个元素，则响应404错误；
				.onErrorResume(ex -> handleDispatchError(exchange, ex)) //异常处理，一旦前面发生异常，调用处理异常
				.flatMap(handler -> handleRequestWith(exchange, handler)); //调用方法处理请求，得到响应结果
	}
```

- 1、请求和响应都封装在 ServerWebExchange 对象中，由handle方法进行处理
- 2、如果没有任何的请求映射器； 直接返回一个： 创建一个未找到的错误； 404； 返回Mono.error；终结流
- 3、跨域工具，是否跨域请求，跨域请求检查是否复杂跨域，需要预检请求；
- 4、Flux流式操作，先找到HandlerMapping，再获取handlerAdapter，再用Adapter处理请求，期间的错误由onErrorResume触发回调进行处理；

源码中的核心两个：

- **handleRequestWith**： 编写了handlerAdapter怎么处理请求
- **handleResult**： String、User、ServerSendEvent、Mono、Flux ...

concatMap： 先挨个元素变，然后把变的结果按照之前元素的顺序拼接成一个完整流

```java
private <R> Mono<R> createNotFoundError() {
		Exception ex = new ResponseStatusException(HttpStatus.NOT_FOUND);
		return Mono.error(ex);
	}
Mono.defer(() -> {
			Exception ex = new ResponseStatusException(HttpStatus.NOT_FOUND);
			return Mono.error(ex);
		}); //有订阅者，且流被激活后就动态调用这个方法； 延迟加载；

```



### 5、注解开发

#### 1、目标方法传参

https://docs.spring.io/spring-framework/reference/6.0/web/webflux/controller/ann-methods/arguments.html

| Controller method argument                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ServerWebExchange                                            | 封装了请求和响应对象的对象; 自定义获取数据、自定义响应       |
| ServerHttpRequest, ServerHttpResponse                        | 请求、响应                                                   |
| WebSession                                                   | 访问Session对象                                              |
| java.security.Principal                                      |                                                              |
| org.springframework.http.HttpMethod                          | 请求方式                                                     |
| java.util.Locale                                             | 国际化                                                       |
| java.util.TimeZone + java.time.ZoneId                        | 时区                                                         |
| @PathVariable                                                | 路径变量                                                     |
| @MatrixVariable                                              | 矩阵变量                                                     |
| @RequestParam                                                | 请求参数                                                     |
| @RequestHeader                                               | 请求头；                                                     |
| @CookieValue                                                 | 获取Cookie                                                   |
| @RequestBody                                                 | 获取请求体，Post、文件上传                                   |
| HttpEntity<B>                                                | 封装后的请求对象                                             |
| @RequestPart                                                 | 获取文件上传的数据 multipart/form-data.                      |
| java.util.Map, org.springframework.ui.Model, and org.springframework.ui.ModelMap. | Map、Model、ModelMap                                         |
| @ModelAttribute                                              |                                                              |
| Errors, BindingResult                                        | 数据校验，封装错误                                           |
| SessionStatus + class-level @SessionAttributes               |                                                              |
| UriComponentsBuilder                                         | For preparing a URL relative to the current request’s host, port, scheme, and context path. See [URI Links](https://docs.spring.io/spring-framework/reference/6.0/web/webflux/uri-building.html). |
| @SessionAttribute                                            |                                                              |
| @RequestAttribute                                            | 转发请求的请求域数据                                         |
| Any other argument                                           | 所有对象都能作为参数：1、基本类型 ，等于标注@RequestParam 2、对象类型，等于标注 @ModelAttribute |





#### 2、返回值写法

sse和websocket区别：

- SSE：单工；请求过去以后，等待服务端源源不断的数据
- websocket：双工： 连接建立后，可以任何交互；

| Controller method return value                               | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| @ResponseBody                                                | 把响应数据写出去，如果是对象，可以自动转为json               |
| HttpEntity<B>, ResponseEntity<B>                             | ResponseEntity：支持快捷自定义响应内容                       |
| HttpHeaders                                                  | 没有响应内容，只有响应头                                     |
| ErrorResponse                                                | 快速构建错误响应                                             |
| ProblemDetail                                                | SpringBoot3；                                                |
| String                                                       | 就是和以前的使用规则一样；forward: 转发到一个地址redirect: 重定向到一个地址配合模板引擎 |
| View                                                         | 直接返回视图对象                                             |
| java.util.Map, org.springframework.ui.Model                  | 以前一样                                                     |
| @ModelAttribute                                              | 以前一样                                                     |
| Rendering                                                    | 新版的页面跳转API； 不能标注 @ResponseBody 注解              |
| void                                                         | 仅代表响应完成信号                                           |
| Flux<ServerSentEvent>, Observable<ServerSentEvent>, or other reactive type | 使用  text/event-stream 完成SSE效果                          |
| Other return values                                          | 未在上述列表的其他返回值，都会当成给页面的数据；             |





### 6、文件上传

https://docs.spring.io/spring-framework/reference/6.0/web/webflux/controller/ann-methods/multipart-forms.html

```java
class MyForm {

	private String name;

	private MultipartFile file;

	// ...

}

@Controller
public class FileUploadController {

	@PostMapping("/form")
	public String handleFormUpload(MyForm form, BindingResult errors) {
		// ...
	}

}
```



现在

```java
@PostMapping("/")
public String handle(@RequestPart("meta-data") Part metadata, 
		@RequestPart("file-data") FilePart file) { 
	// ...
}
```



### 7、错误处理

```java
    @ExceptionHandler(ArithmeticException.class)
    public String error(ArithmeticException exception){
        System.out.println("发生了数学运算异常"+exception);

        //返回这些进行错误处理；
//        ProblemDetail：  建造者：声明式编程、链式调用
//        ErrorResponse ： 

        return "炸了，哈哈...";
    }
```



### 8、自定义Flux配置 WebFluxConfigurer

> 容器中注入这个类型的组件，重写底层逻辑

```java
@Configuration
public class MyWebConfiguration {

    //配置底层
    @Bean
    public WebFluxConfigurer webFluxConfigurer(){

        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .allowedOrigins("localhost");
            }
        };
    }
}
```



### 9、Filter

```java
@Component
public class MyWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        System.out.println("请求处理放行到目标方法之前...");
        Mono<Void> filter = chain.filter(exchange); //放行


        //流一旦经过某个操作就会变成新流

        Mono<Void> voidMono = filter.doOnError(err -> {
                    System.out.println("目标方法异常以后...");
                }) // 目标方法发生异常后做事
                .doFinally(signalType -> {
                    System.out.println("目标方法执行以后...");
                });// 目标方法执行之后

        //上面执行不花时间。
        return voidMono; //看清楚返回的是谁！！！
    }
}
```



## 3、R2DBC

#### 1、手写R2DBC

> 用法：
>
> 1、导入驱动: 导入连接池（[r2dbc-pool](https://github.com/r2dbc/r2dbc-pool)）、导入驱动（[r2dbc-mysql](https://github.com/asyncer-io/r2dbc-mysql) ）
>
> 2、使用驱动提供的API操作

**引入依赖**

```java
<dependency>
    <groupId>io.asyncer</groupId>
    <artifactId>r2dbc-mysql</artifactId>
    <version>1.0.5</version>
</dependency>
```

**手写代码**

```java
public static void main(String[] args) throws IOException {

    // 创建mysql配置
    MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
            .host("localhost")
            .port(3306)
            .username("root")
            .password("12345678")
            .database("test")
            .build();

    // 获取mysql连接工厂
    MySqlConnectionFactory factory = MySqlConnectionFactory.from(configuration);
    Mono.from(
            factory.create()
                    .flatMapMany(conn -> conn
                            .createStatement("select * from customers where customer_id = ?")
                            .bind(0, 1L)
                            .execute()
                    ).flatMap(result ->
                            result.map(readable -> {
                                return new Customers(((Integer) readable.get("customer_id")), Objects.requireNonNull(readable.get("customer_name")).toString());
                            }))
    ).subscribe(System.out::println);


    System.in.read();
}
```



#### 2、Spring Data R2DBC

> 提升生产力方式的 响应式数据库操作

##### **0、整合**

1、导入依赖

```java
        <!-- https://mvnrepository.com/artifact/io.asyncer/r2dbc-mysql -->
        <dependency>
            <groupId>io.asyncer</groupId>
            <artifactId>r2dbc-mysql</artifactId>
            <version>1.0.5</version>
        </dependency>
        <!--        响应式 Spring Data R2dbc-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        </dependency>
```

2、编写配置

```yml
spring:
  r2dbc:
    password: 123456
    username: root
    url: r2dbc:mysql://localhost:3306/test
    name: test
```

3、

```java
@Autowired
private R2dbcEntityTemplate template;

/**
 * 测试template  // 适合单表操作，复杂sql不好编写
 * @throws IOException io异常
 */
@Test
public void springDataR2dbcTest() throws IOException {

    // 1. 构建查询条件
    Criteria criteria = Criteria
            .empty()
            .and("project_leader")
            .is("1");
    // 构建Query对象
    Query query = Query
            .query(criteria);
    // 查询数据
    template.select(query, com.jcq.r2dbc.eneity.Test.class)
            .subscribe(test -> System.out.println("test = " + test));

    System.out.println(System.in.read());
}

@Autowired
private DatabaseClient databaseClient;

/**
 * 测试databaseClient // 更底层，适合复杂sql 比如join
 */
@Test
public void databaseClientTest() throws IOException {

    databaseClient.sql("select * from test where id in (?,?)")
            .bind(0, 1)
            .bind(1, 2)
            .fetch()        // 抓取数据
            .all()          // 抓取所有数据
            .map(a -> new com.jcq.r2dbc.eneity.Test(((Integer) a.get("id")),a.get("project_leader").toString()))
            .subscribe(a -> System.out.println("a = " + a));

    System.out.println(System.in.read());
}
```



##### 1、声明式接口：R2dbcRepository

**Repository接口**

```java
@Repository
public interface TAutherRepository extends R2dbcRepository<TAuther,Long> {

    // 根据命名实现sql
    Flux<TAuther> findAllByIdAndNameLike(Long id,String name);

    @Query("select * from t_author")
    Flux<TAuther> queryList();
}

```



**自定义Converter**

```java
@ReadingConverter  // 读取数据库的时候，吧row转成 TBook
public class TBookConverter implements Converter<Row, TBook> {
    @Override
    public TBook convert(Row source) {

        TBook tBook = new TBook();
        tBook.setId((Long) source.get("id"));
        tBook.setTitle((String) source.get("title"));
        tBook.setAuthorId((Long) source.get("author_id"));
        Object instance = source.get("publish_time");
        System.out.println(instance);
        ZonedDateTime instance1 = (ZonedDateTime) instance;
        tBook.setPublishTime(instance1.toInstant());

        TAuther tAuther = new TAuther();
        tAuther.setName(source.get("name", String.class));
        tBook.setTAuther(tAuther);
        return tBook;
    }
}
```



**配置生效**

```java
@Configuration
public class R2DbcConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public R2dbcCustomConversions r2dbcCustomConversions() {

        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new TBookConverter());
    }
}

```



#### 3、编程式组件

- R2dbcEntityTemplate
- DatabaseClient



#### 4、最佳实践

> 最佳实践：  提升生产效率的做法
>
> - 1、Spring Data R2DBC，基础的CRUD用 **R2dbcRepository** 提供好了
> - 2、自定义复杂的SQL（**单表**）： **@Query**；
> - 3、**多表查询复杂结果集**： **DatabaseClient** 自定义SQL及结果封装；
>
> - **@Query + 自定义 Converter 实现结果封装**
>
> **经验：**
>
> - **1-1:1-N 关联关系的封装都需要自定义结果集的方式**
>
> - **Spring Data R2DBC：**
>
> - **自定义Converter指定结果封装**
> - **DatabaseClient：贴近底层的操作进行封装; 见下面代码**
>
> - **MyBatis：  自定义 ResultMap 标签去来封装**



```java
databaseClient.sql("select b.*,t.name as name from t_book b " +
                "LEFT JOIN t_author t on b.author_id = t.id " +
                "WHERE b.id = ?")
        .bind(0, 1L)
        .fetch()
        .all()
        .map(row-> {
            String id = row.get("id").toString();
            String title = row.get("title").toString();
            String author_id = row.get("author_id").toString();
            String name = row.get("name").toString();
            TBook tBook = new TBook();

            tBook.setId(Long.parseLong(id));
            tBook.setTitle(title);

            TAuthor tAuthor = new TAuthor();
            tAuthor.setName(name);
            tAuthor.setId(Long.parseLong(author_id));

            tBook.setAuthor(tAuthor);

            return tBook;
        })
        .subscribe(tBook -> System.out.println("tBook = " + tBook));
```

