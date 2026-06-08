package br.com.alltallent.e2e;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class DashboardE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_BuscarDashboardAdmin() {
        String token = getAdminToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/dashboard")
            .then()
            .statusCode(200)
            .body("totalColaboradores", notNullValue());
    }

    @Test
    void testAlternativeFlow_BuscarDashboardComFiltro() {
        String token = getAdminToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/dashboard?codigoArea=1")
            .then()
            .statusCode(200);
    }

    @Test
    void testExceptionFlow_SemAutenticacao() {
        given()
            .when()
            .get("/api/dashboard")
            .then()
            .statusCode(403);
    }
}

