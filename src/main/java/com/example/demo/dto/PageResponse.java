package com.example.demo.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long total;

    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> resp = new PageResponse<>();
        resp.setItems(page.getContent());
        resp.setPage(page.getNumber());
        resp.setSize(page.getSize());
        resp.setTotal(page.getTotalElements());
        return resp;
    }
}