package kilometros;

import kilometros.cliente.adaptadores.notificacion.AdaptadorNotificacionCliente;
import kilometros.cliente.adaptadores.red.AdaptadorClienteUdp;
import kilometros.cliente.adaptadores.red.CanalUdp;
import kilometros.cliente.adaptadores.red.ProtocoloUdpMapper;
import kilometros.cliente.aplicacion.dto.ConectarCommand;
import kilometros.cliente.aplicacion.dto.ConvertirKmCommand;
import kilometros.cliente.aplicacion.mapper.ClienteMapper;
import kilometros.cliente.aplicacion.servicios.ClienteKmService;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.servidor.adaptadores.red.AdaptadorControlServidorRed;
import kilometros.servidor.adaptadores.red.AdaptadorNotificacionEvento;
import kilometros.servidor.adaptadores.red.AdaptadorSalidaUdp;
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

import static org.junit.jupiter.api.Assertions.*;

class IntegracionEndToEndTest {

    private static final int PUERTO_E2E = 9555;

    // Servidor real
    private GestionarServidorInputPort gestionarServidorPort;

    // Cliente real
    private ClienteKmService clienteService;

    @BeforeEach
    void setUp() {
        // 1. Inicializar Servidor
        kilometros.servidor.adaptadores.red.CanalUdp canalServidor = new kilometros.servidor.adaptadores.red.CanalUdp();
        AdaptadorNotificacionEvento notificadorServidor = new AdaptadorNotificacionEvento();
        UdpNetworkMapper networkMapper = new UdpNetworkMapper();
        PuertoSalidaRed salidaRed = new AdaptadorSalidaUdp(canalServidor, networkMapper, notificadorServidor);

        PeticionMapper peticionMapper = new PeticionMapper();
        CalculoMapper calculoMapper = new CalculoMapper();
        ProcesarPeticionUdpInputPort procesarPort = new ProcesarPeticionUdpService(
                salidaRed, notificadorServidor, peticionMapper, calculoMapper
        );

        ReceptorPeticionesUdp receptor = new ReceptorPeticionesUdp(canalServidor, procesarPort, notificadorServidor);
        ControladorServidorRedPort controladorRed = new AdaptadorControlServidorRed(
                canalServidor, receptor::iniciar, receptor::detener
        );
        gestionarServidorPort = new GestionarServidorService(controladorRed, notificadorServidor);
        gestionarServidorPort.iniciarServidor(PUERTO_E2E);

        // 2. Inicializar Cliente
        CanalUdp canalCliente = new CanalUdp();
        ProtocoloUdpMapper protocoloMapper = new ProtocoloUdpMapper();
        AdaptadorClienteUdp clienteUdpPort = new AdaptadorClienteUdp(canalCliente, protocoloMapper, 3000);
        AdaptadorNotificacionCliente notificadorCliente = new AdaptadorNotificacionCliente();
        ClienteMapper clienteMapper = new ClienteMapper();

        clienteService = new ClienteKmService(clienteUdpPort, notificadorCliente, clienteMapper);
    }

    @AfterEach
    void tearDown() {
        if (clienteService != null && clienteService.estaConectado()) {
            clienteService.desconectar();
        }
        if (gestionarServidorPort != null && gestionarServidorPort.estaActivo()) {
            gestionarServidorPort.detenerServidor();
        }
    }

    @Test
    @DisplayName("E2E: Cliente real y Servidor real se comunican por UDP exitosamente")
    void testComunicacionCompletaClienteServidor() {
        // 1. Handshake de conexión lógica
        clienteService.conectar(new ConectarCommand("127.0.0.1", PUERTO_E2E));
        assertTrue(clienteService.estaConectado(), "El cliente debe figurar como conectado");

        // 2. Envío de petición y recepción de conversión (100 km -> 62.1371 millas)
        ResultadoConversion resultado = clienteService.convertir(new ConvertirKmCommand(100.0));
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals("62.1371", resultado.getMillasFormateadas(), "100 km deben convertirse a 62.1371 millas");
        assertTrue(resultado.mensaje().contains("100.00 km equivalen a 62.1371 millas"));

        // 3. Desconexión lógica
        clienteService.desconectar();
        assertFalse(clienteService.estaConectado(), "El cliente debe figurar como desconectado");
    }
}
