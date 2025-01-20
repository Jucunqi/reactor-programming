package com.jcq.r2dbc.repositories;

import com.jcq.r2dbc.eneity.TBook;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TBookRepository extends R2dbcRepository<TBook,Long> {

    @Query("select t1.*,t2.name from t_book t1 " +
            "left join t_author t2 on t1.author_id = t2.id " +
            "where t1.id = ?")
    Mono<TBook> queryAllData(Long bookId);
}
