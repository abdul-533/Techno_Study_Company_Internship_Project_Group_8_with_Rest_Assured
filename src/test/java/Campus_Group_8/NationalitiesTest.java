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
import static org.hamcrest.Matchers.equalTo;

public class NationalitiesTest {
    Faker faker = new Faker();
    String NationalityID;
    String NationalityName;
    RequestSpecification reqSpec;

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io";
        Map<String, Object> userCredential = new HashMap<>();
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
                        //  .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();
        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void AddNationalities() {
        Map<String, String> Nationality = new HashMap<>();
        NationalityName = faker.name().firstName() + faker.number().digits(3);
        Nationality.put("name", NationalityName);

        NationalityID = given()
                .spec(reqSpec)
                .body(Nationality)
                .log().body()
                .when()
                .post("/school-service/api/nationality")
                .then()
                .statusCode(201)
                .extract().path("id")
        ;
        System.out.println("NationalityID = " + NationalityID);
    }
    @Test(dependsOnMethods = "AddNationalities")
    public void AddNationalitiesNegative(){
        Map<String, String> Nationality = new HashMap<>();
        Nationality.put("name", NationalityName);
         given()
                .spec(reqSpec)
                .body(Nationality)
                .log().body()
                .when()
                .post("/school-service/api/nationality")
                .then()
                .statusCode(400)
        ;

    }

    @Test(dependsOnMethods ="AddNationalitiesNegative")
    public void EditNationalities(){

        Map<String,String> Nationality=new HashMap<>();
        Nationality.put("id",NationalityID);
        NationalityName=faker.name().firstName()+faker.number().digits(3);
        Nationality.put("name",NationalityName);
        given()
                .spec(reqSpec)
                .body(Nationality)
                .when()
                .put("/school-service/api/nationality")
                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(NationalityName))
        ;

    }
    @Test(dependsOnMethods = "EditNationalities")
    public void  DeleteNationalities(){

        given()
                .spec(reqSpec)
                .pathParam("NationalityID",NationalityID)

                .when()
                .delete("/school-service/api/nationality/{NationalityID}")

                .then()
                .log().body()
                .statusCode(200)
        ;

    }
    @Test(dependsOnMethods = "DeleteNationalities")
    public void DeleteCountryNegative() {
        given()
                .spec(reqSpec)
                .pathParam("NationalityID",NationalityID)
                .when()
                .delete("/school-service/api/nationality/{NationalityID}")
                .then()
                .log().body()
                .statusCode(400)
        ;

    }
}