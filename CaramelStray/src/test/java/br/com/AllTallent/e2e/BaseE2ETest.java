package br.com.alltallent.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseE2ETest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        jdbcTemplate.execute(
                "INSERT INTO tb_cad_area(codigo, nome, descricao) VALUES (10, 'Area E2E', 'Desc') ON CONFLICT (codigo) DO NOTHING;");
        jdbcTemplate.execute(
                "INSERT INTO tb_cad_perfil(codigo, nome, descricao) VALUES (1, 'Admin', 'ROLE_ADMIN') ON CONFLICT (codigo) DO NOTHING;");
        jdbcTemplate.execute(
                "INSERT INTO tb_cad_perfil(codigo, nome, descricao) VALUES (2, 'Gestor', 'ROLE_GESTOR') ON CONFLICT (codigo) DO NOTHING;");
        jdbcTemplate.execute(
                "INSERT INTO tb_cad_perfil(codigo, nome, descricao) VALUES (3, 'Colaborador', 'ROLE_COLAB') ON CONFLICT (codigo) DO NOTHING;");
        jdbcTemplate.execute(
                "INSERT INTO tb_cad_competencia(codigo, nome, categoria) VALUES (1, 'Comp E2E', 'Tecnica') ON CONFLICT (codigo) DO NOTHING;");
    }

    protected String getAdminToken() {
        String email = "admin_e2e_" + System.currentTimeMillis() + "@test.com";
        String senha = "password123";
        String cracha = "CRA" + (System.currentTimeMillis() % 100000);
        String cpf = "000" + (System.currentTimeMillis() % 100000000);

        // Cria o user
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nomeCompleto": "Admin E2E",
                          "email": "%s",
                          "senha": "%s",
                          "cpf": "%s",
                          "telefone": "1199999999",
                          "idCracha": "%s",
                          "codigoArea": 10,
                          "codigoPerfil": 1,
                          "tituloProfissional": "Administrador",
                          "localizacao": "SP"
                        }
                        """.formatted(email, senha, cpf, cracha))
                .when()
                .post("/api/auth/register")
                .then()
                .log().all()
                .statusCode(201); // Created

        // Faz o login
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(email, senha))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected String getColabToken() {
        String email = "colab_e2e_" + System.currentTimeMillis() + "@test.com";
        String senha = "password123";
        String cracha = "CRC" + (System.currentTimeMillis() % 100000);
        String cpf = "111" + (System.currentTimeMillis() % 100000000);

        // Cria o user
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nomeCompleto": "Colab E2E",
                          "email": "%s",
                          "senha": "%s",
                          "cpf": "%s",
                          "telefone": "1199999999",
                          "idCracha": "%s",
                          "codigoArea": 10,
                          "codigoPerfil": 2,
                          "tituloProfissional": "Colaborador",
                          "localizacao": "SP"
                        }
                        """.formatted(email, senha, cpf, cracha))
                .when()
                .post("/api/auth/register")
                .then()
                .log().all()
                .statusCode(201);

        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(email, senha))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected String getPureColabToken() {
        String email = "pure_colab_" + System.currentTimeMillis() + "@test.com";
        String senha = "password123";
        String cracha = "CRPC" + (System.currentTimeMillis() % 100000);
        String cpf = "333" + (System.currentTimeMillis() % 100000000);

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nomeCompleto": "Pure Colab E2E",
                          "email": "%s",
                          "senha": "%s",
                          "cpf": "%s",
                          "telefone": "1199999999",
                          "idCracha": "%s",
                          "codigoArea": 10,
                          "codigoPerfil": 3,
                          "tituloProfissional": "Colaborador",
                          "localizacao": "SP"
                        }
                        """.formatted(email, senha, cpf, cracha))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201);

        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(email, senha))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
