package kilometros.servidor;

import kilometros.servidor.adaptadores.red.mapper.UdpNetworkMapper;
import kilometros.servidor.adaptadores.red.response.UdpResponse;
import kilometros.servidor.dominio.modelos.CalculoKmAMillas;
import kilometros.servidor.dominio.modelos.RespuestaCliente;
import kilometros.servidor.dominio.vo.Destinatario;
import kilometros.servidor.dominio.vo.Kilometros;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedTest {

    @Test
    @DisplayName("UdpNetworkMapper debe formatear correctamente las respuestas hacia datagramas")
    void testUdpNetworkMapper() {
        UdpNetworkMapper mapper = new UdpNetworkMapper();
        Destinatario dest = new Destinatario("192.168.1.50", 6000);

        // Conectado
        RespuestaCliente r1 = RespuestaCliente.conectado(dest, "Listo");
        UdpResponse u1 = mapper.toNetworkResponse(r1);
        assertEquals("CONECTADO_OK;Listo", u1.getPayload());
        assertEquals("192.168.1.50", u1.getIpDestino());
        assertEquals(6000, u1.getPuertoDestino());

        // Desconectado
        RespuestaCliente r2 = RespuestaCliente.desconectado(dest, "Adios");
        UdpResponse u2 = mapper.toNetworkResponse(r2);
        assertEquals("DESCONECTADO_OK;Adios", u2.getPayload());

        // Conversión
        CalculoKmAMillas calc = new CalculoKmAMillas(new Kilometros(10.0));
        RespuestaCliente r3 = RespuestaCliente.conversionExitosa(dest, calc.calcular());
        UdpResponse u3 = mapper.toNetworkResponse(r3);
        assertTrue(u3.getPayload().startsWith("OK_CONVERSION;6.2137;"));

        // Error
        RespuestaCliente r4 = RespuestaCliente.error(dest, "Algo fallo");
        UdpResponse u4 = mapper.toNetworkResponse(r4);
        assertEquals("ERROR;Algo fallo", u4.getPayload());
    }
}
