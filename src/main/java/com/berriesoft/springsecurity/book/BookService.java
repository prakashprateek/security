package com.berriesoft.springsecurity.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.berriesoft.springsecurity.user.User;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    public void addBook(BookRequest request, User curUser) {
        var book = Book.builder()
                .id(request.getId())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .createDate(LocalDateTime.now())
                .createdBy(curUser.getId())
                .build();
        repository.save(book);
    }

    public List<Book> findAll() {
        return repository.findAll();
    }
}
