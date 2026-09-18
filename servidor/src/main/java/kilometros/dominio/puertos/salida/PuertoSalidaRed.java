package kilometros.dominio.puertos.salida;

import kilometros.dominio.modelos.RespuestaCliente;

/**
 * Puerto de salida para el envío de respuestas de red hacia los clientes.
 * Recibe exclusivamente objetos del modelo de dominio.
 */
public interface PuertoSalidaRed {
    void enviarRespuesta(RespuestaCliente respuesta);
}
