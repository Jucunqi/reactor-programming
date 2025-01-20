package com.jcq.r2dbc.eneity;

import lombok.Data;

import java.time.Instant;

@Data
public class TBook {

    private Long id;
    private String title;
    private Long authorId;
    private Instant publishTime;
    private TAuther tAuther;
}
