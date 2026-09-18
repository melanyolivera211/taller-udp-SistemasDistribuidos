package kilometros.entrypoint.udp;

import kilometros.adaptadores.red.CanalUdp;
import kilometros.aplicacion.dto.ProcesarPeticionUdpCommand;
import kilometros.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import kilometros.dominio.modelos.EventoServidor;
import kilometros.dominio.puertos.salida.PuertoNotificacionEvento;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ReceptorPeticionesUdp {

    private static final int BUFFER_SIZE = 2048;

    private final CanalUdp canalUdp;
    private final ProcesarPeticionUdpInputPort inputPort;
    private final PuertoNotificacionEvento notificador;

    private Thread hiloEscucha;
    private volatile boolean escuchando = false;

    public ReceptorPeticionesUdp(
            CanalUdp canalUdp,
            ProcesarPeticionUdpInputPort inputPort,
            PuertoNotificacionEvento notificador) {
        this.canalUdp = Objects.requireNonNull(canalUdp, "El canal UDP es obligatorio.");
        this.inputPort = Objects.requireNonNull(inputPort, "El puerto de entrada es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
    }

    public synchronized void iniciar() {
        if (escuchando) {
            return;
        }
        escuchando = true;
        hiloEscucha = new Thread(this::cicloEscucha, "Thread-ReceptorPeticionesUdp-" + canalUdp.getPuertoActual());
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    public synchronized void detener() {
        escuchando = false;
        if (Objects.nonNull(hiloEscucha) && hiloEscucha.isAlive()) {
            hiloEscucha.interrupt();
        }
        hiloEscucha = null;
    }

    public boolean isEscuchando() {
        return escuchando;
    }

    private void cicloEscucha() {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (escuchando && canalUdp.isAbierto()) {
            try {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                canalUdp.recibir(paquete);

                String texto = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8).trim();
                InetAddress direccion = paquete.getAddress();
                int puerto = paquete.getPort();

                ProcesarPeticionUdpCommand comando = new ProcesarPeticionUdpCommand(
                        direccion.getHostAddress(),
                        puerto,
                        texto
                );

                inputPort.procesar(comando);

            } catch (SocketException excepcion) {
                if (!escuchando) {
                    break;
                }
                notificarError("Error de socket al recibir paquete: " + excepcion.getMessage());
            } catch (IOException excepcion) {
                if (escuchando) {
                    notificarError("Error I/O al recibir datagrama: " + excepcion.getMessage());
                }
            }
        }
    }

    private void notificarError(String mensaje) {
        notificador.notificarEvento(new EventoServidor("ERROR", "SOCKET", mensaje));
    }
}
