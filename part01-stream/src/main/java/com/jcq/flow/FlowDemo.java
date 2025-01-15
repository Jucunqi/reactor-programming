package com.jcq.flow;

import java.io.FileNotFoundException;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * @Description: 理解jdk9中flow类‘
 * @Author: jucunqi
 * @Date 2025/1/13
 */
public class FlowDemo {

    /**
     * @Description: 自定义处理器 ，继承SubmissionPublisher承担发布者角色，实现Processor接口，重写订阅者逻辑
     * 目的：对上游数据处理，起到承上启下的作用
     * @Author: jucunqi
     * @Date 2025/1/13
     */
    static class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {

        private Flow.Subscription subscription;
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            System.out.println("processor确认绑定关系");
            subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            System.out.println("processor接收到消息");
            // 消息处理
            item = item + " 哈哈";
            // 再次将数据发出
            submit(item);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    }

    /**
     * 四大模型
     * 1. Publisher：发布者
     * 2. subscriber：订阅者
     * 3. Subscription：绑定关系
     * 4. Processor：处理器
     */
    public static void main(String[] args) throws FileNotFoundException {

        // 创建一个发布者，使用匿名内部类方式
        /*Flow.Publisher publisher = new Flow.Publisher() {
            @Override
            public void subscribe(Flow.Subscriber subscriber) {

            }
        }*/

        // 创建一个发布者，使用jdk提供的
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // 创建自定义处理器
        MyProcessor processor1 = new MyProcessor();
        MyProcessor processor2 = new MyProcessor();
        MyProcessor processor3 = new MyProcessor();
        try (publisher;processor3;processor2;processor1) {

            // 创建一个订阅者
            Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {

                private Flow.Subscription subscription;

                @Override  // 当绑定订阅关系时会执行
                public void onSubscribe(Flow.Subscription subscription) {
                    // 绑定关系
                    this.subscription = subscription;
                    // 表示接收1个消息
                    subscription.request(1);
                    System.out.println("线程： " + Thread.currentThread().getName() + "确定绑定关系");
                }

                @Override   // 当接收下一个消息时执行
                public void onNext(String item) {
                    System.out.println("线程： " + Thread.currentThread().getName() + "接收到消息：" + item);
                    // 继续请求下一个数据
                    subscription.request(1);
                }

                @Override  // 当接收到异常信号的时候执行
                public void onError(Throwable throwable) {
                    System.out.println("线程： " + Thread.currentThread().getName() + "接收到异常信号");
                }

                @Override  // 当接收到完成信号时执行
                public void onComplete() {
                    System.out.println("线程： " + Thread.currentThread().getName() + "接收到完成信号");
                }
            };

            // 绑定订阅关系
            // publisher.subscribe(subscriber);

            // 绑定带有处理器的绑定关系
            publisher.subscribe(processor1);
            processor1.subscribe(processor2);
            processor2.subscribe(processor3);
            processor3.subscribe(subscriber);

            // 发布数据
            System.out.println("线程： " + Thread.currentThread().getName() + "开始发布消息");
            for (int i = 0; i < 10; i++) {
                publisher.submit("item-" + i);
            }
            System.out.println("线程： " + Thread.currentThread().getName() + "结束发布消息");

            // 线程阻塞
            Thread.sleep(30000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
