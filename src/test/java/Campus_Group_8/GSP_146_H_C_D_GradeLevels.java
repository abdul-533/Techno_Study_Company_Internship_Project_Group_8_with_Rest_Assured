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

public class GSP_146_H_C_D_GradeLevels {
        Faker faker = new Faker();
        String gradeName;
        String shortName;
        String order;
        String gradeID;
        RequestSpecification reqSpec;

        @BeforeClass
        public void Setup() {
            baseURI = "https://test.mersys.io/";

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
                            //.log().all()
                            .statusCode(200)
                            .extract().response().getDetailedCookies();

            reqSpec = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .addCookies(cookies)
                    .build();
        }

        @Test
        public void gradeLevels() {

            Map<String, String> gradelevels = new HashMap<>();

            gradeName = faker.address().firstName() + faker.number().digits(6);
            gradelevels.put("name", gradeName);
            gradelevels.put("shortName", faker.number().digits(8));
            gradelevels.put("order", faker.number().digits(8));
            gradelevels.put("code", faker.number().digits(8));

            gradeID =
                    given()
                            .spec(reqSpec)
                            .body(gradelevels)
                            .log().body()

                            .when()
                            .post("/school-service/api/grade-levels")

                            .then()
                            .log().body()
                            .statusCode(201)
                            .extract().path("id");
            System.out.println("gradeID = " + gradeID);


        }

        @Test(dependsOnMethods = "gradeLevels")
        public void gradeLevelsNegative() {
            Map<String, String> gradeLevels = new HashMap<>();
            gradeLevels.put("name", gradeName);
            gradeLevels.put("shortName", faker.number().digits(8));
            gradeLevels.put("order", faker.number().digits(8));
            gradeLevels.put("code", faker.number().digits(8));

            given()
                    .spec(reqSpec)
                    .body(gradeLevels)
                    .log().body()

                    .when()
                    .post("/school-service/api/grade-levels")

                    .then()
                    .log().body()
                    .statusCode(400)
            ;


        }

        @Test(dependsOnMethods = "gradeLevelsNegative")
        public void updateGradeLevels() {
            Map<String, String> gradeLevels = new HashMap<>();
            gradeName = "grup8" + faker.address().firstName() + faker.number().digits(6);

            gradeLevels.put("name", gradeName);
            gradeLevels.put("shortName", faker.number().digits(8));
            gradeLevels.put("order", faker.number().digits(8));
            gradeLevels.put("code", faker.number().digits(8));

            given()
                    .spec(reqSpec)
                    .body(gradeLevels)
                    .log().body()

                    .when()
                    .post("/school-service/api/grade-levels")

                    .then()
                    .log().body()
                    .statusCode(201)


            ;


        }

        @Test(dependsOnMethods = "updateGradeLevels")
        public void deleteupdateGradeLevels() {
            given()
                    .spec(reqSpec)
                    .pathParam("gradeID", gradeID)
                    .log().uri()

                    .delete("/school-service/api/grade-levels/{gradeID}")

                    .then()
                    .log().body()
                    .statusCode(200);
        }

        @Test(dependsOnMethods = "deleteupdateGradeLevels")
        public void deleteupdateGradeLevelsNegative() {
            given()
                    .spec(reqSpec)
                    .pathParam("gradeID", gradeID)
                    .log().uri()

                    .delete("/school-service/api/grade-levels/{gradeID}")

                    .then()
                    .log().body()
                    .statusCode(400);

        }
    }

