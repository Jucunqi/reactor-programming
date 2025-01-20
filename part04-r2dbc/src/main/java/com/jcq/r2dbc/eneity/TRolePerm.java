package com.jcq.r2dbc.eneity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("t_role_perm")
public class TRolePerm {

    private Long id;
    private Long role_id;
    private Long perm_id;
    private Date create_time;
    private Date update_time;
}
