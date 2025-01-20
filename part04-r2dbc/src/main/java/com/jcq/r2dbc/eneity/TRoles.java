package com.jcq.r2dbc.eneity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("t_roles")
public class TRoles {
    private Long id;
    private String name;
    private String value;
    private Date create_time;
    private Date update_time;
}
