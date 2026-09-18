package kilometros;

import kilometros.dominio.excepciones.DestinatarioInvalidoException;
import kilometros.dominio.excepciones.DominioException;
import kilometros.dominio.excepciones.KilometrosInvalidosException;
import kilometros.dominio.excepciones.PuertoInvalidoException;
import kilometros.dominio.modelos.CalculoKmAMillas;
import kilometros.dominio.modelos.ResultadoConversion;
import kilometros.dominio.vo.Destinatario;
import kilometros.dominio.vo.Kilometros;
import kilometros.dominio.vo.Millas;
import kilometros.dominio.vo.PuertoRed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DominioTest {

    @Test
    @DisplayName("Kilómetros válidos deben crearse y formatearse correctamente")
    void testKilometrosValidos() {
        Kilometros km = new Kilometros(10.5);
        assertEquals(10.5, km.valor(), 0.0001);
        assertEquals("10.50", km.formateado());

        Kilometros kmCero = new Kilometros(0.0);
        assertEquals(0.0, kmCero.valor(), 0.0001);
    }

    @Test
    @DisplayName("Kilómetros negativos o inválidos deben lanzar KilometrosInvalidosException")
    void testKilometrosInvalidos() {
        assertThrows(KilometrosInvalidosException.class, () -> new Kilometros(-1.0));
        assertThrows(KilometrosInvalidosException.class, () -> new Kilometros(Double.NaN));
        assertThrows(KilometrosInvalidosException.class, () -> new Kilometros(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Cálculo de kilómetros a millas debe ser exacto con el factor 0.621371")
    void testCalculoConversion() {
        Kilometros km = new Kilometros(10.0);
        CalculoKmAMillas calculo = new CalculoKmAMillas(km);
        ResultadoConversion res = calculo.calcular();

        assertEquals(6.21371, res.millas().valor(), 0.00001);
        assertEquals("6.2137", res.getMillasFormateadas());
        assertTrue(res.mensaje().contains("10.00 km equivalen a 6.2137 millas"));
    }

    @Test
    @DisplayName("Destinatario válido e inválido")
    void testDestinatario() {
        Destinatario d = new Destinatario("127.0.0.1", 9007);
        assertEquals("127.0.0.1", d.ip());
        assertEquals(9007, d.puerto());
        assertEquals("127.0.0.1:9007", d.endpoint());

        assertThrows(DestinatarioInvalidoException.class, () -> new Destinatario("", 9007));
        assertThrows(DestinatarioInvalidoException.class, () -> new Destinatario("127.0.0.1", 0));
        assertThrows(DestinatarioInvalidoException.class, () -> new Destinatario("127.0.0.1", 70000));
    }

    @Test
    @DisplayName("Puerto de red con rango válido e inválido")
    void testPuertoRed() {
        PuertoRed p = new PuertoRed(9007);
        assertEquals(9007, p.valor());

        assertThrows(PuertoInvalidoException.class, () -> new PuertoRed(80));
        assertThrows(PuertoInvalidoException.class, () -> new PuertoRed(1023));
        assertThrows(PuertoInvalidoException.class, () -> new PuertoRed(65536));
    }
}
