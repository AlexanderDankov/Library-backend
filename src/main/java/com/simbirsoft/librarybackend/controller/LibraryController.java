package com.simbirsoft.librarybackend.controller;

import com.simbirsoft.librarybackend.domain.Author;
import com.simbirsoft.librarybackend.domain.AuthorInfo;
import com.simbirsoft.librarybackend.domain.DeleteAuthor;
import com.simbirsoft.librarybackend.exceptions.AlreadyExistException;
import com.simbirsoft.librarybackend.exceptions.NotFoundException;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LibraryController {

    private final List<Map<String, String>> booksList = new ArrayList<>() {{
        add(new HashMap<>() {{put("books", "Война и мир, Анна Каренина, Воскресение"); put("author_name", "Толстой Лев"); put("id", "1");}});
        add(new HashMap<>() {{put("books", "Евгений Онегин, Капитанская дочка, Руслан и Людмила"); put("author_name", "Пушкин Александр"); put("id", "2");}});
        add(new HashMap<>() {{put("books", "Мастер и Маргарита, Собачье сердце, Записки юного врача"); put("author_name", "Булгаков Михаил"); put("id", "3");}});
    }};

    @GetMapping("api/books/list")
    @ApiOperation("Список всех книг")
    public List<Map<String, String>> getBooksList() {
        return booksList;
    }

    @PostMapping("api/authors/add")
    @ApiOperation("Добавить нового автора")
    public Author addAuthor(@RequestBody AuthorInfo authorInfo) {
        if(booksList.stream()
                .anyMatch(list -> list.containsValue(authorInfo.getAuthorName()))) {
            throw new AlreadyExistException("Автор уже имеется в базе");
        } else {
            int id = booksList.size() + 1;
            booksList.add(new HashMap<>() {{
                put("books", authorInfo.getBooks());
                put("author_name", authorInfo.getAuthorName());
                put("id", String.valueOf(id));
            }});

            return Author.builder()
                    .authorName(authorInfo.getAuthorName())
                    .books(authorInfo.getBooks())
                    .id(id)
                    .build();
        }
    }

    @GetMapping("api/authors/{id}")
    @ApiOperation("Найти все книги автора")
    public Map<String, String> getBookByAuthor(@PathVariable String id) {
        return booksList.stream()
                .filter(list -> list.get("id").equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);

    }

    @DeleteMapping("api/authors/{id}")
    @ApiOperation("Удалить автора")
    public DeleteAuthor deleteAuthor(@PathVariable String id) {
        if (booksList.stream()
                .anyMatch(list -> list.containsValue(id))) {

            Map<String, String> bookByAuthor = getBookByAuthor(id);
            booksList.remove(bookByAuthor);

            return DeleteAuthor.builder()
                    .id(Integer.parseInt(id))
                    .success(true)
                    .message("Указанный автор успешно удален")
                    .build();
        } else {
            throw new NotFoundException();
        }
    }
}
