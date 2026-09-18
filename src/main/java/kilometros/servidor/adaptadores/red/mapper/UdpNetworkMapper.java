package kilometros.servidor.adaptadores.red.mapper;

import kilometros.servidor.adaptadores.red.response.UdpResponse;
import kilometros.servidor.dominio.excepciones.RespuestaInvalidaException;
import kilometros.servidor.dominio.modelos.RespuestaCliente;
import kilometros.servidor.dominio.modelos.ResultadoConversion;
import java.util.Objects;

public final class UdpNetworkMapper {

    public UdpResponse toNetworkResponse(RespuestaCliente respuesta) {
        if (Objects.isNull(respuesta)) {
            throw new RespuestaInvalidaException("No se puede serializar una respuesta nula.");
        }

        String payload = switch (respuesta.getTipo()) {
            case CONECTADO -> "CONECTADO_OK;" + respuesta.getMensaje();
            case DESCONECTADO -> "DESCONECTADO_OK;" + respuesta.getMensaje();
            case OK_CONVERSION -> buildPayloadConversion(respuesta.getResultado());
            case ERROR -> "ERROR;" + (Objects.nonNull(respuesta.getMensaje()) ? respuesta.getMensaje() : "Error desconocido.");
        };

        return new UdpResponse(
                payload,
                respuesta.getDestinatario().ip(),
                respuesta.getDestinatario().puerto()
        );
    }

    private static String buildPayloadConversion(ResultadoConversion resultado) {
        if (Objects.isNull(resultado)) {
            throw new RespuestaInvalidaException("No se puede serializar una conversión sin resultado.");
        }
        return String.format(
                "OK_CONVERSION;%s;%s",
                resultado.getMillasFormateadas(),
                resultado.mensaje()
        );
    }
}
