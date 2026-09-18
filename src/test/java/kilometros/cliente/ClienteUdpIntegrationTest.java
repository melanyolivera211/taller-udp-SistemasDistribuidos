package kilometros.cliente.cliente;

import kilometros.cliente.adaptadores.red.AdaptadorClienteUdp;
import kilometros.cliente.adaptadores.red.CanalUdp;
import kilometros.cliente.adaptadores.red.ProtocoloUdpMapper;
import kilometros.cliente.aplicacion.excepciones.ClienteRedException;
import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.vo.DestinoServidor;
import kilometros.cliente.dominio.vo.Kilometros;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ClienteUdpIntegrationTest {

    private static final int PUERTO_TEST = 9876;

    private DatagramSocket socketServidorSimulado;
    private Thread hiloServidor;
    private final AtomicBoolean servidorActivo = new AtomicBoolean(false);

    private CanalUdp canalUdp;
    private ProtocoloUdpMapper mapper;
    private AdaptadorClienteUdp adaptador;

    @BeforeEach
    void setUp() throws Exception {
        socketServidorSimulado = new DatagramSocket(PUERTO_TEST);
        servidorActivo.set(true);

        hiloServidor = new Thread(() -> {
            byte[] buffer = new byte[2048];
            while (servidorActivo.get()) {
                try {
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socketServidorSimulado.receive(paquete);
                    String mensaje = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8);

                    String respuesta;
                    if ("CONECTAR".equals(mensaje)) {
                        respuesta = "CONECTADO_OK;Servidor UDP listo para recibir conversiones de Km a Millas";
                    } else if (mensaje.startsWith("CONVERTIR;")) {
                        respuesta = "OK_CONVERSION;9.6313;15.50 km equivalen a 9.6313 millas";
                    } else if ("DESCONECTAR".equals(mensaje)) {
                        respuesta = "DESCONECTADO_OK;Sesión finalizada";
                    } else {
                        respuesta = "ERROR;Comando no reconocido";
                    }

                    byte[] respBytes = respuesta.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket paqueteResp = new DatagramPacket(
                            respBytes, respBytes.length, paquete.getAddress(), paquete.getPort()
                    );
                    socketServidorSimulado.send(paqueteResp);
                } catch (IOException ignored) {
                    // Socket cerrado al terminar el test
                }
            }
        });
        hiloServidor.start();

        canalUdp = new CanalUdp();
        mapper = new ProtocoloUdpMapper();
        adaptador = new AdaptadorClienteUdp(canalUdp, mapper, 1500);
    }

    @AfterEach
    void tearDown() {
        servidorActivo.set(false);
        if (socketServidorSimulado != null && !socketServidorSimulado.isClosed()) {
            socketServidorSimulado.close();
        }
        if (adaptador != null && adaptador.estaConectado()) {
            adaptador.desconectar(new DestinoServidor("127.0.0.1", PUERTO_TEST));
        }
    }

    @Test
    @DisplayName("Integración UDP: Ciclo completo conectar -> convertir -> desconectar")
    void testFlujoCompletoUdp() {
        DestinoServidor destino = new DestinoServidor("127.0.0.1", PUERTO_TEST);

        // 1. Conectar
        adaptador.conectar(destino);
        assertTrue(adaptador.estaConectado());
        assertEquals(destino, adaptador.getDestinoActual());

        // 2. Convertir
        DatosConversion datos = new DatosConversion(new Kilometros(15.5), destino);
        ResultadoConversion resultado = adaptador.convertir(datos);
        assertNotNull(resultado);
        assertEquals("9.6313", resultado.getMillasFormateadas());
        assertTrue(resultado.mensaje().contains("15.50 km equivalen a 9.6313 millas"));

        // 3. Desconectar
        adaptador.desconectar(destino);
        assertFalse(adaptador.estaConectado());
    }

    @Test
    @DisplayName("Integración UDP: Timeout controlado cuando el servidor no responde")
    void testTimeoutServidorInactivo() {
        // Apagar servidor
        servidorActivo.set(false);
        socketServidorSimulado.close();

        DestinoServidor destinoInactivo = new DestinoServidor("127.0.0.1", 9999);
        CanalUdp canalTimeout = new CanalUdp();
        AdaptadorClienteUdp adaptadorTimeout = new AdaptadorClienteUdp(canalTimeout, mapper, 500);

        ClienteRedException ex = assertThrows(
                ClienteRedException.class,
                () -> adaptadorTimeout.conectar(destinoInactivo)
        );
        assertTrue(ex.getMessage().contains("Tiempo de espera agotado"));
        assertFalse(adaptadorTimeout.estaConectado());
    }
}
