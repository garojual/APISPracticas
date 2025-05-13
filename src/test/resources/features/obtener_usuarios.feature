Feature: Obtener lista de usuarios
  Como usuario autenticado del sistema
  Quiero obtener la lista de usuarios
  Para poder ver los usuarios registrados

  Background:
    Given la API está disponible
    Given existe un usuario registrado para pruebas
    Given estoy autenticado con un token JWT valido

  Scenario: Obtener todos los usuarios registrados (con defaults)
    When envío una solicitud GET a "/usuarios"
    Then la respuesta debe tener el código 200
    And la respuesta JSON debe ser una lista
    And la respuesta JSON debe contener al menos 1 elemento

