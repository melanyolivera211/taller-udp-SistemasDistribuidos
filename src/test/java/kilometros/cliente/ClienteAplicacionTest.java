package kilometros.cliente;

import kilometros.cliente.aplicacion.dto.ConectarCommand;
import kilometros.cliente.aplicacion.dto.ConvertirKmCommand;
import kilometros.cliente.aplicacion.excepciones.ClienteRedException;
import kilometros.cliente.aplicacion.mapper.ClienteMapper;
import kilometros.cliente.aplicacion.servicios.ClienteKmService;
import kilometros.cliente.dominio.enums.EstadoConexion;
import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.modelos.EventoCliente;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.puertos.salida.ClienteUdpPort;
import kilometros.cliente.dominio.puertos.salida.PuertoNotificacionCliente;
import kilometros.cliente.dominio.vo.DestinoServidor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClienteAplicacionTest {

    private ClienteMapper mapper;
    private MockClienteUdpPort mockUdpPort;
    private MockNotificador mockNotificador;
    private ClienteKmService service;

    @BeforeEach
    void setUp() {
        mapper = new ClienteMapper();
        mockUdpPort = new MockClienteUdpPort();
        mockNotificador = new MockNotificador();
        service = new ClienteKmService(mockUdpPort, mockNotificador, mapper);
    }

    @Test
    @DisplayName("Aplicación: Conectar cambia estado a CONECTADO y notifica eventos")
    void testConectarExitoso() {
        service.conectar(new ConectarCommand("127.0.0.1", 9007));

        assertTrue(service.estaConectado());
        assertEquals("127.0.0.1:9007", service.getDestinoActual().endpoint());
        assertEquals(EstadoConexion.CONECTADO, mockNotificador.ultimoEstado);
        assertFalse(mockNotificador.eventos.isEmpty());
    }

    @Test
    @DisplayName("Aplicación: Convertir sin estar conectado lanza ClienteRedException")
    void testConvertirSinConexionLanzaExcepcion() {
        assertThrows(
                ClienteRedException.class,
                () -> service.convertir(new ConvertirKmCommand(10.0))
        );
    }

    @Test
    @DisplayName("Aplicación: Convertir conectado delega correctamente al puerto UDP")
    void testConvertirConectado() {
        service.conectar(new ConectarCommand("127.0.0.1", 9007));
        ResultadoConversion resultado = service.convertir(new ConvertirKmCommand(10.0));

        assertNotNull(resultado);
        assertEquals("6.2137", resultado.getMillasFormateadas());
        assertTrue(mockNotificador.eventos.stream().anyMatch(e -> e.getTipo().equals("RESPUESTA")));
    }

    @Test
    @DisplayName("Aplicación: Desconectar actualiza estado a DESCONECTADO")
    void testDesconectar() {
        service.conectar(new ConectarCommand("127.0.0.1", 9007));
        service.desconectar();

        assertFalse(service.estaConectado());
        assertEquals(EstadoConexion.DESCONECTADO, mockNotificador.ultimoEstado);
    }

    private static class MockClienteUdpPort implements ClienteUdpPort {
        private boolean conectado = false;
        private DestinoServidor destinoActual = null;

        @Override
        public void conectar(DestinoServidor destino) {
            this.destinoActual = destino;
            this.conectado = true;
        }

        @Override
        public ResultadoConversion convertir(DatosConversion datos) {
            return new ResultadoConversion(6.21371, "10.00 km equivalen a 6.2137 millas");
        }

        @Override
        public void desconectar(DestinoServidor destino) {
            this.conectado = false;
            this.destinoActual = null;
        }

        @Override
        public boolean estaConectado() {
            return conectado;
        }

        @Override
        public DestinoServidor getDestinoActual() {
            return destinoActual;
        }
    }

    private static class MockNotificador implements PuertoNotificacionCliente {
        private EstadoConexion ultimoEstado;
        private final List<EventoCliente> eventos = new ArrayList<>();

        @Override
        public void notificarEvento(EventoCliente evento) {
            eventos.add(evento);
        }

        @Override
        public void notificarCambioEstado(EstadoConexion nuevoEstado) {
            this.ultimoEstado = nuevoEstado;
        }
    }
}
