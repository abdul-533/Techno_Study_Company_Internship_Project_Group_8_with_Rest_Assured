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

public class D_Y_Fields {
    RequestSpecification reqSpec;
    Faker faker=new Faker();
    String fieldsName;
    String fieldsID;
    String type = "Integer";



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

        Map<String,String> fields = new HashMap<>();
        fieldsName = faker.address().cityName() + faker.number().digits(3);
        fields.put("name" , fieldsName);
        fields.put("code" ,faker.number().digits(4));
        fields.put("type", "INTEGER");
        fields.put("schoolId","6390f3207a3bcb6a7ac977f9");


        fieldsID =
                given()
                        .spec(reqSpec)
                        .body(fields)
                        .log().body()
                        .log().uri()
                        .when()
                        .post("/school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }
    @Test(dependsOnMethods = "createSubjectCategories")
    public void createSubjectCategoriesNegative() {

        Map<String,String> fields = new HashMap<>();
        fields.put("name", fieldsName);
        fields.put("code", faker.number().digits(4));
        fields.put("type", "INTEGER");
        fields.put("schoolId","6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(fields)
                // .log().body()

                .when()
                .post("school-service/api/entity-field")
                .then()
                .log().body()
                .statusCode(400)
                .body("message" , containsString("already"));
    }
    @Test(dependsOnMethods = "createSubjectCategoriesNegative")
    public void updateSubjectCategories(){
        Map<String,String> fields=new HashMap<>();
        fields.put("id", fieldsID);

        fieldsName = "kubilay"+faker.number().digits(3);
        fields.put("name", fieldsName);
        fields.put("code", faker.number().digits(5));
        fields.put("type", "STRING");
        fields.put("schoolId","6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(fields)

                .when()
                .put("school-service/api/entity-field")

                .then()
                .statusCode(200)
                .log().body()
                .body("name" , equalTo(fieldsName));
    }
    @Test(dependsOnMethods = "updateSubjectCategories")
    public void deleteSubjectCategories() {
        given()
                .spec(reqSpec)
                .pathParam("fieldsID",fieldsID)
                .log().uri()

                .when()
                .delete("/school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }
    @Test(dependsOnMethods = "deleteSubjectCategories")
    public void deleteSubjectCategoriesNegative() {
        given()
                .spec(reqSpec)
                .pathParam("fieldsID" , fieldsID)
                .log().uri()

                .when()
                .delete("school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body()
                .statusCode(400)

                .body("message",equalTo("EntityField not found"))
        ;
    }
}
