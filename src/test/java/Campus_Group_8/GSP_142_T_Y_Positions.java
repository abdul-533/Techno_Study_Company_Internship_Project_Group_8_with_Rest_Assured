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

public class GSP_142_T_Y_Positions {

    Faker faker=new Faker();
    String positionsID;
    String tenantId;
    String positionsName;
    RequestSpecification recSpec;

    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
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
                        .extract().response().getDetailedCookies()
                ;

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createPosition()  {

        Map<String,String> positions=new HashMap<>();
        positionsName=faker.address().firstName()+faker.number().digits(5);
        tenantId="6390ef53f697997914ec20c2";
        positions.put("name",positionsName);
        positions.put("shortName",faker.address().lastName()+faker.number().digits(5));
        positions.put("tenantId",tenantId);
        positionsID=
                given()
                        .spec(recSpec)
                        .body(positions)
                        .log().body()

                        .when()
                        .post("/school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");


        System.out.println("positionsID = " + positionsID);
    }

    @Test(dependsOnMethods = "createPosition")
    public void createPositionNegative()  {

        Map<String,String> positions=new HashMap<>();
        positions.put("name",positionsName);
        positions.put("shortName",faker.address().lastName()+faker.number().digits(5));
        positions.put("tenantId",tenantId);
        given()
                .spec(recSpec)
                .body(positions) // giden body
                .log().body() // giden body yi log olarak göster

                .when()
                .post("/school-service/api/employee-position")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400)
                .body("message", containsString("already"))  // gelen body deki...
        ;
    }

    @Test(dependsOnMethods = "createPositionNegative")
    public void updatePosition()  {

        Map<String,String> positions=new HashMap<>();
        positions.put("id",positionsID);

        positionsName="Group8"+faker.number().digits(7);
        positions.put("name",positionsName);
        positions.put("shortName",faker.address().lastName()+faker.number().digits(5));
        positions.put("tenantId",tenantId);
        given()
                .spec(recSpec)
                .body(positions) // giden body
                //.log().body() // giden body yi log olarak göster

                .when()
                .put("/school-service/api/employee-position")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
                .body("name", equalTo(positionsName))
        ;
    }

    @Test(dependsOnMethods = "updatePosition")
    public void deletePosition()  {

        given()
                .spec(recSpec)
                .pathParam("positionsID", positionsID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionsID}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(204)
        ;

    }

    @Test(dependsOnMethods = "deletePosition")
    public void deletePositionNegative()  {

        given()
                .spec(recSpec)
                .pathParam("positionsID", positionsID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionsID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Country not found"))
        ;

    }

}