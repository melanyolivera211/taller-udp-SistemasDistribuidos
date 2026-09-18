package kilometros.cliente.entrypoint.gui;

import kilometros.cliente.adaptadores.notificacion.ObservadorCliente;
import kilometros.cliente.aplicacion.dto.ConectarCommand;
import kilometros.cliente.aplicacion.dto.ConvertirKmCommand;
import kilometros.cliente.aplicacion.puertos.entrada.ConvertirKmInputPort;
import kilometros.cliente.aplicacion.puertos.entrada.GestionarConexionInputPort;
import kilometros.cliente.dominio.enums.EstadoConexion;
import kilometros.cliente.dominio.modelos.EventoCliente;
import kilometros.cliente.dominio.modelos.ResultadoConversion;

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
import javax.swing.border.TitledBorder;
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

public class ClienteFrame extends JFrame implements ObservadorCliente {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");

    private final transient ConvertirKmInputPort convertirKmPort;
    private final transient GestionarConexionInputPort gestionarConexionPort;

    // Componentes de Conexión
    private JTextField campoIP;
    private JTextField campoPuerto;
    private JLabel txtEstado;
    private JButton btnConectar;

    // Componentes de Conversión
    private JTextField campoKm;
    private JButton btnConvertir;
    private JButton btnLimpiar;
    private JTextField campoMillas;
    private JTextArea txtMensajeServidor;

    // Componentes de Log
    private JTextArea cajaLog;

    public ClienteFrame(
            ConvertirKmInputPort convertirKmPort,
            GestionarConexionInputPort gestionarConexionPort) {
        super("Cliente");
        this.convertirKmPort = Objects.requireNonNull(convertirKmPort, "El puerto de conversión es obligatorio.");
        this.gestionarConexionPort = Objects.requireNonNull(gestionarConexionPort, "El puerto de gestión es obligatorio.");

        initComponents();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 520);
        setMinimumSize(new Dimension(520, 460));
        setLayout(new BorderLayout(10, 10));

        // Título superior
        JLabel lblTitulo = new JLabel("Cliente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Pestaña 1: Operaciones (Conexión + Conversión)
        JPanel panelOperaciones = new JPanel(new BorderLayout(10, 10));
        panelOperaciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Subpanel Superior: Conexión al Servidor
        JPanel panelConexion = new JPanel(new GridBagLayout());
        panelConexion.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Conexión con el Servidor",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblIP = new JLabel("IP Servidor:");
        lblIP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoIP = new JTextField("127.0.0.1", 12);
        campoIP.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblPuerto = new JLabel("Puerto:");
        lblPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoPuerto = new JTextField("9007", 6);
        campoPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblEstadoTitulo = new JLabel("Estado:");
        lblEstadoTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEstado = new JLabel("DESCONECTADO");
        txtEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtEstado.setForeground(new Color(211, 47, 47));

        btnConectar = new JButton("Conectar");
        btnConectar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConectar.setForeground(new Color(46, 125, 50));
        btnConectar.setPreferredSize(new Dimension(130, 32));
        btnConectar.setFocusPainted(false);
        btnConectar.addActionListener(evt -> alternarConexion());

        gbc.gridx = 0; gbc.gridy = 0;
        panelConexion.add(lblIP, gbc);
        gbc.gridx = 1;
        panelConexion.add(campoIP, gbc);
        gbc.gridx = 2;
        panelConexion.add(lblPuerto, gbc);
        gbc.gridx = 3;
        panelConexion.add(campoPuerto, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelConexion.add(lblEstadoTitulo, gbc);
        gbc.gridx = 1;
        panelConexion.add(txtEstado, gbc);
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panelConexion.add(btnConectar, gbc);

        panelOperaciones.add(panelConexion, BorderLayout.NORTH);

        // Subpanel Central: Conversión de Kilómetros a Millas
        JPanel panelConversion = new JPanel(new GridBagLayout());
        panelConversion.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Conversión",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)
        ));

        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.insets = new Insets(8, 12, 8, 12);
        gbcC.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblKm = new JLabel("Distancia (Km):");
        lblKm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        campoKm = new JTextField("", 12);
        campoKm.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnConvertir = new JButton("Convertir");
        btnConvertir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConvertir.setEnabled(false);
        btnConvertir.setPreferredSize(new Dimension(120, 32));
        btnConvertir.setFocusPainted(false);
        btnConvertir.addActionListener(evt -> ejecutarConversion());

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimpiar.setPreferredSize(new Dimension(90, 32));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(evt -> limpiarCampos());

        JLabel lblMillas = new JLabel("Resultado (Millas):");
        lblMillas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        campoMillas = new JTextField(12);
        campoMillas.setEditable(false);
        campoMillas.setFont(new Font("Segoe UI", Font.BOLD, 15));
        campoMillas.setForeground(new Color(21, 101, 192));

        JLabel lblMensaje = new JLabel("Mensaje:");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMensajeServidor = new JTextArea(2, 25);
        txtMensajeServidor.setEditable(false);
        txtMensajeServidor.setLineWrap(true);
        txtMensajeServidor.setWrapStyleWord(true);
        txtMensajeServidor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMensajeServidor.setBackground(panelConversion.getBackground());
        JScrollPane scrollDetalle = new JScrollPane(txtMensajeServidor);
        scrollDetalle.setBorder(BorderFactory.createEtchedBorder());

