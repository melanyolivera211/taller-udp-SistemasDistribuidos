package kilometros.cliente.cliente;

import kilometros.cliente.adaptadores.notificacion.AdaptadorNotificacionCliente;
import kilometros.cliente.adaptadores.red.AdaptadorClienteUdp;
import kilometros.cliente.adaptadores.red.CanalUdp;
import kilometros.cliente.adaptadores.red.ProtocoloUdpMapper;
import kilometros.cliente.aplicacion.mapper.ClienteMapper;
import kilometros.cliente.aplicacion.servicios.ClienteKmService;
import kilometros.cliente.entrypoint.gui.ClienteFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Composition Root del Cliente UDP.
 * Ensambla adaptadores, servicios de aplicación y entrypoint GUI
 * siguiendo estrictamente la Arquitectura Hexagonal y los principios SOLID.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        aplicarLookAndFeel();

        // 1. Adaptadores e infraestructura de red
        CanalUdp canalUdp = new CanalUdp();
        ProtocoloUdpMapper protocolMapper = new ProtocoloUdpMapper();
        AdaptadorClienteUdp clienteUdpPort = new AdaptadorClienteUdp(canalUdp, protocolMapper);

        // 2. Adaptador de notificación (Patrón Observador)
        AdaptadorNotificacionCliente notificador = new AdaptadorNotificacionCliente();

        // 3. Mapper de aplicación
        ClienteMapper clienteMapper = new ClienteMapper();

        // 4. Servicio de aplicación (Casos de uso: Conexión y Conversión)
        ClienteKmService clienteKmService = new ClienteKmService(
                clienteUdpPort,
                notificador,
                clienteMapper
        );

        // 5. Lanzar interfaz gráfica en el EDT de Swing
        SwingUtilities.invokeLater(() -> {
            ClienteFrame frame = new ClienteFrame(clienteKmService, clienteKmService);
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
