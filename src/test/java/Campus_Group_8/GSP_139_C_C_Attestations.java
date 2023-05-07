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

public class GSP_139_C_C_Attestations {
    Faker faker = new Faker();
    String attestationName;
    String attestationID;
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
    public void createAttestations(){

        Map<String, String> attestations = new HashMap<>();
        attestationName = faker.name().fullName();
        attestations.put("name", attestationName);

        attestationID =
        given()
                .spec(reqSpec)
                .body(attestations)
                .log().body()

                .when()
                .post("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id");

        System.out.println("attestationID = " + attestationID);
    }

    @Test(dependsOnMethods = "createAttestations")
    public void createAttestationsNegative(){

        Map<String, String> attestations = new HashMap<>();
        attestations.put("name", attestationName);

        given()
                .spec(reqSpec)
                .body(attestations)
                .log().body()

                .when()
                .post("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"));

    }


    @Test(dependsOnMethods = "createAttestationsNegative")
    public void updateAttestations(){

        Map<String, String> attestations = new HashMap<>();
        attestations.put("id", attestationID);

        attestationName = faker.name().fullName() + faker.number().digits(1);
        attestations.put("name", attestationName);

        given()
                .spec(reqSpec)
                .body(attestations)
                .log().body()

                .when()
                .put("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(attestationName));
    }

    @Test(dependsOnMethods = "updateAttestations")
    public void deleteAttestations(){

        given()
                .spec(reqSpec)
                .pathParam("attestationID", attestationID)
                .log().uri()

                .when()
                .delete("/school-service/api/attestation/{attestationID}")

                .then()
                .log().body()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteAttestations")
    public void deleteAttestationsNegative(){

        given()
                .spec(reqSpec)
                .pathParam("attestationID", attestationID)
                .log().uri()

                .when()
                .delete("/school-service/api/attestation/{attestationID}")

                .then()
                .log().body()
                .statusCode(400);
    }
}
