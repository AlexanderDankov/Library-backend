package com.simbirsoft.librarybackend.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;

public class Spec {

    public static RequestSpecification request = with()
            .baseUri("http://localhost:8080")
            .basePath("/api")
            .log().uri()
            .log().body()
            .contentType(ContentType.JSON);

    public static ResponseSpecification response = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .build();
}
