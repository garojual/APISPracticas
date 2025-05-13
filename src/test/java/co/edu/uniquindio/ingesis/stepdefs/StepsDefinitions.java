package co.edu.uniquindio.ingesis.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasEntry;

@QuarkusTest
public class StepsDefinitions {

    private Response response;
    private ValidatableResponse validatableResponse;
    private String jwtToken;
    private Long createdUserId;

    @Given("la API está disponible")
    public void la_api_esta_disponible() {
        //baseURI = "http://localhost:8081";
    }

    @Given("existe un usuario registrado para pruebas")
    public void existe_un_usuario_registrado_para_pruebas() {
        // Crear objeto JSON con los datos del usuario según el formato específico
        String userJson = "{"
                + "\"email\": \"isabellacardozo11@gmail.com\","
                + "\"usuario\": \"isabellaCardozo\","
                + "\"clave\": \"Password123\","
                + "\"rol\": \"ADMIN\""
                + "}";

        // Enviar solicitud para crear el usuario
        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(userJson)
                .when()
                .post("/usuarios");

        // Verificar si la creación fue exitosa o si el usuario ya existe
        int statusCode = createResponse.getStatusCode();
        if (statusCode != 201 && statusCode != 409) { // 201 Created o 409 Conflict (si ya existe)
            throw new RuntimeException("No se pudo crear el usuario de prueba. Código: " + statusCode);
        }
    }

    @Given("estoy autenticado con un token JWT valido")
    public void estoy_autenticado_con_un_token_jwt_valido() {

        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body("{\"email\": \"isabellacardozo11@gmail.com\", \"clave\": \"Password123\"}")
                .when()
                .post("/usuarios/login");

        loginResponse.then().statusCode(200);

        this.jwtToken = loginResponse.jsonPath().getString("token");

        if (this.jwtToken == null || this.jwtToken.isEmpty()) {
            throw new RuntimeException("No se pudo obtener el token JWT. Verifica credenciales y respuesta de /usuarios/login.");
        }
    }

    @Given("existe un usuario con email {string}, usuario {string}, clave {string} y rol {string}")
    public void existe_un_usuario_con_email_usuario_clave_y_rol(String email, String usuario, String clave, String rol) {
        String body = String.format("{\"email\": \"%s\", \"usuario\": \"%s\", \"clave\": \"%s\", \"rol\": \"%s\"}", email, usuario, clave, rol);

        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/usuarios");

        createResponse.then().statusCode(201);

        this.createdUserId = createResponse.jsonPath().getLong("id");
    }

