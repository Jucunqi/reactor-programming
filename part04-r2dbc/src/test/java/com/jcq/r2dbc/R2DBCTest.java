package com.jcq.r2dbc;

import com.jcq.r2dbc.eneity.Customers;
import com.jcq.r2dbc.repositories.TAutherRepository;
import com.jcq.r2dbc.repositories.TBookRepository;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

@SpringBootTest
public class R2DBCTest {

    /*
        最佳实践：
        1. Spring Data R2dbc ，基础的crud，使用repository就好了
        2. 自定义复杂sql（单表），使用@Query注解
        3. 多表查询复杂结果集，DatabaseClient 自定义sql及结果封装
     */
    @Test
    public void test() throws IOException {

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
                                .createStatement("select * from test where project_leader = ?")
                                .bind(0, "1")
                                .execute()
                        ).flatMap(result ->
                                result.map(readable -> new Customers(((Integer) readable.get("customer_id")), Objects.requireNonNull(readable.get("customer_name")).toString())))
        ).subscribe(System.out::println);

        int read = System.in.read();
        System.out.println(read);
    }

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

    @Autowired
    private TAutherRepository tAutherRepository;

    @Test
    public void tAutherRepositoryTest() throws IOException {
        // tAutherRepository.queryList().subscribe(a -> System.out.println("a = " + a));
        tAutherRepository.findAllByIdAndNameLike(1L,"张%").subscribe(a -> System.out.println("a = " + a));
        tAutherRepository.queryList().subscribe(a -> System.out.println("a = " + a));
        System.out.println(System.in.read());
    }

    @Autowired
    private TBookRepository tBookRepository;

    @Test
    public void tBookRepositoryTest() throws IOException {
        // tBookRepository.findAll().subscribe(a -> System.out.println("a = " + a));
        tBookRepository.queryAllData(1L).subscribe(a -> System.out.println("a = " + a));
        System.out.println(System.in.read());
    }
}
