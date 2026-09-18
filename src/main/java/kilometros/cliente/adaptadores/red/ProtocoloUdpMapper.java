package kilometros.cliente.adaptadores.red;

import kilometros.cliente.dominio.excepciones.RespuestaServidorException;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.vo.Kilometros;
import java.util.Objects;

public final class ProtocoloUdpMapper {

    public String toPayloadConectar() {
        return "CONECTAR";
    }

    public String toPayloadConvertir(Kilometros kilometros) {
        Objects.requireNonNull(kilometros, "Los kilómetros no pueden ser nulos.");
        return "CONVERTIR;" + kilometros.formateado();
    }

    public String toPayloadDesconectar() {
        return "DESCONECTAR";
    }

    public String parseRespuestaConectar(String payload) {
        validarPayloadNoVacio(payload);
        String texto = payload.trim();

        if (texto.startsWith("CONECTADO_OK;")) {
            return texto.substring("CONECTADO_OK;".length()).trim();
        }
        if (texto.startsWith("ERROR;")) {
            throw new RespuestaServidorException(extraerMensajeError(texto));
        }

        throw new RespuestaServidorException("Respuesta de conexión no reconocida: " + texto);
    }

    public ResultadoConversion parseRespuestaConversion(String payload) {
        validarPayloadNoVacio(payload);
        String texto = payload.trim();

        if (texto.startsWith("OK_CONVERSION;")) {
            String[] partes = texto.split(";", 3);
            if (partes.length < 3) {
                throw new RespuestaServidorException("Formato incompleto de conversión recibido: " + texto);
            }
            try {
                double millas = Double.parseDouble(partes[1].trim().replace(',', '.'));
                String mensaje = partes[2].trim();
                return new ResultadoConversion(millas, mensaje);
            } catch (NumberFormatException excepcion) {
                throw new RespuestaServidorException("El servidor devolvió un valor de millas no numérico: " + partes[1]);
            }
        }

        if (texto.startsWith("ERROR;")) {
            throw new RespuestaServidorException(extraerMensajeError(texto));
        }

        throw new RespuestaServidorException("Respuesta de conversión no reconocida: " + texto);
    }

    public String parseRespuestaDesconectar(String payload) {
        validarPayloadNoVacio(payload);
        String texto = payload.trim();

        if (texto.startsWith("DESCONECTADO_OK;")) {
            return texto.substring("DESCONECTADO_OK;".length()).trim();
        }
        if (texto.startsWith("ERROR;")) {
            throw new RespuestaServidorException(extraerMensajeError(texto));
        }

        return texto;
    }

    private static void validarPayloadNoVacio(String payload) {
        if (Objects.isNull(payload) || payload.isBlank()) {
            throw new RespuestaServidorException("Respuesta vacía o nula recibida del servidor.");
        }
    }

    private static String extraerMensajeError(String texto) {
        String[] partes = texto.split(";", 2);
        return partes.length > 1 ? partes[1].trim() : "Error genérico devuelto por el servidor.";
    }
}
