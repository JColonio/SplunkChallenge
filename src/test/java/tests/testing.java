package tests;

import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;


public class testing {

    //Test basic URI request requirements (need: header & param)
    @Test
    public void testURIRequestReq() {

        RestAssured.baseURI = "https://splunk.mocklab.io/movies";

        //With the set baseURI, Assert that it fails
        given().
                when().
                get().
                then().
                statusCode(404);

        //Add header json, Assert that it fails
        given().
                header("Accept", "application/json").
                when().
                get().
                then().
                statusCode(404);

        //Add header json and param, Assert that it passes
        //  and contenttype is json
        given().
                param("q", "batman").
                header("Accept", "application/json").
                when().
                get().
                then().
                statusCode(200).
                assertThat().contentType("\"application/json\"");

    }


}
