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

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GSP_11_A_A_Discounts {
    RequestSpecification recSpec;
    String discountsID;
    String description;
    Faker faker = new Faker();

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io";
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies =

                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }

    @Test
    public void createDiscounts() {

        Map<String, Object> discounts = new HashMap<>();
        description = faker.funnyName() + faker.number().digits(5);
        discounts.put("description", description);
        discounts.put("code", faker.code().imei() + faker.number().digits(5));
        discounts.put("priority", faker.number().digits(7));
        discounts.put("active", "true");

        discountsID =
                given()
                        .spec(recSpec)
                        .body(discounts)

                        .when()
                        .post("/school-service/api/discounts")

                        .then()
                        .statusCode(201)
                        .extract().path("id")

        ;
        //System.out.println("Discounts ID = " + discountsID);
        //  System.out.println("description = " + description);

    }

    @Test(dependsOnMethods = "createDiscounts")
    public void createDiscountsNegative() {
        Map<String, Object> discounts = new HashMap<>();
        discounts.put("description", description);
        discounts.put("code", faker.code().imei() + faker.number().digits(5));
        discounts.put("priority", faker.number().digits(7));
        discounts.put("active", "true");


        given()
                .spec(recSpec)
                .body(discounts)

                .when()
                .post("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }

    @Test(dependsOnMethods = "createDiscountsNegative")
    public void updateDiscounts() {
        Map<String, Object> discounts = new HashMap<>();
        description = "Bokser_area" + faker.funnyName() + faker.number().digits(2);
        discounts.put("id", discountsID);
        discounts.put("description", description);
        discounts.put("code", faker.code().imei() + faker.number().digits(5));
        discounts.put("priority", faker.number().digits(7));
        discounts.put("active", "true");


        given()
                .spec(recSpec)
                .body(discounts)

                .when()
                .put("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)
                .body("description", equalTo(description))
        ;

    }

    @Test(dependsOnMethods = "updateDiscounts")
    public void deleteDiscounts() {

        given()
                .spec(recSpec)
                .pathParam("discountsID", discountsID)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountsID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteDiscounts")
    public void deleteDiscountsNegative() {
        given()
                .spec(recSpec)
                .pathParam("discountsID", discountsID)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountsID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
}
