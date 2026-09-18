package kilometros.cliente.dominio.puertos.salida;

import kilometros.cliente.dominio.enums.EstadoConexion;
import kilometros.cliente.dominio.modelos.EventoCliente;

public interface PuertoNotificacionCliente {

    void notificarEvento(EventoCliente evento);

    void notificarCambioEstado(EstadoConexion nuevoEstado);
}
