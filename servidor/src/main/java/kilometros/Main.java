package kilometros;

import kilometros.adaptadores.red.AdaptadorControlServidorRed;
import kilometros.adaptadores.red.AdaptadorNotificacionEvento;
import kilometros.adaptadores.red.AdaptadorSalidaUdp;
import kilometros.adaptadores.red.CanalUdp;
import kilometros.adaptadores.red.mapper.UdpNetworkMapper;
import kilometros.aplicacion.mapper.CalculoMapper;
import kilometros.aplicacion.mapper.PeticionMapper;
import kilometros.aplicacion.puertos.entrada.GestionarServidorInputPort;
import kilometros.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import kilometros.aplicacion.servicios.GestionarServidorService;
import kilometros.aplicacion.servicios.ProcesarPeticionUdpService;
import kilometros.dominio.puertos.salida.ControladorServidorRedPort;
import kilometros.dominio.puertos.salida.PuertoSalidaRed;
import kilometros.entrypoint.gui.ServidorFrame;
import kilometros.entrypoint.udp.ReceptorPeticionesUdp;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Composition Root del Servidor UDP.
 * Ensambla adaptadores, servicios de aplicación y entrypoints
 * siguiendo estrictamente la Arquitectura Hexagonal y los principios SOLID.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        aplicarLookAndFeel();

        // 1. Adaptadores e infraestructura de red compartida
        CanalUdp canalUdp = new CanalUdp();
        AdaptadorNotificacionEvento notificador = new AdaptadorNotificacionEvento();
        UdpNetworkMapper redMapper = new UdpNetworkMapper();
        PuertoSalidaRed puertoSalidaRed = new AdaptadorSalidaUdp(canalUdp, redMapper, notificador);

        // 2. Mappers de aplicación
        PeticionMapper peticionMapper = new PeticionMapper();
        CalculoMapper calculoMapper = new CalculoMapper();

        // 3. Servicios de aplicación (Casos de uso)
        ProcesarPeticionUdpInputPort procesarPeticionPort = new ProcesarPeticionUdpService(
                puertoSalidaRed,
                notificador,
                peticionMapper,
                calculoMapper
        );

        // 4. Entrypoint UDP y controlador de red
        ReceptorPeticionesUdp receptorUdp = new ReceptorPeticionesUdp(
                canalUdp,
                procesarPeticionPort,
                notificador
        );

        ControladorServidorRedPort controladorRed = new AdaptadorControlServidorRed(
                canalUdp,
                receptorUdp::iniciar,
                receptorUdp::detener
        );

        GestionarServidorInputPort gestionarServidorPort = new GestionarServidorService(
                controladorRed,
                notificador
        );

        // 5. Lanzar GUI en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            ServidorFrame frame = new ServidorFrame(gestionarServidorPort);
            notificador.registrarObservador(frame);
            frame.setVisible(true);
        });
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ignored) {
            // Usar Look & Feel predeterminado si falla el del sistema
        }
    }
}
