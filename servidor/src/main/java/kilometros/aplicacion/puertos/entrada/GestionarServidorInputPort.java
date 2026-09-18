package kilometros.aplicacion.puertos.entrada;

import kilometros.dominio.enums.EstadoServidor;

public interface GestionarServidorInputPort {
    void iniciarServidor(int puerto);

    void detenerServidor();

    boolean estaActivo();

    EstadoServidor obtenerEstadoActual();

    int obtenerPuertoActual();
}
