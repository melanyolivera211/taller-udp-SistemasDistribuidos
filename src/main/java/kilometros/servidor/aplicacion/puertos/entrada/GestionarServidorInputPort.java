package kilometros.servidor.aplicacion.puertos.entrada;

import kilometros.servidor.dominio.enums.EstadoServidor;

public interface GestionarServidorInputPort {
    void iniciarServidor(int puerto);

    void detenerServidor();

    boolean estaActivo();

    EstadoServidor obtenerEstadoActual();

    int obtenerPuertoActual();
}
