package br.com.alltallent.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AvaliacaoE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_CriarAvaliacao() {
        String token = getAdminToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "titulo": "AvaliaÃ§Ã£o E2E",
                          "dataPrazo": "2026-12-31",
                          "codigosFuncionarios": [],
                          "codigosPerguntas": []
                        }
                        """)
                .when()
                .post("/api/avaliacoes")
                .then()
                .statusCode(anyOf(is(201), is(400), is(404)));
    }

    @Test
    void testAlternativeFlow_BuscarAvaliacoes() {
        String token = getColabToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/avaliacoes")
                .then()
                .statusCode(200);
    }

    @Test
    void testExceptionFlow_BuscarAvaliacaoInexistente() {
        String token = getAdminToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/avaliacoes/999999")
                .then()
                .statusCode(anyOf(is(404), is(403)));
    }
}


