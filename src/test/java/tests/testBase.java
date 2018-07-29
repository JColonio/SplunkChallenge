package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class testBase {

    public static Response getRequest (String endpoint) {

        return given().
                    param("q", "batman").
                    header("Accept", "application/json").
                when().
                get(endpoint).
                then().
                    contentType(ContentType.JSON).
                extract().
                    response();
    }

}
