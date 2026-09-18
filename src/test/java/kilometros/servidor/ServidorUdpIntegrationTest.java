package kilometros.servidor;

import kilometros.servidor.adaptadores.red.AdaptadorControlServidorRed;
import kilometros.servidor.adaptadores.red.AdaptadorNotificacionEvento;
import kilometros.servidor.adaptadores.red.AdaptadorSalidaUdp;
import kilometros.servidor.adaptadores.red.CanalUdp;
import kilometros.servidor.adaptadores.red.mapper.UdpNetworkMapper;
import kilometros.servidor.aplicacion.mapper.CalculoMapper;
import kilometros.servidor.aplicacion.mapper.PeticionMapper;
import kilometros.servidor.aplicacion.puertos.entrada.GestionarServidorInputPort;
import kilometros.servidor.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import kilometros.servidor.aplicacion.servicios.GestionarServidorService;
import kilometros.servidor.aplicacion.servicios.ProcesarPeticionUdpService;
import kilometros.servidor.dominio.puertos.salida.ControladorServidorRedPort;
import kilometros.servidor.dominio.puertos.salida.PuertoSalidaRed;
import kilometros.servidor.entrypoint.udp.ReceptorPeticionesUdp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ServidorUdpIntegrationTest {

    private static final int PUERTO_TEST = 9123;
    private GestionarServidorInputPort gestionarServidorPort;
    private DatagramSocket socketCliente;

    @BeforeEach
    void setUp() throws Exception {
        CanalUdp canalUdp = new CanalUdp();
        AdaptadorNotificacionEvento notificador = new AdaptadorNotificacionEvento();
        UdpNetworkMapper redMapper = new UdpNetworkMapper();
        PuertoSalidaRed puertoSalidaRed = new AdaptadorSalidaUdp(canalUdp, redMapper, notificador);

        PeticionMapper peticionMapper = new PeticionMapper();
        CalculoMapper calculoMapper = new CalculoMapper();

        ProcesarPeticionUdpInputPort procesarPeticionPort = new ProcesarPeticionUdpService(
                puertoSalidaRed,
                notificador,
                peticionMapper,
                calculoMapper
        );

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

        gestionarServidorPort = new GestionarServidorService(controladorRed, notificador);
        gestionarServidorPort.iniciarServidor(PUERTO_TEST);

        socketCliente = new DatagramSocket();
        socketCliente.setSoTimeout(3000);
    }

    @AfterEach
    void tearDown() {
        if (socketCliente != null && !socketCliente.isClosed()) {
            socketCliente.close();
        }
        if (gestionarServidorPort != null && gestionarServidorPort.estaActivo()) {
            gestionarServidorPort.detenerServidor();
        }
    }

    @Test
    @DisplayName("Integración UDP: Cliente envía datagramas CONECTAR y CONVERTIR y recibe respuestas válidas")
    void testComunicacionUdpCompleta() throws Exception {
        InetAddress host = InetAddress.getByName("127.0.0.1");

        // 1. Handshake lógico CONECTAR
        byte[] bufferEnvio = "CONECTAR".getBytes(StandardCharsets.UTF_8);
        socketCliente.send(new DatagramPacket(bufferEnvio, bufferEnvio.length, host, PUERTO_TEST));

        byte[] bufferRecibo = new byte[2048];
        DatagramPacket paqueteRecibido = new DatagramPacket(bufferRecibo, bufferRecibo.length);
        socketCliente.receive(paqueteRecibido);
        String respuestaConectar = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength(), StandardCharsets.UTF_8);
        assertTrue(respuestaConectar.startsWith("CONECTADO_OK;"), "Respuesta esperada: CONECTADO_OK, pero fue: " + respuestaConectar);

        // 2. Solicitud de conversión CONVERTIR;15.5
        bufferEnvio = "CONVERTIR;15.5".getBytes(StandardCharsets.UTF_8);
        socketCliente.send(new DatagramPacket(bufferEnvio, bufferEnvio.length, host, PUERTO_TEST));

        paqueteRecibido = new DatagramPacket(bufferRecibo, bufferRecibo.length);
        socketCliente.receive(paqueteRecibido);
        String respuestaConversion = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength(), StandardCharsets.UTF_8);
        assertTrue(respuestaConversion.startsWith("OK_CONVERSION;"), "Respuesta esperada: OK_CONVERSION, pero fue: " + respuestaConversion);
        assertTrue(respuestaConversion.contains("9.6313"), "Debe contener las millas calculadas (15.5 * 0.621371 = 9.6313)");

        // 3. Desconexión lógica DESCONECTAR
        bufferEnvio = "DESCONECTAR".getBytes(StandardCharsets.UTF_8);
        socketCliente.send(new DatagramPacket(bufferEnvio, bufferEnvio.length, host, PUERTO_TEST));

        paqueteRecibido = new DatagramPacket(bufferRecibo, bufferRecibo.length);
        socketCliente.receive(paqueteRecibido);
        String respuestaDesconectar = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength(), StandardCharsets.UTF_8);
        assertTrue(respuestaDesconectar.startsWith("DESCONECTADO_OK;"), "Respuesta esperada: DESCONECTADO_OK, pero fue: " + respuestaDesconectar);
    }
}
