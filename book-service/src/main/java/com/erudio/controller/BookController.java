package com.erudio.controller;

import com.erudio.BookRepository;
import com.erudio.CambioProxy;
import com.erudio.model.Book;
import com.erudio.response.Cambio;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("book-service")
public class BookController {

    private final Environment environment;
    private final BookRepository repository;
    private final CambioProxy proxy;

    @GetMapping("{bookId}/{currency}")
    public Book findBookById(@PathVariable("bookId") Long id,
                             @PathVariable("currency") String currency) {

        var book = repository.getReferenceById(id);
        if (book == null) throw new RuntimeException("Book not found " + id);

        var cambio = proxy.getCambio(book.getPrice(), "USD", currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment(port + " Feign");
        book.setPrice(cambio.getConvertedValue());
        return book;
    }

    /* Connect to the cambio-service manually, through RestTemplate. Not recommended because is not flexible.

    @GetMapping("{bookId}/{currency}")
    public Book findBookById(@PathVariable("bookId") Long id,
                             @PathVariable("currency") String currency) {

        var book = repository.getReferenceById(id);
        if (book == null) throw new RuntimeException("Book not found " + id);

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", book.getPrice().toString());
        params.put("from", "USD");
        params.put("to", currency);
        var response = new RestTemplate()
                .getForEntity("http://localhost:8000/cambio-service/{amount}/{from}/{to}", Cambio.class,
                        params);
        var cambio = response.getBody();

        var port = environment.getProperty("local.server.port");
        book.setEnvironment(port);
        book.setPrice(cambio.getConvertedValue());
        return book;
    }*/
}
