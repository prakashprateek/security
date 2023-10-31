package com.berriesoft.springsecurity.book;

import com.berriesoft.springsecurity.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;
    Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody BookRequest request
    ) {
        User curUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("in Post book request");
        logger.debug(request.toString());
        service.addBook(request, curUser);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<Book>> findAllBooks() {
        return ResponseEntity.ok(service.findAll());
    }
}
