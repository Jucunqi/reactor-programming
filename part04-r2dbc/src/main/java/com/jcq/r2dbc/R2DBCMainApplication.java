package com.jcq.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springBoot主程序
 * 自动配置：
 *  1、R2dbcAutoConfiguration: 主要配置工厂，连接池
 *  2、R2dbcDataAutoConfiguration:
 *      R2dbcEntityTemplate: 操作数据库的响应式客户端提供crud
 *      数据类型映射关系、转换器、自定义 MappingR2dbcConverter转换器组件
 *  3、R2dbcRepositoriesAutoConfiguration：开启spring data 声明式方式的crud
 *
 * @author : jucunqi
 * @since : 2025/1/17
 */
@SpringBootApplication
public class R2DBCMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(R2DBCMainApplication.class);
    }
}