        gbcC.gridx = 0; gbcC.gridy = 0;
        panelConversion.add(lblKm, gbcC);
        gbcC.gridx = 1;
        panelConversion.add(campoKm, gbcC);

        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBotonesAccion.add(btnConvertir);
        panelBotonesAccion.add(btnLimpiar);
        gbcC.gridx = 0; gbcC.gridy = 1;
        gbcC.gridwidth = 2;
        panelConversion.add(panelBotonesAccion, gbcC);

        gbcC.gridx = 0; gbcC.gridy = 2;
        gbcC.gridwidth = 1;
        panelConversion.add(lblMillas, gbcC);
        gbcC.gridx = 1;
        panelConversion.add(campoMillas, gbcC);

        gbcC.gridx = 0; gbcC.gridy = 3;
        panelConversion.add(lblMensaje, gbcC);
        gbcC.gridx = 1;
        gbcC.fill = GridBagConstraints.BOTH;
        gbcC.weightx = 1.0;
        gbcC.weighty = 1.0;
        panelConversion.add(scrollDetalle, gbcC);

        panelOperaciones.add(panelConversion, BorderLayout.CENTER);

        tabbedPane.addTab("Operaciones", panelOperaciones);

        // Pestaña 2: Log de Eventos
        JPanel panelLog = new JPanel(new BorderLayout(8, 8));
        panelLog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cajaLog = new JTextArea();
        cajaLog.setEditable(false);
        cajaLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(cajaLog);
        panelLog.add(scrollLog, BorderLayout.CENTER);

        JPanel panelBotonLog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiarLog = new JButton("Limpiar Log");
        btnLimpiarLog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimpiarLog.setFocusPainted(false);
        btnLimpiarLog.addActionListener(evt -> cajaLog.setText(""));
        panelBotonLog.add(btnLimpiarLog);
        panelLog.add(panelBotonLog, BorderLayout.SOUTH);

        tabbedPane.addTab("Log de Eventos", panelLog);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void alternarConexion() {
        if (!gestionarConexionPort.estaConectado()) {
            String ip = campoIP.getText().trim();
            String puertoStr = campoPuerto.getText().trim();

            if (ip.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la dirección IP del servidor.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int puerto;
            try {
                puerto = Integer.parseInt(puertoStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El puerto debe ser un número entero válido.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            btnConectar.setEnabled(false);
            new Thread(() -> {
                try {
                    gestionarConexionPort.conectar(new ConectarCommand(ip, puerto));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Error al Conectar",
                            JOptionPane.ERROR_MESSAGE
                    ));
                } finally {
                    SwingUtilities.invokeLater(() -> btnConectar.setEnabled(true));
                }
            }).start();

        } else {
            btnConectar.setEnabled(false);
            new Thread(() -> {
                try {
                    gestionarConexionPort.desconectar();
                } finally {
                    SwingUtilities.invokeLater(() -> btnConectar.setEnabled(true));
                }
            }).start();
        }
    }

    private void ejecutarConversion() {
        String kmTexto = campoKm.getText().trim();
        if (kmTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la distancia en kilómetros.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double km;
        try {
            km = Double.parseDouble(kmTexto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La distancia en kilómetros debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnConvertir.setEnabled(false);
        new Thread(() -> {
            try {
                ResultadoConversion resultado = convertirKmPort.convertir(new ConvertirKmCommand(km));
                SwingUtilities.invokeLater(() -> {
                    campoMillas.setText(resultado.getMillasFormateadas());
                    txtMensajeServidor.setText(resultado.mensaje());
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error en la Conversión",
                        JOptionPane.ERROR_MESSAGE
                ));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    if (gestionarConexionPort.estaConectado()) {
                        btnConvertir.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private void limpiarCampos() {
        campoKm.setText("");
        campoMillas.setText("");
        txtMensajeServidor.setText("");
        campoKm.requestFocus();
    }

    @Override
    public void onEvento(EventoCliente evento) {
        SwingUtilities.invokeLater(() -> {
            String lineaLog = String.format(
                    "[%s] [%s] %s%n",
                    evento.getFechaHora().format(FORMATO_FECHA),
                    evento.getTipo(),
                    evento.getDescripcion()
            );
            cajaLog.append(lineaLog);
            cajaLog.setCaretPosition(cajaLog.getDocument().getLength());
        });
    }

    @Override
    public void onCambioEstado(EstadoConexion nuevoEstado) {
        SwingUtilities.invokeLater(() -> {
            if (nuevoEstado == EstadoConexion.CONECTADO) {
                txtEstado.setText("CONECTADO");
                txtEstado.setForeground(new Color(46, 125, 50));
                btnConectar.setText("Desconectar");
                btnConectar.setForeground(new Color(211, 47, 47));
                campoIP.setEditable(false);
                campoPuerto.setEditable(false);
                btnConvertir.setEnabled(true);
            } else {
                txtEstado.setText("DESCONECTADO");
                txtEstado.setForeground(new Color(211, 47, 47));
                btnConectar.setText("Conectar");
                btnConectar.setForeground(new Color(46, 125, 50));
                campoIP.setEditable(true);
                campoPuerto.setEditable(true);
                btnConvertir.setEnabled(false);
            }
        });
    }

    // Getters para verificación y pruebas
    public JTextField getCampoIP() { return campoIP; }
    public JTextField getCampoPuerto() { return campoPuerto; }
    public JLabel getTxtEstado() { return txtEstado; }
    public JButton getBtnConectar() { return btnConectar; }
    public JTextField getCampoKm() { return campoKm; }
    public JButton getBtnConvertir() { return btnConvertir; }
    public JTextField getCampoMillas() { return campoMillas; }
    public JTextArea getTxtMensajeServidor() { return txtMensajeServidor; }
    public JTextArea getCajaLog() { return cajaLog; }
}
