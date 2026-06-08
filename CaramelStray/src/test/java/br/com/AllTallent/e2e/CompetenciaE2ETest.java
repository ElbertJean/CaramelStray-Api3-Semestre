package br.com.alltallent.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class CompetenciaE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_CriarEBuscarCompetencia() {
        String token = getAdminToken();
        String compName = "Competencia " + System.currentTimeMillis();

        // 1. POST Competencia
        int compId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "nome": "%s",
                  "categoria": "TÃƒÂ©cnica"
                }
                """.formatted(compName))
            .when()
            .post("/api/competencia")
            .then()
            .statusCode(201)
            .body("nome", equalTo(compName))
            .extract()
            .path("id");

        // 2. GET Competencia
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/competencia/" + compId)
            .then()
            .statusCode(200)
            .body("id", equalTo(compId));
    }

    @Test
    void testAlternativeFlow_ListarCompetencias() {
        String token = getColabToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/competencia")
            .then()
            .statusCode(200); 
    }

    @Test
    void testExceptionFlow_DuplicarCompetencia() {
        String token = getAdminToken();
        String compName = "Comp Duplicada " + System.currentTimeMillis();

        // Primeira vez - OK
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "nome": "%s",
                  "categoria": "TÃƒÂ©cnica"
                }
                """.formatted(compName))
            .when()
            .post("/api/competencia")
            .then()
            .statusCode(201);

        // Segunda vez - Bad Request
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "nome": "%s",
                  "categoria": "TÃƒÂ©cnica"
                }
                """.formatted(compName))
            .when()
            .post("/api/competencia")
            .then()
            .statusCode(400);
    }
}


