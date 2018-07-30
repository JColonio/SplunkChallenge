package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class testBase {

    public static Response getRequest(String endpoint) {

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

    public boolean isPalindrome(String text) {

        int length = text.length();

        // If the string only has 1 char or is empty
        //      single letters, numbers, and empty strings are Palindrome
        // If word has already been processed to this point,
        //      all letters match and only one left
        if (length < 2)
            return true;
        else {
            // Check opposite ends of the string for equality
            if (text.charAt(0) != text.charAt(length - 1))
                return false;
                // Function call for string with the two ends snipped off
            else
                return isPalindrome(text.substring(1, length - 1));
        }
    }

    public int getArraySum(int[] array) {
        int sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum;
    }

}
