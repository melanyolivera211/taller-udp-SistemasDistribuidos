package kilometros.servidor.servidor;

import kilometros.servidor.aplicacion.dto.ConvertirKmCommand;
import kilometros.servidor.aplicacion.dto.ProcesarPeticionUdpCommand;
import kilometros.servidor.aplicacion.dto.ResultadoConversionDto;
import kilometros.servidor.aplicacion.mapper.CalculoMapper;
import kilometros.servidor.aplicacion.mapper.PeticionMapper;
import kilometros.servidor.aplicacion.servicios.ConvertirKmService;
import kilometros.servidor.aplicacion.servicios.ProcesarPeticionUdpService;
import kilometros.servidor.dominio.enums.EstadoServidor;
import kilometros.servidor.dominio.enums.TipoRespuesta;
import kilometros.servidor.dominio.modelos.EventoServidor;
import kilometros.servidor.dominio.modelos.RespuestaCliente;
import kilometros.servidor.dominio.puertos.salida.PuertoNotificacionEvento;
import kilometros.servidor.dominio.puertos.salida.PuertoSalidaRed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AplicacionTest {

    @Test
    @DisplayName("ConvertirKmService debe procesar comando y devolver DTO esperado")
    void testConvertirKmService() {
        CalculoMapper mapper = new CalculoMapper();
        ConvertirKmService service = new ConvertirKmService(mapper);

        ResultadoConversionDto dto = service.convertir(new ConvertirKmCommand(100.0));
        assertNotNull(dto);
        assertEquals(62.1371, dto.millas(), 0.0001);
        assertEquals("62.1371", dto.millasFormateadas());
    }

    @Test
    @DisplayName("ProcesarPeticionUdpService procesa peticiones CONECTAR, DESCONECTAR y CONVERTIR")
    void testProcesarPeticionUdpService() {
        List<RespuestaCliente> respuestasEnviadas = new ArrayList<>();
        List<EventoServidor> eventos = new ArrayList<>();

        PuertoSalidaRed salidaRed = respuestasEnviadas::add;
        PuertoNotificacionEvento notificador = new PuertoNotificacionEvento() {
            @Override
            public void notificarEvento(EventoServidor evento) {
                eventos.add(evento);
            }

            @Override
            public void notificarCambioEstado(EstadoServidor nuevoEstado, int puerto) {
            }
        };

        ProcesarPeticionUdpService service = new ProcesarPeticionUdpService(
                salidaRed,
                notificador,
                new PeticionMapper(),
                new CalculoMapper()
        );

        // 1. CONECTAR
        service.procesar(new ProcesarPeticionUdpCommand("127.0.0.1", 5000, "CONECTAR"));
        assertEquals(1, respuestasEnviadas.size());
        assertEquals(TipoRespuesta.CONECTADO, respuestasEnviadas.get(0).getTipo());

        // 2. CONVERTIR;50.0
        service.procesar(new ProcesarPeticionUdpCommand("127.0.0.1", 5000, "CONVERTIR;50.0"));
        assertEquals(2, respuestasEnviadas.size());
        assertEquals(TipoRespuesta.OK_CONVERSION, respuestasEnviadas.get(1).getTipo());
        assertEquals(31.06855, respuestasEnviadas.get(1).getResultado().millas().valor(), 0.0001);

        // 3. DESCONECTAR
        service.procesar(new ProcesarPeticionUdpCommand("127.0.0.1", 5000, "DESCONECTAR"));
        assertEquals(3, respuestasEnviadas.size());
        assertEquals(TipoRespuesta.DESCONECTADO, respuestasEnviadas.get(2).getTipo());

        // 4. Comando inválido
        service.procesar(new ProcesarPeticionUdpCommand("127.0.0.1", 5000, "INVALIDO"));
        assertEquals(4, respuestasEnviadas.size());
        assertEquals(TipoRespuesta.ERROR, respuestasEnviadas.get(3).getTipo());
    }
}
