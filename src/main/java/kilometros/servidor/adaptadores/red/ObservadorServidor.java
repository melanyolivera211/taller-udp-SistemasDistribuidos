package kilometros.servidor.adaptadores.red;

import kilometros.servidor.dominio.enums.EstadoServidor;
import kilometros.servidor.dominio.modelos.EventoServidor;

public interface ObservadorServidor {
    void onEvento(EventoServidor evento);

    void onCambioEstado(EstadoServidor nuevoEstado, int puerto);
}
