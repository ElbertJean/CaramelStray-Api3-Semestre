package br.com.alltallent.e2e;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class AreaE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_ListarAreas() {
        String token = getColabToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/area")
            .then()
            .statusCode(200);
    }
}

