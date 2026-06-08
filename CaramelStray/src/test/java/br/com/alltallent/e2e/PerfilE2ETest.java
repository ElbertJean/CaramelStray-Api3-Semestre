package br.com.alltallent.e2e;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class PerfilE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_ListarPerfis() {
        String token = getColabToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/perfil")
            .then()
            .statusCode(200);
    }
}

