package br.com.alltallent.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

class FuncionarioE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_CrudFuncionario() {
        String token = getAdminToken();
        String novoEmail = "func_" + System.currentTimeMillis() + "@test.com";
        String cpf = "444" + (System.currentTimeMillis() % 100000000);
        String cracha = "CRFUNC" + (System.currentTimeMillis() % 100000);

        // 1. Criar um Funcionario (POST)
        int codigoCriado = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nomeCompleto": "Novo Funcionario",
                          "email": "%s",
                          "senhaHash": "password123",
                          "cpf": "%s",
                          "telefone": "1199999999",
                          "idCracha": "%s",
                          "areaId": 10,
                          "perfilId": 2,
                          "tituloProfissional": "Desenvolvedor",
                          "localizacao": "SP"
                        }
                        """.formatted(novoEmail, cpf, cracha))
                .when()
                .post("/api/funcionario")
                .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/funcionario-response-schema.json"))
                .body("nomeCompleto", equalTo("Novo Funcionario"))
                .body("email", equalTo(novoEmail))
                .extract()
                .path("codigo");

        // 2. Buscar por ID (GET)
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/funcionario/" + codigoCriado)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/funcionario-response-schema.json"))
                .body("codigo", equalTo(codigoCriado));

        String tokenNovoFunc = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "password123"
                        }
                        """.formatted(novoEmail))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().path("token");

        given()
                .header("Authorization", "Bearer " + tokenNovoFunc)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nomeCompleto": "Funcionario Atualizado",
                          "email": "%s",
                          "telefone": "1188888888"
                        }
                        """.formatted(novoEmail))
                .when()
                .put("/api/funcionario/" + codigoCriado)
                .then()
                .statusCode(200)
                .body("nomeCompleto", equalTo("Funcionario Atualizado"));
    }

    @Test
    void testAlternativeFlow_ListarFuncionarios() {
        String token = getAdminToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/funcionario")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/funcionario-list-response-schema.json"));
    }

    @Test
    void testExceptionFlow_AcessoNegadoSemToken() {
        given()
                .when()
                .get("/api/funcionario")
                .then()
                .statusCode(anyOf(is(401), is(403)));
    }

    @Test
    void testExceptionFlow_BuscarIdInexistente() {
        String token = getAdminToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/funcionario/999999")
                .then()
                .statusCode(anyOf(is(404), is(403)));
    }
}
