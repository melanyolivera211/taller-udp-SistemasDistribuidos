package kilometros.cliente.cliente;

import kilometros.cliente.adaptadores.red.ProtocoloUdpMapper;
import kilometros.cliente.dominio.excepciones.RespuestaServidorException;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.vo.Kilometros;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtocoloUdpMapperTest {

    private ProtocoloUdpMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProtocoloUdpMapper();
    }

    @Test
    @DisplayName("Serialización: Comandos CONECTAR, CONVERTIR y DESCONECTAR")
    void testSerializacionComandos() {
        assertEquals("CONECTAR", mapper.toPayloadConectar());
        assertEquals("CONVERTIR;10.50", mapper.toPayloadConvertir(new Kilometros(10.5)));
        assertEquals("DESCONECTAR", mapper.toPayloadDesconectar());
    }

    @Test
    @DisplayName("Deserialización: CONECTADO_OK devuelve mensaje confirmatorio")
    void testParseRespuestaConectar() {
        String mensaje = mapper.parseRespuestaConectar("CONECTADO_OK;Servidor UDP listo para recibir conversiones");
        assertEquals("Servidor UDP listo para recibir conversiones", mensaje);
    }

    @Test
    @DisplayName("Deserialización: ERROR en conexión lanza RespuestaServidorException")
    void testParseRespuestaConectarConError() {
        RespuestaServidorException ex = assertThrows(
                RespuestaServidorException.class,
                () -> mapper.parseRespuestaConectar("ERROR;Servidor no disponible")
        );
        assertEquals("Servidor no disponible", ex.getMessage());
    }

    @Test
    @DisplayName("Deserialización: OK_CONVERSION parsea correctamente millas y mensaje")
    void testParseRespuestaConversion() {
        ResultadoConversion resultado = mapper.parseRespuestaConversion("OK_CONVERSION;6.2137;10.00 km equivalen a 6.2137 millas");
        assertEquals(6.2137, resultado.millas(), 0.0001);
        assertEquals("6.2137", resultado.getMillasFormateadas());
        assertEquals("10.00 km equivalen a 6.2137 millas", resultado.mensaje());
    }

    @Test
    @DisplayName("Deserialización: ERROR en conversión propaga mensaje como excepción")
    void testParseRespuestaConversionConError() {
        RespuestaServidorException ex = assertThrows(
                RespuestaServidorException.class,
                () -> mapper.parseRespuestaConversion("ERROR;El valor de kilómetros debe ser numérico.")
        );
        assertEquals("El valor de kilómetros debe ser numérico.", ex.getMessage());
    }

    @Test
    @DisplayName("Deserialización: Formato corrupto lanza RespuestaServidorException")
    void testParseRespuestaCorrupta() {
        assertThrows(RespuestaServidorException.class, () -> mapper.parseRespuestaConversion("BASURA_SIN_FORMATO"));
        assertThrows(RespuestaServidorException.class, () -> mapper.parseRespuestaConversion(""));
    }

    @Test
    @DisplayName("Deserialización: DESCONECTADO_OK devuelve mensaje exitoso")
    void testParseRespuestaDesconectar() {
        String mensaje = mapper.parseRespuestaDesconectar("DESCONECTADO_OK;Sesión finalizada");
        assertEquals("Sesión finalizada", mensaje);
    }
}
