package com.jcq.r2dbc.eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

/**
 * test表实体类
 * @author : jucunqi
 * @since : 2025/1/17
 */
@Data
@Table("test")
@AllArgsConstructor
@NoArgsConstructor
public class Test {

    /**
     * 主键
     */
    private Integer id;
    /**
     * 项目经理
     */
    private String projectLeader;


}
