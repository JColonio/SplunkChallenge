package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class testing extends testBase {

    //Test basic URI request requirements (need: header & param)
    @Test
    public void testURIRequestReq() {

        RestAssured.baseURI = "https://splunk.mocklab.io/movies";

        //With the set baseURI, Assert that it fails
        //@formatter:off
        given().
        when().
        get().
        then().
            statusCode(404);
        //@formatter:on

        //Add header json, Assert that it fails
        //@formatter:off
        given().
            header("Accept", "application/json").
        when().
        get().
        then().
            statusCode(404);
        //@formatter:on

        //verify request is successful
        //@formatter:off
        given().
            param("q", "batman").
            header("Accept", "application/json").
        when().
        get().
        then().
            statusCode(200);
        //@formatter:on

        //verify request can be sent with both q and count
        //@formatter:off
        given().
            param("q", "batman").
            param("count", "3").
            header("Accept", "application/json").
        when().
        get().
        then().
            statusCode(200).
            assertThat().contentType("application/json");
        //@formatter:on
    }


    //Test URI request Param: see if count limits the number of records
    @Test
    public void testCountFunction() {

        int count = 3;
        RestAssured.baseURI = "https://splunk.mocklab.io/movies";

        //verify response count
        //Test fails: count Limits number of records in the response.
        //@formatter:off
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
        //@formatter:on
    }

    //Test if any PosterPath are null
    @Test
    public void testPosterPathNotNull() {

        Response response = getRequest("https://splunk.mocklab.io/movies");
        //Get list of all poster_path from response
        List<String> allPosterPath = response.jsonPath().getList("results.poster_path");
        //loop through and verify each poster_path is not null
        for (String posterPath : allPosterPath) {
            Assert.assertNotNull(posterPath, "poster_path is null");
        }
    }

    //Test if any PosterPath are duplicates
    @Test
    public void testDupPosterPath() {

        Set<String> dupList = new HashSet<String>();
        Set<String> uniqueList = new HashSet<String>();

        Response response = getRequest("https://splunk.mocklab.io/movies");
        //Get list of all poster_path from response
        List<String> allPosterPath = response.jsonPath().getList("results.poster_path");

        //loop through each PosterPath and assign to hashset uniqueList
        //  if the img url already exist then add value to dupList
        for (String posterPath : allPosterPath) {
            if (!uniqueList.add(posterPath)) {
                dupList.add(posterPath);
            }
        }

        //if dupList has atleast 1 value this test fails
        Assert.assertTrue(dupList.isEmpty(), "Duplicates were found");

    }

    @Test
    public void testMovieTitlePalindrome() {

        String[] words;
        Set<String> palindromList = new HashSet<String>();

        Response response = getRequest("https://splunk.mocklab.io/movies");
        //Get list of all poster_path from response
        List<String> allTitles = response.jsonPath().getList("results.title");

        //loop through each title
        for (String title : allTitles) {
            words = title.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[^\\w]", "");
                if (isPalindrome(words[i])) {
                    palindromList.add(words[i]);
                }
            }
        }
        Assert.assertTrue(palindromList.size() > 0, "Not one palindrome was found");
    }


    @Test
    public void testResultsSorting() {

        Response response = getRequest("https://splunk.mocklab.io/movies");
        //Get list of all poster_path from response
        List<String> allGenreIds = response.jsonPath().getList("results.genre_ids");
        List<String> allIds = response.jsonPath().getList("results.id");
        boolean result = false;

        for (int i = 1; i < allGenreIds.size(); i++) {
            String previous = Collections.singleton(allGenreIds.get(i - 1)).toString().replaceAll("\\[", "").replaceAll("\\]", "");
            ;
            String next = Collections.singleton(allGenreIds.get(i)).toString().replaceAll("\\[", "").replaceAll("\\]", "");
            ;

            //if the first point has values & the 2nd pointer is empty
            //      items are not sorted. Failed
            if ((previous.length() > 0) && (next.length() == 0)) {
                result = false;
                break;
            }
            //else if both are empty, check sorting by ids
            else if ((previous.length() == 0) && (next.length() == 0)) {
                if (allIds.get(i - 1).compareTo(allIds.get(i)) < 0) {
                    result = false;
                    break;
                } else {
                    result = true;
                }
            } else {
                result = true;
            }
        }

        Assert.assertTrue(result, "list is not sorted by gener_id or ids");

    }

    @Test
    public void testSumGenreIds() {

        Response response = getRequest("https://splunk.mocklab.io/movies");
        //Get list of all poster_path from response
        List<String> allGenreIds = response.jsonPath().getList("results.genre_ids");
        int count = 0;

        //loop through each title
        for (int i = 0; i < allGenreIds.size(); i++) {

            String[] cleanGenreids = Collections.singleton(allGenreIds.get(i)).toString().replaceAll("[\\[\\]\\(\\)]", "").split(",");

            int[] numbers = new int[cleanGenreids.length];

            for (int n = 0; n < numbers.length; n++) {
                if (!cleanGenreids[n].trim().isEmpty()) {
                    numbers[n] = Integer.parseInt(cleanGenreids[n].trim());
                }
            }

            int sumValue = getArraySum(numbers);

            if (sumValue > 400) {
                count += 1;
            }
        }

        Assert.assertTrue(count <= 7, "There is more than 7 movies with a genre_id sum of 400");
    }

}
