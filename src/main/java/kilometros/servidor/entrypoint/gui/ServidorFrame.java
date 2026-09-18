package kilometros.servidor.entrypoint.gui;

import kilometros.servidor.adaptadores.red.ObservadorServidor;
import kilometros.servidor.adaptadores.red.util.RedUtil;
import kilometros.servidor.aplicacion.excepciones.ServidorRedException;
import kilometros.servidor.aplicacion.puertos.entrada.GestionarServidorInputPort;
import kilometros.servidor.dominio.enums.EstadoServidor;
import kilometros.servidor.dominio.excepciones.DominioException;
import kilometros.servidor.dominio.modelos.EventoServidor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ServidorFrame extends JFrame implements ObservadorServidor {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");

    private final transient GestionarServidorInputPort gestionarServidorPort;

    private JTextField campoIP;
    private JTextField campoPuerto;
    private JLabel txtEstado;
    private JButton btnIniciar;
    private JTextArea cajaLog;

    public ServidorFrame(GestionarServidorInputPort gestionarServidorPort) {
        super("Servidor UDP - Conversión Km a Millas (Arquitectura Hexagonal)");
        this.gestionarServidorPort = Objects.requireNonNull(gestionarServidorPort, "El puerto de gestión es obligatorio.");
        initComponents();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 420);
        setMinimumSize(new Dimension(500, 360));
        setLayout(new BorderLayout(10, 10));

        // Título superior
        JLabel lblTitulo = new JLabel("SERVIDOR CONVERSIÓN (KM A MILLAS) - UDP", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Pestaña 1: CONEXIÓN
        JPanel panelConexion = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblIP = new JLabel("DIRECCIÓN IP:");
        lblIP.setFont(new Font("Tahoma", Font.PLAIN, 13));
        campoIP = new JTextField(RedUtil.obtenerIpLocal(), 15);
        campoIP.setEditable(false);

        JLabel lblPuerto = new JLabel("PUERTO DE RED:");
        lblPuerto.setFont(new Font("Tahoma", Font.PLAIN, 13));
        campoPuerto = new JTextField("9007", 15);

        JLabel lblEstadoTitulo = new JLabel("ESTADO:");
        lblEstadoTitulo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtEstado = new JLabel("OFF LINE");
        txtEstado.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtEstado.setForeground(Color.RED);

        btnIniciar = new JButton("INICIAR");
        btnIniciar.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnIniciar.setForeground(new Color(0, 153, 51));
        btnIniciar.setPreferredSize(new Dimension(140, 35));
        btnIniciar.addActionListener(evt -> alternarServidor());

        // Layout pestaña 1
        gbc.gridx = 0; gbc.gridy = 0;
        panelConexion.add(lblIP, gbc);
        gbc.gridx = 1;
        panelConexion.add(campoIP, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelConexion.add(lblPuerto, gbc);
        gbc.gridx = 1;
        panelConexion.add(campoPuerto, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelConexion.add(lblEstadoTitulo, gbc);
        gbc.gridx = 1;
        panelConexion.add(txtEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panelConexion.add(btnIniciar, gbc);

        tabbedPane.addTab("CONEXIÓN", panelConexion);

        // Pestaña 2: LOG DE CONEXIONES
        JPanel panelLog = new JPanel(new BorderLayout(8, 8));
        panelLog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cajaLog = new JTextArea();
        cajaLog.setEditable(false);
        cajaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(cajaLog);
        panelLog.add(scrollLog, BorderLayout.CENTER);

        JPanel panelBotonLog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiar = new JButton("LIMPIAR LOG");
        btnLimpiar.addActionListener(evt -> cajaLog.setText(""));
        panelBotonLog.add(btnLimpiar);
        panelLog.add(panelBotonLog, BorderLayout.SOUTH);

        tabbedPane.addTab("LOG DE CONEXIONES", panelLog);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void alternarServidor() {
        if (!gestionarServidorPort.estaActivo()) {
            try {
                int puerto = Integer.parseInt(campoPuerto.getText().trim());
                gestionarServidorPort.iniciarServidor(puerto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un puerto de red válido (número entero).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            } catch (DominioException | ServidorRedException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Iniciar Servidor", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            gestionarServidorPort.detenerServidor();
        }
    }

    @Override
    public void onEvento(EventoServidor evento) {
        SwingUtilities.invokeLater(() -> {
            String lineaLog = String.format(
                    "[%s] [%s] %s -> %s%n",
                    evento.getFechaHora().format(FORMATO_FECHA),
                    evento.getCategoria(),
                    evento.getEndpoint(),
                    evento.getDescripcion()
            );
            cajaLog.append(lineaLog);
            cajaLog.setCaretPosition(cajaLog.getDocument().getLength());
        });
    }

    @Override
    public void onCambioEstado(EstadoServidor nuevoEstado, int puerto) {
        SwingUtilities.invokeLater(() -> {
            if (nuevoEstado == EstadoServidor.ACTIVO) {
                txtEstado.setText("ONLINE");
                txtEstado.setForeground(new Color(0, 153, 51));
                btnIniciar.setText("DETENER");
                btnIniciar.setForeground(Color.RED);
                campoPuerto.setEditable(false);
            } else {
                txtEstado.setText("OFF LINE");
                txtEstado.setForeground(Color.RED);
                btnIniciar.setText("INICIAR");
                btnIniciar.setForeground(new Color(0, 153, 51));
                campoPuerto.setEditable(true);
            }
        });
    }

    public JTextField getCampoIP() { return campoIP; }
    public JTextField getCampoPuerto() { return campoPuerto; }
    public JLabel getTxtEstado() { return txtEstado; }
    public JButton getBtnIniciar() { return btnIniciar; }
    public JTextArea getCajaLog() { return cajaLog; }
}
