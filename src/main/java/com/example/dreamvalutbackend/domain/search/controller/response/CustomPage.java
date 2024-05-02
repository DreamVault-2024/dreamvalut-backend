package com.example.dreamvalutbackend.domain.search.controller.response;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPage<T> {

    private List<T> content;
    private Pageable pageable;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private int size;
    private int number;
    private Sort sort;
    private boolean first;
    private int numberOfElements;
    private boolean empty;

    public CustomPage(List<T> content, Pageable pageable, long totalElements) {
        this.content = content;
        this.pageable = pageable;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        this.last = pageable.getPageNumber() == this.totalPages - 1;
        this.size = pageable.getPageSize();
        this.number = pageable.getPageNumber();
        this.sort = pageable.getSort();
        this.first = pageable.getPageNumber() == 0;
        this.numberOfElements = content.size();
        this.empty = content.isEmpty();
    }
}
