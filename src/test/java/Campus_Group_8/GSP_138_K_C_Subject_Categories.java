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

public class GSP_138_K_C_Subject_Categories {

    RequestSpecification reqSpec;
    Faker faker=new Faker();
    String categoriesName;
    String categoriesID;

    @BeforeClass
    @Test
    public void Login() {
        baseURI = "https://test.mersys.io/";

        Map<String,String > userCredential = new HashMap<>();
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
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }
    @Test
    public void createSubjectCategories() {

        Map<String,String> categories = new HashMap<>();
        categoriesName = faker.address().cityName() + faker.number().digits(3);
        categories.put("name" , categoriesName);
        categories.put("code" ,faker.number().digits(4));

        categoriesID =
        given()
                .spec(reqSpec)
                .body(categories)
                .log().body()
                .when()
                .post("school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")
                ;
    }
     @Test(dependsOnMethods = "createSubjectCategories")
    public void createSubjectCategoriesNegative() {
         Map<String,String> categories = new HashMap<>();
         categories.put("name", categoriesName);
         categories.put("code", faker.number().digits(4));

        given()
                .spec(reqSpec)
                .body(categories)
                // .log().body()

                .when()
                .post("school-service/api/subject-categories")
                .then()
                .log().body()
                .statusCode(400)
                .body("message" , containsString("already"));
     }
     @Test(dependsOnMethods = "createSubjectCategoriesNegative")
    public void updateSubjectCategories(){
         Map<String,String> categories=new HashMap<>();
         categories.put("id", categoriesID);

         categoriesName = "kubilay culha" + faker.number().digits(3);
         categories.put("name", categoriesName);
         categories.put("code", faker.number().digits(5));

        given()
                .spec(reqSpec)
                .body(categories)

                .when()
                .put("/school-service/api/subject-categories")

                .then()
                .statusCode(200)
                .log().body()
                .body("name" , equalTo(categoriesName));
     }
     @Test(dependsOnMethods = "updateSubjectCategories")
    public void deleteSubjectCategories() {
         given()
                 .spec(reqSpec)
                 .pathParam("categoriesID",categoriesID)
                 .log().uri()

                 .when()
                 .delete("/school-service/api/subject-categories/{categoriesID}")

                 .then()
                 .statusCode(200)
                 ;
     }
     @Test(dependsOnMethods = "deleteSubjectCategories")
    public void deleteSubjectCategoriesNegative() {
        given()
                .spec(reqSpec)
                .pathParam("categoriesID" , categoriesID)
                .log().uri()

                .when()
                .delete("/school-service/api/subject-categories/{categoriesID}")

                .then()
                .log().body()
                .statusCode(400)

                // .body("message",equalTo("SubjectCategory not found"))
                ;
     }
}