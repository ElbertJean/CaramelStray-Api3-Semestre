package br.com.alltallent.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PerguntaE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_CriarEBuscarPergunta() {
        String token = getAdminToken();

        // 1. POST Pergunta
        int perguntaId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "pergunta": "Qual sua maior qualidade?",
                  "competenciaCodigo": 1,
                  "tipoPergunta": "DISSERTATIVA"
                }
                """)
            .when()
            .post("/api/perguntas")
            .then()
            .statusCode(201)
            .body("pergunta", equalTo("Qual sua maior qualidade?"))
            .extract()
            .path("codigo");

        // 2. GET Pergunta
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/perguntas/" + perguntaId)
            .then()
            .statusCode(200)
            .body("codigo", equalTo(perguntaId));
    }

    @Test
    void testAlternativeFlow_ListarPerguntas() {
        String token = getColabToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/perguntas")
            .then()
            .statusCode(200); 
    }

    @Test
    void testExceptionFlow_SemPermissaoParaCriar() {
        String token = getPureColabToken(); // Colab puro nÃ£o pode criar

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "pergunta": "Essa pergunta vai dar forbidden",
                  "competenciaCodigo": 1,
                  "tipoPergunta": "DISSERTATIVA"
                }
                """)
            .when()
            .post("/api/perguntas")
            .then()
            .statusCode(403);
    }
}

