package com.jcq.r2dbc.test;

import com.jcq.r2dbc.eneity.Customers;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

/**
 * 测试R2DBC程序
 * @author : jucunqi
 * @since : 2025/1/17
 */
public class R2dbcTest {

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
                                .createStatement("select * from test where id = ?")
                                .bind(0, 1)
                                .execute()
                        ).flatMap(result ->
                                result.map(readable -> new Customers(((Integer) readable.get("customer_id")), Objects.requireNonNull(readable.get("customer_name")).toString())))
        ).subscribe(System.out::println);


        System.out.println(System.in.read());
    }
}
