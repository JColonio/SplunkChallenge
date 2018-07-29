package tests;

import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;


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

        //verify request is successful
        given().
            param("q", "batman").
            header("Accept", "application/json").
        when().
        get().
        then().
            statusCode(200);

        //verify request can be sent with both q and count
        given().
            param("q", "batman").
            param("count", "3").
            header("Accept", "application/json").
        when().
        get().
        then().
            statusCode(200).
            assertThat().contentType("application/json");
    }


    //Test basic URI request requirements (need: header & param)
    @Test
    public void testCountFunction() {

        int count = 3;
        RestAssured.baseURI = "https://splunk.mocklab.io/movies";

        //verify response count
        //Test fails: count Limits number of records in the response.
        given().
            param("q", "batman").
            param("count", count).
            header("Accept", "application/json").
        when().
        get().
        then().
            statusCode(200).
            assertThat().contentType("application/json").
            body("results.size()", equalTo(count));
    }


}
