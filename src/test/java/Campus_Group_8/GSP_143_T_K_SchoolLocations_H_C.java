package Campus_Group_8;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class GSP_143_T_K_SchoolLocations_H_C {
    Faker faker = new Faker();
    String locationsName;
    String type = "CLASS";
    String schoolID = "6390f3207a3bcb6a7ac977f9";
    String locationsID;
    RequestSpecification reqSpec;

    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential = new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
        given()
                .contentType(ContentType.JSON)
                .body(userCredential)

                .when()
                .post("/auth/login")

                .then()
                //.log().all()
                .statusCode(200)
                .extract().response().getDetailedCookies();

        reqSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createLocations(){

        Map<String,String> locations = new HashMap<>();
        locationsName = faker.name().fullName();
        locations.put("name",locationsName);
        locations.put("shortName", "tS");
        locations.put("capacity", faker.number().digits(3));
        locations.put("type", type);
        locations.put("school", schoolID);

        locationsID =
        given()
                .spec(reqSpec)
                .body(locations)
                .log().body()

                .when()
                .post("school-service/api/location")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id");

        System.out.println("locationsID = " + locationsID);
    }

    @Test(dependsOnMethods = "createLocations")
    public void createLocationsNegative(){

        Map<String,String> locations = new HashMap<>();
        locations.put("name",locationsName);
        locations.put("shortName", "tS");
        locations.put("capacity", faker.number().digits(3));
        locations.put("type", type);
        locations.put("school", schoolID);

        given()
                .spec(reqSpec)
                .body(locations)
                .log().body()

                .when()
                .post("school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"));
    }

    @Test(dependsOnMethods = "createLocationsNegative")
    public void updateLocations(){

        Map<String,String> locations = new HashMap<>();
        locations.put("id", locationsID);

        locationsName = faker.name().fullName() + faker.number().digits(1);
        locations.put("name",locationsName);
        locations.put("shortName", "tS");
        locations.put("capacity", faker.number().digits(3));
        locations.put("type", type);
        locations.put("school", schoolID);

        given()
                .spec(reqSpec)
                .body(locations)
                .log().body()

                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(locationsName));

    }

    @Test(dependsOnMethods = "updateLocations")
    public void deleteLocations(){

        given()
                .spec(reqSpec)
                .pathParam("locationsID", locationsID)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{locationsID}")

                .then()
                .log().body()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "deleteLocations")
    public void deleteLocationsNegative(){

        given()
                .spec(reqSpec)
                .pathParam("locationsID", locationsID)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{locationsID}")

                .then()
                .log().body()
                .statusCode(400);
    }
}
