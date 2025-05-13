
Feature: Eliminación de usuarios
  Como usuario autenticado del sistema
  Quiero eliminar un usuario existente por su ID
  Para gestionar los usuarios registrados en la aplicación

  Background:
    Given la API está disponible
    # La autenticación se hará en los escenarios específicos que la requieran

  Scenario: Eliminar un usuario existente exitosamente
    Given existe un usuario registrado para pruebas
    Given estoy autenticado con un token JWT valido
    # Necesitamos un usuario para eliminar, lo creamos previamente
    And existe un usuario con email "delete.target@example.com", usuario "deleteTarget", clave "DeleteSecure123" y rol "USER"
    When envío una solicitud DELETE a "/usuarios/{id}" para el usuario creado
    Then la respuesta debe tener el código 204

  Scenario: Intentar eliminar un usuario inexistente
    Given existe un usuario registrado para pruebas
    Given estoy autenticado con un token JWT valido
    When envío una solicitud DELETE a "/usuarios/{id}" para el ID 999999999
    Then la respuesta debe tener el código 404
    And la respuesta body es el texto "Usuario no encontrado"

  Scenario: Acceso no autenticado a la eliminación
    # Necesitamos un usuario para intentar eliminar, aunque la falla sea por auth
    And existe un usuario con email "delete.unauth@example.com", usuario "deleteUnauth", clave "DeleteSecure456" y rol "USER"
    When envío una solicitud DELETE no autenticada a "/usuarios/{id}" para el usuario creado
    Then la respuesta debe tener el código 401

