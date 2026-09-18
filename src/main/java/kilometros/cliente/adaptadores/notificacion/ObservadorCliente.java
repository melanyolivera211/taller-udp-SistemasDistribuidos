package kilometros.cliente.adaptadores.notificacion;

import kilometros.cliente.dominio.enums.EstadoConexion;
import kilometros.cliente.dominio.modelos.EventoCliente;

public interface ObservadorCliente {

    void onEvento(EventoCliente evento);

    void onCambioEstado(EstadoConexion nuevoEstado);
}
