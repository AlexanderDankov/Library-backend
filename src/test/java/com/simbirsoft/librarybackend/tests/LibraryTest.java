package com.simbirsoft.librarybackend.tests;


import com.simbirsoft.librarybackend.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.simbirsoft.librarybackend.spec.Spec.request;
import static com.simbirsoft.librarybackend.spec.Spec.response;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LibraryTest {

    public static final String
            author = "Пушкин Александр",
            books = "Евгений Онегин, Капитанская дочка, Руслан и Людмила",
            id = "2";

    @Test
    @DisplayName("Добавление нового автора и проверка его наличия в общем списке")
    void successfulAddAndGetAuthorInList() {
        String author = "Гоголь Николай";
        String books = "Мертвые души, Ревизор";

        Author addAuthor =
                given()
                        .spec(request)
                        .body("{\"author_name\":\"" + author + "\", \"books\":\"" + books + "\"}")
                        .when()
                        .post("/authors/add")
                        .then()
                        .log().body()
                        .spec(response)
                        .extract()
                        .body().as(Author.class);



        assertAll("checkAddAuthorResult",
                () -> assertThat(addAuthor.getAuthorName()).isEqualTo(author),
                () -> assertThat(addAuthor.getBooks()).isEqualTo(books),
                () -> assertThat(addAuthor.getId()).isNotZero()
        );


        given()
                .spec(request)
                .when()
                .get("/books/list")
                .then()
                .log().body()
                .spec(response)
                .body("findAll{it}.author_name.flatten()", hasItem(author))
                .body("findAll{it}.books.flatten()", hasItem(books));
    }


    @Test
    @DisplayName("Добавление уже существующего автора")
    void addNewAuthorWhenAlreadyExist() {
        given()
                .spec(request)
                .body("{\"author_name\":\"" + author + "\", \"books\":\"" + books + "\"}")
                .when()
                .post("/authors/add")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", is("Автор уже имеется в базе"));

    }

    @Test
    @DisplayName("Поиск автора по id")
    void successfulGetAuthorById() {
        Author getAuthor =
                given()
                        .spec(request)
                        .when()
                        .get("/authors/" + id)
                        .then()
                        .log().body()
                        .spec(response)
                        .extract()
                        .body().as(Author.class);


        assertAll("checkGetAuthorByIdResult",
                () -> assertThat(getAuthor.getAuthorName()).isEqualTo(author),
                () -> assertThat(getAuthor.getBooks()).isEqualTo(books),
                () -> assertThat(getAuthor.getId()).isEqualTo(Integer.parseInt(id))
        );
    }

    @Test
    @DisplayName("Поиск автора по несуществующему id")
    void getAuthorByIdWhenIdNotFound() {
        given()
                .spec(request)
                .when()
                .get("/authors/500")
                .then()
                .log().body()
                .statusCode(404)
                .body("status", is(404))
                .body("message", is("Автор не найден"));
    }

    @Test
    @DisplayName("Успешное удаление автора")
    void successfulDeleteAuthor() {
        String id = "1";
        String author = "Толстой Лев";
        String books = "Война и мир, Анна Каренина, Воскресение";

        given()
                .spec(request)
                .when()
                .delete("/authors/" + id)
                .then()
                .log().body()
                .statusCode(200)
                .body("id", is(Integer.parseInt(id)))
                .body("success", is(true))
                .body("message", is("Указанный автор успешно удален"));

        given()
                .spec(request)
                .when()
                .get("/books/list")
                .then()
                .log().body()
                .spec(response)
                .body("findAll{it}.author_name.flatten()", is(not(hasItem(author))))
                .body("findAll{it}.books.flatten()", is(not(hasItem(books))));

    }

    @Test
    @DisplayName("Удаление автора по несуществующему id")
    void deleteAuthorWhenIdNotFound() {
        given()
                .spec(request)
                .when()
                .delete("/authors/500")
                .then()
                .log().body()
                .statusCode(404)
                .body("status", is(404))
                .body("message", is("Автор не найден"));
    }
}
