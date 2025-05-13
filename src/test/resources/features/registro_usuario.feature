Feature: Registro de usuarios
  Como usuario nuevo del sistema
  Quiero registrarme en la aplicación
  Para poder acceder a las funcionalidades del sistema

  Background:
    Given la API está disponible

  Scenario: Registrar un nuevo usuario exitosamente
    When envío una solicitud POST a "http://quarkus-api:8080/usuarios" con el siguiente cuerpo:
      """
      {
        "email": "nuevo@example.com",
        "clave": "Password123",
        "usuario": "nuevoUsuario",
        "rol": "ADMIN"
      }
      """
    Then la respuesta debe tener el código 201
    And la respuesta JSON contiene el campo "email" con valor "nuevo@example.com"
    And la respuesta JSON contiene el campo "usuario" con valor "nuevoUsuario"
    And la respuesta JSON contiene el campo "rol" con valor "ADMIN"

  Scenario: Intento de registrar un usuario con datos incompletos (faltando usuario)
    When envío una solicitud POST a "http://quarkus-api:8080/usuarios" con el siguiente cuerpo:
      """
      {
        "email": "incompleto@example.com",
        "clave": "Password123",
        "rol": "USER"
      }
      """
    Then la respuesta debe tener el código 400
    And la respuesta debe contener un error de validación para el campo "registerUser.request.usuario" con el mensaje "El campo es requerido"

  Scenario: Intento de registrar un usuario con formato de email inválido
    When envío una solicitud POST a "http://quarkus-api:8080/usuarios" con el siguiente cuerpo:
      """
      {
        "email": "emailinvalido",
        "clave": "Password789",
        "usuario": "usuarioInvalido",
        "rol": "USER"
      }
      """
    Then la respuesta debe tener el código 400
    And la respuesta debe contener un error de validación para el campo "registerUser.request.email" con el mensaje "Debe ser un email válido"

  Scenario: Intento de registrar un usuario con rol inválido
    When envío una solicitud POST a "http://quarkus-api:8080/usuarios" con el siguiente cuerpo:
      """
      {
        "email": "usuario.rol@example.com",
        "clave": "Password123",
        "usuario": "usuarioRolInvalido",
        "rol": "ROL_INEXISTENTE"
      }
      """
    Then la respuesta debe tener el código 400
    And la respuesta JSON contiene el campo "attributeName" con valor "rol"
    And la respuesta JSON contiene el campo "value" con valor "ROL_INEXISTENTE"