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

public class GSP_1_A_A_Position_Categories {
    RequestSpecification recSpec;
    String positionCatID;
    String positionCatName;
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
    public void createPositionCategories() {

        Map<String, String> positionCat = new HashMap<>();
        positionCatName = faker.name().fullName() + faker.number().digits(3);
        positionCat.put("name", positionCatName);

        positionCatID =
                given()
                        .spec(recSpec)
                        .body(positionCat)

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        .statusCode(201)
                        .extract().path("id")

        ;
        //System.out.println("Discounts ID = " + discountsID);
        System.out.println("name = " + positionCatName);

    }

    @Test(dependsOnMethods = "createPositionCategories")
    public void createPositionCategoriesNegative() {
        Map<String, String> positionCat = new HashMap<>();
        positionCat.put("name", positionCatName);

        given()
                .spec(recSpec)
                .body(positionCat)

                .when()
                .post("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }

    @Test(dependsOnMethods = "createPositionCategoriesNegative")
    public void updatePositionCategories() {
        Map<String, String> positionCat = new HashMap<>();
        positionCatName = faker.name().fullName() + faker.number().digits(3);
        positionCat.put("name", positionCatName);
        positionCat.put("id", positionCatID);

        given()
                .spec(recSpec)
                .body(positionCat)

                .when()
                .put("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionCatName))
        ;

    }

    @Test(dependsOnMethods = "updatePositionCategories")
    public void deletePositionCategories() {

        given()
                .spec(recSpec)
                .pathParam("positionCatID", positionCatID)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionCatID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deletePositionCategories")
    public void deletePositionCategoriesNegative() {
        given()
                .spec(recSpec)
                .pathParam("positionCatID", positionCatID)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionCatID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
}
