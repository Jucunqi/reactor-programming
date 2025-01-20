package com.jcq.r2dbc.eneity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("t_perm")
public class TPerm {
    private Long id;
    private String value;
    private String uri;
    private String description;
    private Date create_time;
    private Date update_time;

}
