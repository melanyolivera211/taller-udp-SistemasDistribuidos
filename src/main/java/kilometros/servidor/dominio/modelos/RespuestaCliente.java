package kilometros.servidor.dominio.modelos;

import kilometros.servidor.dominio.enums.TipoRespuesta;
import kilometros.servidor.dominio.vo.Destinatario;
import java.util.Objects;

public class RespuestaCliente {
    private final Destinatario destinatario;
    private final TipoRespuesta tipo;
    private final String mensaje;
    private final ResultadoConversion resultado;

    private RespuestaCliente(Destinatario destinatario, TipoRespuesta tipo, String mensaje, ResultadoConversion resultado) {
        this.destinatario = Objects.requireNonNull(destinatario, "El destinatario es obligatorio.");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de respuesta es obligatorio.");
        this.mensaje = mensaje;
        this.resultado = resultado;
    }

    public static RespuestaCliente conectado(Destinatario destinatario, String mensaje) {
        return new RespuestaCliente(destinatario, TipoRespuesta.CONECTADO, mensaje, null);
    }

    public static RespuestaCliente desconectado(Destinatario destinatario, String mensaje) {
        return new RespuestaCliente(destinatario, TipoRespuesta.DESCONECTADO, mensaje, null);
    }

    public static RespuestaCliente conversionExitosa(Destinatario destinatario, ResultadoConversion resultado) {
        Objects.requireNonNull(resultado, "El resultado de la conversión es obligatorio.");
        return new RespuestaCliente(destinatario, TipoRespuesta.OK_CONVERSION, resultado.mensaje(), resultado);
    }

    public static RespuestaCliente error(Destinatario destinatario, String mensaje) {
        return new RespuestaCliente(destinatario, TipoRespuesta.ERROR, mensaje, null);
    }

    public Destinatario getDestinatario() {
        return destinatario;
    }

    public TipoRespuesta getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public ResultadoConversion getResultado() {
        return resultado;
    }
}
