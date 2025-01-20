package com.jcq.r2dbc.convertor;

import com.jcq.r2dbc.eneity.TAuther;
import com.jcq.r2dbc.eneity.TBook;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import java.time.ZonedDateTime;

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
        assert instance1 != null;
        tBook.setPublishTime(instance1.toInstant());

        TAuther tAuther = new TAuther();
        tAuther.setName(source.get("name", String.class));
        tBook.setTAuther(tAuther);
        return tBook;
    }
}
