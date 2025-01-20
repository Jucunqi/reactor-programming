package com.jcq.r2dbc.eneity;

import lombok.Data;

import java.util.Date;

@Data
public class TUserRole {

    private Long id;
    private Long user_id;
    private Long role_id;
    private Date create_time;
    private Date update_time;
}
