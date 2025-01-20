package com.jcq.r2dbc.eneity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("t_author")
public class TAuther {
    private Long id;
    private String  name;
}
