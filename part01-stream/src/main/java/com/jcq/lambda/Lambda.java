package com.jcq.lambda;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description: 回去Lambda语法
 * @Author: jucunqi
 * @Date 2025/1/10
 */
public class Lambda {

    public static void main(String[] args) {

        functionTest();

    }

    private static void functionTest() {
        Function<String, Integer> function = a -> Integer.parseInt(a);

        Consumer<String> consumer = a -> System.out.println(a);

        Supplier<String> supplier = () -> UUID.randomUUID().toString();
        Runnable runnable = () -> System.out.println("xixi");
        runnable.run();
    }

    /**
     * @Description: 通过lambda排序
     * @Author: jucunqi
     * @Date 2025/1/10
     */
    private static void lambdaSort() {
        List<String> list = new ArrayList<>();
        list.add("b");
        list.add("a");
        list.add("d");
        list.add("c");

        // 匿名内部类排序
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        // lambda排序
        Collections.sort(list,(String a,String b) -> {return a.compareTo(b);});
        // 简写
        Collections.sort(list,(a,b) -> {return a.compareTo(b);});
        // 简写
        Collections.sort(list, (a, b) -> a.compareTo(b));
        // 简写
        Collections.sort(list, String::compareTo);
        System.out.println(list);
    }

    private static void simpleLambda() {
        // 最原始的调用
        UserInterface userImpl = new UserImpl();
        userImpl.junp();

        // 匿名内部类
        UserInterface userInterface = new UserInterface() {
            @Override
            public void junp() {
                System.out.println("通过匿名内部类的方式，跳了一下");
            }
        };
        userInterface.junp();

        // Lambda -> 接口里只能有一个未实现的方法 ，可以这么写
        UserInterface lambdaUserInterface = () -> { System.out.println("通过Lambda方式，跳了一下");};
        lambdaUserInterface.junp();
        // 简写
        UserInterface lambdaUserInterface2 = () -> System.out.println("通过Lambda方式，跳了一下");
    }
}

/**
 * @Description: 定义接口
 * @Author: jucunqi
 * @Date 2025/1/10
 */
interface UserInterface{

    /**
     * @Description: 跳方法
     * @Author: jucunqi
     * @Date 2025/1/10
     */
    void junp();

}

/**
 * @Description: 定义实现类
 * @Author: jucunqi
 * @Date 2025/1/10
 */
class UserImpl implements UserInterface{

    @Override
    public void junp() {
        System.out.println("跳了一下");
    }

}