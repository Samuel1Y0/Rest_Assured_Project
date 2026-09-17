package Testcases;


import RestAssured.CountriesPojo;
import RestAssured.PlacePojo;
import Utilities.ReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

public class CountriesTest
{
    ExtentReports extent;
    ExtentTest test;
        RequestSpecification request;

        @BeforeClass
        public void beforeClass() {
            request = given().baseUri("https://api.zippopotam.us/")
                    .contentType(ContentType.JSON);
        }

    //Set the report before the execution
    @BeforeTest
    public void setup() {
        extent = ReportManager.getReporter();

    }

    @Test(priority = 1)
    //Validate country info
    public void getCountryInfo(){



        CountriesPojo countries = new CountriesPojo();
        PlacePojo place = new PlacePojo();

        countries.setCountryAbbreviation("US");
        countries.setPostalCode("00210");
        countries.setCountry("United States");

        place.setPlaceName("Portsmouth");
        place.setLatitude("43.0059");
        place.setLongitude("-71.0132");
        place.setState("New Hampshire");
        place.setStateAbbreviation("NH");


        Response response =    given()
                .spec(request)
                .when().get(countries.getCountryAbbreviation()+"/" + countries.getPostalCode())
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("country", equalTo(countries.getCountry()))
                .assertThat().body("'post code'", equalTo(countries.getPostalCode()))
                .assertThat().body("'country abbreviation'", equalTo(countries.getCountryAbbreviation()))
                .assertThat().body("places[0].'place name'", equalTo(place.getPlaceName()))
                .assertThat().body("places[0].longitude", equalTo(place.getLongitude()))
                .assertThat().body("places[0].latitude", equalTo(place.getLatitude()))
                .assertThat().body("places[0].state", equalTo(place.getState()))
                .assertThat().body("places[0].'state abbreviation'", equalTo(place.getStateAbbreviation()))
                .extract().response();
        test = extent.createTest("Test getCountry Info");
        test.info("Status code: " + response.statusCode());
        test.info("Request URL: " + "api.zippopotam.us/US/00210");
        test.info("Request Method: GET " );
        test.info("Response: " + response.asString());

    }

    @Test(priority = 2)
    //Negative test: invalid postal code should return 404
    public void getCountryInfoWithInvalidPostalCode() {


        CountriesPojo countries = new CountriesPojo();
        countries.setCountryAbbreviation("US");
        countries.setPostalCode("00000");

      Response response =   given()
                .spec(request)
                .when().get(countries.getCountryAbbreviation() + "/" + countries.getPostalCode())
                .then().log().all()
                .assertThat().statusCode(404)
                .extract().response();


        test = extent.createTest("Test getCountry Info With Invalid PostalCode");
        test.info("Status code: " + response.statusCode());
        test.info("Request URL: " + "api.zippopotam.us/US/00000");
        test.info("Request Method: GET " );
        test.info("Response: " + response.asString());

    }

    @Test(priority = 3)
    //Negative test: invalid country abbreviation should return 404
    public void getCountryInfoWithInvalidCountry() {


        CountriesPojo countries = new CountriesPojo();
        countries.setCountryAbbreviation("XX");
        countries.setPostalCode("00210");

        Response response = given()
                .spec(request)
                .when().get(countries.getCountryAbbreviation() + "/" + countries.getPostalCode())
                .then().log().all()
                .assertThat().statusCode(404)
                .extract().response();

        test = extent.createTest("Test getCountry Info With Invalid Country");
        test.info("Status code: " + response.statusCode());
        test.info("Request URL: " + "api.zippopotam.us/XX/00210");
        test.info("Request Method: GET ");
        test.info("Response: " + response.asString());
    }

    @Test(priority = 4)
    //Validate country abbreviation is case-insensitive
    public void getCountryInfoWithLowercaseAbbreviation() {


        CountriesPojo countries = new CountriesPojo();
        countries.setCountryAbbreviation("us");
        countries.setPostalCode("00210");
        countries.setCountry("United States");

        Response response =  given()
                .spec(request)
                .when().get(countries.getCountryAbbreviation() + "/" + countries.getPostalCode())
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("country", equalTo(countries.getCountry()))
                .extract().response();

        test = extent.createTest("Test get Country Info With Lowercase Abbreviation");
        test.info("Status code: " + response.statusCode());
        test.info("Request URL: " + "api.zippopotam.us/us/00210");
        test.info("Request Method: GET " );
        test.info("Response: " + response.asString());
    }

    @Test(priority = 5)
    //Validate response time within 2 seconds
    public void validateResponseTime() {


        CountriesPojo countries = new CountriesPojo();
        countries.setCountryAbbreviation("US");
        countries.setPostalCode("00210");

        Response response =    given()
                .spec(request)
                .when().get(countries.getCountryAbbreviation() + "/" + countries.getPostalCode())
                .then()
                .assertThat().statusCode(200)
                    .assertThat().time(lessThan(2000L))
                .extract().response();


        test = extent.createTest("Test validate Response Time");
        test.info("Status code: " + response.statusCode());
        test.info("Request URL: " + "api.zippopotam.us/US/00210");
        test.info("Request Method: GET " );
        test.info("Response: " + response.asString());

    }


    @AfterTest
    public void tearDown() {
        extent.flush(); // Close and save the report
    }


}
