package kilometros.adaptadores.red;

import kilometros.aplicacion.excepciones.ServidorRedException;
import kilometros.dominio.puertos.salida.ControladorServidorRedPort;
import kilometros.dominio.vo.PuertoRed;
import java.net.SocketException;
import java.util.Objects;

public final class AdaptadorControlServidorRed implements ControladorServidorRedPort {

    private final CanalUdp canalUdp;
    private final Runnable accionIniciarListener;
    private final Runnable accionDetenerListener;

    public AdaptadorControlServidorRed(
            CanalUdp canalUdp,
            Runnable accionIniciarListener,
            Runnable accionDetenerListener) {
        this.canalUdp = Objects.requireNonNull(canalUdp, "El canal UDP es obligatorio.");
        this.accionIniciarListener = Objects.requireNonNull(accionIniciarListener, "La acción de inicio es obligatoria.");
        this.accionDetenerListener = Objects.requireNonNull(accionDetenerListener, "La acción de detención es obligatoria.");
    }

    @Override
    public synchronized void iniciar(PuertoRed puerto) throws ServidorRedException {
        try {
            canalUdp.abrir(Objects.requireNonNull(puerto, "El puerto es obligatorio.").valor());
            accionIniciarListener.run();
        } catch (SocketException excepcion) {
            throw new ServidorRedException(
                    "No se pudo abrir el servidor UDP en el puerto " + puerto.valor() + ".", excepcion);
        }
    }

    @Override
    public synchronized void detener() {
        accionDetenerListener.run();
        canalUdp.cerrar();
    }

    @Override
    public boolean isActivo() {
        return canalUdp.isAbierto();
    }

    @Override
    public PuertoRed getPuertoActual() {
        if (!canalUdp.isAbierto()) {
            return null;
        }
        return new PuertoRed(canalUdp.getPuertoActual());
    }
}
