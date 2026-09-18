
package kilometros;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Punto de entrada principal y lanzador del sistema distribuido.
 * Permite seleccionar e iniciar el Servidor UDP, el Cliente UDP o ambos
 * para pruebas de integración rápida, sin comprometer la separación de
 * responsabilidades de la Arquitectura Hexagonal.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            String modo = args[0].trim().toLowerCase();
            switch (modo) {
                case "servidor", "server" -> {
                    kilometros.servidor.Main.main(args);
                    return;
                }
                case "cliente", "client" -> {
                    kilometros.cliente.Main.main(args);
                    return;
                }
                case "ambos", "both" -> {
                    iniciarAmbos();
                    return;
                }
                default -> {
                    // Si el argumento no coincide, abrir la ventana selectora
                }
            }
        }

        aplicarLookAndFeel();
        SwingUtilities.invokeLater(Main::mostrarVentanaLanzador);
    }

    private static void mostrarVentanaLanzador() {
        JFrame frame = new JFrame("Conversión de Kilómetros a Millas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(460, 320);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(15, 15));

        // Panel de encabezado
        JPanel panelHeader = new JPanel(new GridLayout(2, 1, 4, 4));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Conversión de Kilómetros a Millas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(new Color(33, 37, 41));

        JLabel lblSubtitulo = new JLabel("Seleccione una opción:", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(108, 117, 125));

        panelHeader.add(lblTitulo);
        panelHeader.add(lblSubtitulo);
        frame.add(panelHeader, BorderLayout.NORTH);

        // Panel de botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 12));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 35, 20, 35));

        JButton btnServidor = crearBoton("1. Iniciar Servidor", new Color(0, 123, 255));
        btnServidor.addActionListener(e -> {
            kilometros.servidor.Main.main(new String[0]);
            frame.dispose();
        });

        JButton btnCliente = crearBoton("2. Iniciar Cliente", new Color(40, 167, 69));
        btnCliente.addActionListener(e -> {
            kilometros.cliente.Main.main(new String[0]);
            frame.dispose();
        });

        JButton btnAmbos = crearBoton("3. Iniciar Ambos", new Color(108, 117, 125));
        btnAmbos.addActionListener(e -> {
            iniciarAmbos();
            frame.dispose();
        });

        panelBotones.add(btnServidor);
        panelBotones.add(btnCliente);
        panelBotones.add(btnAmbos);
        frame.add(panelBotones, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static void iniciarAmbos() {
        // Iniciar el servidor
        kilometros.servidor.Main.main(new String[0]);

        // Iniciar el cliente con un ligero retardo para que el servidor prepare su ventana
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            kilometros.cliente.Main.main(new String[0]);
        });
    }

    private static JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(280, 42));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setFocusPainted(false);
        return boton;
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ignored) {
            // Mantener L&F por defecto si falla el del sistema
        }
    }
}
