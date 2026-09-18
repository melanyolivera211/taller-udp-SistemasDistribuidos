package kilometros.aplicacion.servicios;

import kilometros.aplicacion.excepciones.ServidorRedException;
import kilometros.aplicacion.puertos.entrada.GestionarServidorInputPort;
import kilometros.dominio.enums.EstadoServidor;
import kilometros.dominio.modelos.EventoServidor;
import kilometros.dominio.puertos.salida.ControladorServidorRedPort;
import kilometros.dominio.puertos.salida.PuertoNotificacionEvento;
import kilometros.dominio.vo.PuertoRed;
import java.util.Objects;

public final class GestionarServidorService implements GestionarServidorInputPort {

    private final ControladorServidorRedPort controladorRed;
    private final PuertoNotificacionEvento notificador;

    public GestionarServidorService(ControladorServidorRedPort controladorRed, PuertoNotificacionEvento notificador) {
        this.controladorRed = Objects.requireNonNull(controladorRed, "El controlador de red es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
    }

    @Override
    public void iniciarServidor(int puerto) throws ServidorRedException {
        PuertoRed puertoVo = new PuertoRed(puerto);
        controladorRed.iniciar(puertoVo);

        notificador.notificarCambioEstado(EstadoServidor.ACTIVO, puerto);
        notificador.notificarEvento(
                new EventoServidor("SERVICIO", "LOCAL:" + puerto, "Servidor UDP iniciado en el puerto " + puerto + ".")
        );
    }

    @Override
    public void detenerServidor() {
        int puertoAnterior = obtenerPuertoActual();
        controladorRed.detener();

        notificador.notificarCambioEstado(EstadoServidor.INACTIVO, puertoAnterior);
        notificador.notificarEvento(
                new EventoServidor("SERVICIO", "LOCAL", "Servidor UDP detenido correctamente.")
        );
    }

    @Override
    public boolean estaActivo() {
        return controladorRed.isActivo();
    }

    @Override
    public EstadoServidor obtenerEstadoActual() {
        return estaActivo() ? EstadoServidor.ACTIVO : EstadoServidor.INACTIVO;
    }

    @Override
    public int obtenerPuertoActual() {
        PuertoRed puerto = controladorRed.getPuertoActual();
        return Objects.nonNull(puerto) ? puerto.valor() : 0;
    }
}
