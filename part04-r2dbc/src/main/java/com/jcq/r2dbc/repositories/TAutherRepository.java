package com.jcq.r2dbc.repositories;

import com.jcq.r2dbc.eneity.TAuther;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface TAutherRepository extends R2dbcRepository<TAuther,Long> {

    // 根据命名实现sql
    Flux<TAuther> findAllByIdAndNameLike(Long id,String name);

    @Query("select * from t_author")
    Flux<TAuther> queryList();
}
