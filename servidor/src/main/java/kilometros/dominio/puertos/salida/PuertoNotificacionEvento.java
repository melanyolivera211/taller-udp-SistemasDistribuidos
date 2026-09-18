package kilometros.dominio.puertos.salida;

import kilometros.dominio.enums.EstadoServidor;
import kilometros.dominio.modelos.EventoServidor;

/**
 * Puerto de salida para emitir eventos y cambios de estado del servidor
 * hacia observadores externos (como la interfaz gráfica).
 */
public interface PuertoNotificacionEvento {
    void notificarEvento(EventoServidor evento);

    void notificarCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
