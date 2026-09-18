package kilometros.cliente.cliente;

import kilometros.cliente.dominio.excepciones.DestinoInvalidoException;
import kilometros.cliente.dominio.excepciones.KilometrosInvalidosException;
import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.modelos.EventoCliente;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.vo.DestinoServidor;
import kilometros.cliente.dominio.vo.Kilometros;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DominioClienteTest {

    @Test
    @DisplayName("Kilometros: Creación válida y formateo numérico correcto")
    void testKilometrosValidos() {
        Kilometros km = new Kilometros(15.5);
        assertEquals(15.5, km.valor(), 0.0001);
        assertEquals("15.50", km.formateado());
    }

    @Test
    @DisplayName("Kilometros: Valor negativo debe lanzar KilometrosInvalidosException")
    void testKilometrosNegativosLanzaExcepcion() {
        assertThrows(KilometrosInvalidosException.class, () -> new Kilometros(-0.1));
    }

    @Test
    @DisplayName("Kilometros: Valor NaN o Infinito debe lanzar KilometrosInvalidosException")
    void testKilometrosNaNLanzaExcepcion() {
        assertThrows(KilometrosInvalidosException.class, () -> new Kilometros(Double.NaN));
        assertThrows(KilometrosInvalidosException.class, () -> new Kilometros(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("DestinoServidor: Creación válida con IP y puerto en rango")
    void testDestinoServidorValido() {
        DestinoServidor destino = new DestinoServidor("192.168.1.10", 9007);
        assertEquals("192.168.1.10", destino.ip());
        assertEquals(9007, destino.puerto());
        assertEquals("192.168.1.10:9007", destino.endpoint());
    }

    @Test
    @DisplayName("DestinoServidor: IP vacía o nula lanza DestinoInvalidoException")
    void testDestinoServidorIpInvalida() {
        assertThrows(DestinoInvalidoException.class, () -> new DestinoServidor(null, 9007));
        assertThrows(DestinoInvalidoException.class, () -> new DestinoServidor("   ", 9007));
    }

    @Test
    @DisplayName("DestinoServidor: Puerto fuera de rango lanza DestinoInvalidoException")
    void testDestinoServidorPuertoInvalido() {
        assertThrows(DestinoInvalidoException.class, () -> new DestinoServidor("127.0.0.1", 0));
        assertThrows(DestinoInvalidoException.class, () -> new DestinoServidor("127.0.0.1", -5));
        assertThrows(DestinoInvalidoException.class, () -> new DestinoServidor("127.0.0.1", 65536));
    }

    @Test
    @DisplayName("DatosConversion: Creación exitosa y validación de nulidad")
    void testDatosConversion() {
        Kilometros km = new Kilometros(25.0);
        DestinoServidor destino = new DestinoServidor("127.0.0.1", 9007);
        DatosConversion datos = new DatosConversion(km, destino);

        assertSame(km, datos.kilometros());
        assertSame(destino, datos.destino());
        assertThrows(NullPointerException.class, () -> new DatosConversion(null, destino));
        assertThrows(NullPointerException.class, () -> new DatosConversion(km, null));
    }

    @Test
    @DisplayName("ResultadoConversion: Formateo de 4 decimales y validaciones")
    void testResultadoConversion() {
        ResultadoConversion resultado = new ResultadoConversion(6.21371, "10.00 km equivalen a 6.2137 millas");
        assertEquals(6.21371, resultado.millas(), 0.00001);
        assertEquals("6.2137", resultado.getMillasFormateadas());
        assertEquals("10.00 km equivalen a 6.2137 millas", resultado.mensaje());
    }

    @Test
    @DisplayName("EventoCliente: Registro de fecha, tipo y descripción")
    void testEventoCliente() {
        EventoCliente evento = new EventoCliente("INFO", "Prueba de evento");
        assertNotNull(evento.getFechaHora());
        assertEquals("INFO", evento.getTipo());
        assertEquals("Prueba de evento", evento.getDescripcion());
    }
}
