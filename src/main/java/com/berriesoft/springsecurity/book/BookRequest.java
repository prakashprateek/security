package com.berriesoft.springsecurity.book;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class BookRequest {

    private Integer id;
    private String author;
    private String isbn;
}
