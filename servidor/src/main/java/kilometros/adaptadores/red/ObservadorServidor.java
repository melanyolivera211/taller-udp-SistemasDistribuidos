package kilometros.adaptadores.red;

import kilometros.dominio.enums.EstadoServidor;
import kilometros.dominio.modelos.EventoServidor;

public interface ObservadorServidor {
    void onEvento(EventoServidor evento);

    void onCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
