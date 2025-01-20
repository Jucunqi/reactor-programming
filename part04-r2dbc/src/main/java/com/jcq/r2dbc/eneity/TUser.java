package com.jcq.r2dbc.eneity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("t_user")
public class TUser {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Date create_time;
    private Date update_time;
}
