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

public class TY_Departments {
    Faker faker = new Faker();
    String departmentName;
    String schoolID = "6390f3207a3bcb6a7ac977f9";
    String departmentID;
    RequestSpecification reqSpec;

    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io/";

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
    public void createDepartment(){

        Map<String, String> department = new HashMap<>();
        departmentName = faker.name().fullName();
        department.put("name", departmentName);
        department.put("code", faker.number().digits(3));
        department.put("active", "true");
        department.put("school", schoolID);

        departmentID =
        given()
                .spec(reqSpec)
                .body(department)
                .log().body()

                .when()
                .post("school-service/api/department")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id");

        System.out.println("departmentID = " + departmentID);

    }

    @Test(dependsOnMethods = "createDepartment")
    public void createDepartmentNegative(){

        Map<String, String> department = new HashMap<>();
        department.put("name", departmentName);
        department.put("code", faker.number().digits(3));
        department.put("active", "true");
        department.put("school", schoolID);

        given()
                .spec(reqSpec)
                .body(department)
                .log().body()

                .when()
                .post("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"));
    }

    @Test(dependsOnMethods = "createDepartmentNegative")
    public void updateDepartment(){

        Map<String, String> department = new HashMap<>();
        department.put("id", departmentID);

        departmentName = faker.name().fullName() + faker.number().digits(1);
        department.put("name", departmentName);
        department.put("code", faker.number().digits(3));
        department.put("active", "true");
        department.put("school", schoolID);

        given()
                .spec(reqSpec)
                .body(department)
                .log().body()

                .when()
                .put("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(departmentName));
    }

    @Test(dependsOnMethods = "updateDepartment")
    public void deleteDepartment(){

        given()
                .spec(reqSpec)
                .pathParam("departmentID", departmentID)
                .log().uri()

                .when()
                .delete("/school-service/api/department/{departmentID}")

                .then()
                .log().body()
                .statusCode(204);

    }


    @Test(dependsOnMethods = "deleteDepartment")
    public void deleteDepartmentNegative(){

        given()
                .spec(reqSpec)
                .pathParam("departmentID", departmentID)
                .log().uri()

                .when()
                .delete("/school-service/api/department/{departmentID}")

                .then()
                .log().body()
                .statusCode(400);

    }
}
