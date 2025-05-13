
Feature: Actualización parcial de usuarios
  Como usuario autenticado del sistema
  Quiero actualizar parcialmente los datos de un usuario existente
  Para corregir o modificar información sin enviar todos los datos

  Background:
    Given la API está disponible
    # La autenticación se hará en los escenarios específicos que la requieran

  Scenario: Actualizar email y usuario de un usuario existente
    #Given existe un usuario registrado para pruebas
    Given estoy autenticado con un token JWT valido
    # Necesitamos un usuario para actualizar, lo creamos previamente en un paso Given
    And existe un usuario con email "patch.target@example.com", usuario "patchTarget", clave "PatchSecure123" y rol "USER"
    When envío una solicitud PATCH a "/usuarios/{id}" para el usuario creado con el siguiente cuerpo:
      """
      {
        "email": "patch.updated@example.com",
        "usuario": "patchUserUpdated"
      }
      """
    Then la respuesta debe tener el código 200
    And la respuesta JSON contiene el campo "email" con valor "patch.updated@example.com"
    And la respuesta JSON contiene el campo "usuario" con valor "patchUserUpdated"
    And la respuesta JSON contiene el campo "rol" con valor "USER"

  Scenario: Acceso no autenticado a la actualización parcial
    And existe un usuario con email "patch.unauth@example.com", usuario "patchUnauth", clave "PatchSecure789" y rol "USER"
    When envío una solicitud PATCH no autenticada a "/usuarios/{id}" para el usuario creado con el siguiente cuerpo:
      """
      {
        "usuario": "unauthenticatedAttempt"
      }
      """
    Then la respuesta debe tener el código 401



