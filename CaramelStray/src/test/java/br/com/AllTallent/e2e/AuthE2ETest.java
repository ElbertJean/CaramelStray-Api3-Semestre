package br.com.alltallent.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

class AuthE2ETest extends BaseE2ETest {

    @Test
    void testMainFlow_CadastroELogin() {
        String email = "auth_main_" + System.currentTimeMillis() + "@test.com";
        String cpf = "222" + (System.currentTimeMillis() % 100000000);
        String cracha = "CRAUTH" + (System.currentTimeMillis() % 100000);

        // 1. Cadastro
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nomeCompleto": "Usuario Auth Main",
                          "email": "%s",
                          "senha": "password123",
                          "cpf": "%s",
                          "telefone": "1199999999",
                          "idCracha": "%s",
                          "codigoArea": 10,
                          "codigoPerfil": 2,
                          "tituloProfissional": "Testador",
                          "localizacao": "SP"
                        }
                        """.formatted(email, cpf, cracha))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body(equalTo("Colaborador cadastrado com sucesso!"));

        // 2. Login com Schema Validation
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "password123"
                        }
                        """.formatted(email))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/auth-login-response-schema.json"))
                .body("token", notNullValue());
    }

    @Test
    void testExceptionFlow_LoginInvalido() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "nao_existe@test.com",
                          "password": "senha_errada"
                        }
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(anyOf(is(401), is(403)));
    }

    @Test
    void testExceptionFlow_CadastroSemEmail() {
        String cpf = "333" + (System.currentTimeMillis() % 100000000);
        String cracha = "CRAUTH2" + (System.currentTimeMillis() % 100000);

        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "nomeCompleto": "Usuario Sem Email",
                  "senha": "password123",
                  "cpf": "%s",
                  "telefone": "1199999999",
                  "idCracha": "%s",
                  "codigoArea": 10,
                  "codigoPerfil": 2,
                  "tituloProfissional": "Testador",
                  "localizacao": "SP"
                }
                """.formatted(cpf, cracha))
            .when()
            .post("/api/auth/register")
            .then()
            .statusCode(anyOf(is(400), is(403)));
    }
}