    @When("envío una solicitud POST a {string} con el siguiente cuerpo:")
    public void envio_solicitud_post_con_cuerpo(String endpoint, String body) {
        var requestSpec = given().header("Content-Type", "application/json").body(body);
        this.response = requestSpec.when().post(endpoint);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud GET a {string}")
    public void envio_solicitud_get(String endpoint) {
        if (this.jwtToken == null) {
            throw new RuntimeException("El token JWT es null. La solicitud GET requiere autenticación.");
        }
        this.response = given()
                .header("Authorization", "Bearer " + this.jwtToken)
                .when()
                .get(endpoint);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud GET a {string} con parámetros:")
    public void envio_solicitud_get_con_parametros(String endpoint, DataTable dataTable) {
        if (this.jwtToken == null) {
            throw new RuntimeException("El token JWT es null. La solicitud GET con parámetros requiere autenticación.");
        }
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        var requestSpec = given().header("Authorization", "Bearer " + this.jwtToken);
        params.forEach((key, value) -> requestSpec.queryParam(key, value));
        this.response = requestSpec.when().get(endpoint);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud GET no autenticada a {string}")
    public void envio_solicitud_get_no_autenticada(String endpoint) {
        this.response = given()
                .when()
                .get(endpoint);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud PATCH a {string} para el usuario creado con el siguiente cuerpo:")
    public void envio_solicitud_patch_autenticada_usuario_creado(String endpointTemplate, String body) {
        if (this.jwtToken == null) {
            throw new RuntimeException("El token JWT es null. La solicitud PATCH requiere autenticación.");
        }
        if (this.createdUserId == null) {
            throw new RuntimeException("No hay un usuario creado para enviar la solicitud PATCH.");
        }
        String url = endpointTemplate.replace("{id}", String.valueOf(this.createdUserId));

        this.response = given()
                .header("Authorization", "Bearer " + this.jwtToken)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch(url);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud PATCH a {string} para el ID {long} con el siguiente cuerpo:")
    public void envio_solicitud_patch_autenticada_id_especifico(String endpointTemplate, Long id, String body) {
        if (this.jwtToken == null) {
            throw new RuntimeException("El token JWT es null. La solicitud PATCH requiere autenticación.");
        }
        String url = endpointTemplate.replace("{id}", String.valueOf(id));

        this.response = given()
                .header("Authorization", "Bearer " + this.jwtToken)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch(url);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud PATCH no autenticada a {string} para el usuario creado con el siguiente cuerpo:")
    public void envio_solicitud_patch_no_autenticada_usuario_creado(String endpointTemplate, String body) {
        if (this.createdUserId == null) {
            throw new RuntimeException("No hay un usuario creado para enviar la solicitud PATCH no autenticada.");
        }
        String url = endpointTemplate.replace("{id}", String.valueOf(this.createdUserId));

        this.response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch(url);
        this.validatableResponse = this.response.then();
    }

    // *** --- NUEVOS PASOS PARA DELETE --- ***

    @When("envío una solicitud DELETE a {string} para el usuario creado")
    public void envio_solicitud_delete_autenticada_usuario_creado(String endpointTemplate) {
        if (this.jwtToken == null) {
            throw new RuntimeException("El token JWT es null. La solicitud DELETE requiere autenticación.");
        }
        if (this.createdUserId == null) {
            throw new RuntimeException("No hay un usuario creado para enviar la solicitud DELETE.");
        }
        String url = endpointTemplate.replace("{id}", String.valueOf(this.createdUserId));

        this.response = given()
                .header("Authorization", "Bearer " + this.jwtToken)
                .when()
                .delete(url);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud DELETE a {string} para el ID {long}")
    public void envio_solicitud_delete_autenticada_id_especifico(String endpointTemplate, Long id) {
        if (this.jwtToken == null) {
            throw new RuntimeException("El token JWT es null. La solicitud DELETE requiere autenticación.");
        }
        String url = endpointTemplate.replace("{id}", String.valueOf(id));

        this.response = given()
                .header("Authorization", "Bearer " + this.jwtToken)
                .when()
                .delete(url);
        this.validatableResponse = this.response.then();
    }

    @When("envío una solicitud DELETE no autenticada a {string} para el usuario creado")
    public void envio_solicitud_delete_no_autenticada_usuario_creado(String endpointTemplate) {
        if (this.createdUserId == null) {
            throw new RuntimeException("No hay un usuario creado para enviar la solicitud DELETE no autenticada.");
        }
        String url = endpointTemplate.replace("{id}", String.valueOf(this.createdUserId));

        this.response = given()
                // NOTA: Aquí NO se añade la cabecera Authorization
                .when()
                .delete(url);
        this.validatableResponse = this.response.then();
    }


    // *** --- FIN NUEVOS PASOS PARA DELETE --- ***


    @Then("la respuesta debe tener el código {int}")
    public void la_respuesta_debe_tener_codigo(int statusCode) {
        this.validatableResponse.statusCode(statusCode);
    }

    @And("la respuesta JSON contiene el campo {string}")
    public void la_respuesta_json_contiene_campo(String fieldName) {
        this.validatableResponse.body(fieldName, notNullValue());
    }

    @And("la respuesta JSON contiene el campo {string} con valor {string}")
    public void la_respuesta_json_contiene_campo_con_valor(String fieldName, String fieldValue) {
        this.validatableResponse.body(fieldName, is(fieldValue));
    }

    @And("la respuesta debe contener un error de validación para el campo {string} con el mensaje {string}")
    public void la_respuesta_debe_contener_error_validacion(String fieldName, String message) {
        this.validatableResponse
                .body("violations", hasItem(hasEntry("field", fieldName)))
                .body("violations", hasItem(hasEntry("message", message)));
    }

    @And("la respuesta JSON debe ser una lista")
    public void la_respuesta_json_debe_ser_una_lista() {
        this.validatableResponse.body("", instanceOf(List.class));
    }

    @And("la respuesta JSON debe contener al menos 1 elemento")
    public void la_respuesta_json_debe_contener_al_menos_un_elemento() {
        this.validatableResponse.body("", not(emptyArray()));
    }

    @And("la respuesta JSON debe contener exactamente {int} elemento\\(s)")
    public void la_respuesta_json_debe_contener_exactamente_elementos(int count) {
        this.validatableResponse.body("", hasSize(count));
    }

    @And("el primer elemento de la lista JSON contiene el campo {string} con valor {string}")
    public void el_primer_elemento_lista_json_contiene_campo_con_valor(String fieldName, String fieldValue) {
        this.validatableResponse.body("[0]." + fieldName, is(fieldValue));
    }

    @And("la respuesta body es el texto {string}")
    public void la_respuesta_body_es_el_texto(String expectedText) {
        this.validatableResponse.body(is(expectedText));
    }
}