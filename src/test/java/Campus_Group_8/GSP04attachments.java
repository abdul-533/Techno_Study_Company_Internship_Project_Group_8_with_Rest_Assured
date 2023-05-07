package Campus_Group_8;
import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class GSP04attachments {

    Faker faker=new Faker();
    String documentTypesID;

    String stage="EMPLOYMENT";

    String documentTypesName;
    String schoolId="6390f3207a3bcb6a7ac977f9";
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
    public void createDocument()  {


        Map<String,String> document=new HashMap<>();
        documentTypesName=faker.address().firstName()+faker.number().digits(5);
        document.put("name",documentTypesName);
        document.put("description",faker.address().lastName()+faker.number().digits(5));
        document.put("attachmentStages",stage);
        document.put("schoolId",schoolId);
        documentTypesID=
                given()
                        .spec(recSpec)
                        .body(document)
                        .log().body()

                        .when()
                        .post("/school-service/api/attachments/create")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");


        System.out.println("documentTypesID = " + documentTypesID);
    }

    @Test(dependsOnMethods = "createDocument")
    public void createDocumentNegative()  {

        Map<String,String> document=new HashMap<>();
        document.put("name",documentTypesName);
        document.put("description",faker.address().lastName()+faker.number().digits(5));
        document.put("attachmentStages",stage);
        document.put("schoolId",schoolId);
        given()
                .spec(recSpec)
                .body(document) // giden body
                .log().body() // giden body yi log olarak göster

                .when()
                .post("/school-service/api/attachments/create")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400)
                .body("message", containsString("already"))  // gelen body deki...
        ;
    }

    @Test(dependsOnMethods = "createDocumentNegative")
    public void updateDocument()  {

        Map<String,String> document=new HashMap<>();
        document.put("id",documentTypesID);
        document.put("attachmentStages",stage);
        documentTypesName="Group8"+faker.number().digits(7);
        document.put("name",documentTypesName);
        document.put("description",faker.address().lastName()+faker.number().digits(5));

        document.put("schoolId",schoolId);
        given()
                .spec(recSpec)
                .body(document) // giden body
                //.log().body() // giden body yi log olarak göster

                .when()
                .put("/school-service/api/attachments/create")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
                .body("name", equalTo(documentTypesName))
        ;
    }

    @Test(dependsOnMethods = "updateDocument")
    public void deleteDocument()  {

        given()
                .spec(recSpec)
                .pathParam("documentTypesID", documentTypesID)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/create/{documentTypesID}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(204)
        ;

    }

    @Test(dependsOnMethods = "deleteDocument")
    public void deleteDocumentNegative()  {

        given()
                .spec(recSpec)
                .pathParam("documentTypesID", documentTypesID)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/create/{documentTypesID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Country not found"))
        ;

    }

}