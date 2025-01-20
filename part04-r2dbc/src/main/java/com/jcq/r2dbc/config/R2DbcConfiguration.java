package com.jcq.r2dbc.config;

import com.jcq.r2dbc.convertor.TBookConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;

/**
 * R2DBC配置类
 * @author : jucunqi
 * @since : 2025/1/20
 */
@Configuration
public class R2DbcConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public R2dbcCustomConversions r2dbcCustomConversions() {

        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new TBookConverter());
    }
}
