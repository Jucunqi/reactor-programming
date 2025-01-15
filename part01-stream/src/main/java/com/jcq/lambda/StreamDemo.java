package com.jcq.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

/**
 * @Description: 流式操作
 * @Author: jucunqi
 * @Date 2025/1/10
 */
public class StreamDemo {



    public static void main(String[] args) {

        // 判断最大偶数
        // List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // for实现
        // implByFor(list);

        // stream实现
        // implByStream(list);

        // 测试optional
        // optionalTest();

        // 流的三大部份
        // streamTest(list);

        // stream应用
        // steamApply();

        List<Integer> collect = Stream.of(1, 2, 3, 4, 5, 6)
                .filter(a -> a > 2)         // 无条件遍历
                .toList();

        System.out.println(collect);

        List<Integer> collect1 = Stream.of(1, 2, 3, 4, 5, 6)
                .takeWhile(a -> a < 2)    // 当条件不满足时，直接返回
                .toList();
        System.out.println(collect1);

    }

    private static void steamApply() {
        List<Person> personList = List.of(new Person("Luca ju", Integer.parseInt("18"))
                , new Person("张 三", Integer.parseInt("20"))
                , new Person("里 斯", Integer.parseInt("22")));
        // 通过Stream 统计年龄大于19 ，然后姓名分离
        personList.stream()
                .filter(a -> a.getAge() > 19)
                .map(Person::getName)
                .flatMap(a -> {
                    String[] split = a.split(" ");
                    return Arrays.stream(split);
                })
                .forEach(System.out::println);
    }


    private static void streamTest(List<Integer> list) {
        // 1.创建流 2.N个中间操作 3.一个终止操作
        Stream<Integer> integerStream = Stream.of(1, 2, 3);
        Stream<Object> buildStream = Stream.builder().add(1).add(2).add(3).build();
        Stream<Object> concatStream = Stream.concat(integerStream, buildStream);
        Stream<Integer> stream = list.stream();

        List<Integer> resultList = new ArrayList<>();
        System.out.println("main线程： "+Thread.currentThread().getName());
        // 流是不是并发操作？  答：默认单线程，可以通过parallel开启多线程，但是如果开启多线程，则需要自身注意线程安全问题
        long count = list.stream()
                .parallel()     // 开启多线程
                .filter(i -> {
                    // resultList.add(i);      // 开启多线程，不能这样写，要保证流里面的数据是无状态的，即流里面的数据只在流内部使用，可以计算完成以后返回出去，但是不能在内部又引用外部的数据，可能会出现问题
                    System.out.println("filter线程: " + Thread.currentThread().getName());
                    return i > 2;
                })
                .count();

        System.out.println(resultList);
    }

    private static void optionalTest() {
        Optional<Integer> optionalI1 = Optional.of(1);
        // or
        optionalI1.or(() -> Optional.of(10)).ifPresent(System.out::println);
        // or else
        System.out.println(optionalI1.orElse(10));
        // flatMap
        optionalI1.flatMap(a -> Optional.of(a+5)).ifPresent(System.out::println);
    }

    private static void implByStream(List<Integer> list) {
        list.stream()                                   // 创建流
                .filter(a -> a % 2 == 0)                // intermediate operation. 中间操作
                .max(Integer::compareTo)                // terminal operation. 终止操作
                .ifPresent(System.out::println);
    }

    private static void implByFor(List<Integer> list) {
        // for循环写法
        int max = 0;
        for (Integer i : list) {
            if (i % 2 == 0) {
                max = max > i ? max : i;
            }
        }
        System.out.println(max);
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class Person{

    private String name;
    private Integer age;
}
