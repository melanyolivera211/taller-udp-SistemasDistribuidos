package kilometros.cliente.dominio.vo;

import kilometros.cliente.dominio.excepciones.DestinoInvalidoException;
import java.util.Objects;

public record DestinoServidor(String ip, int puerto) {
    public DestinoServidor {
        if (Objects.isNull(ip) || ip.trim().isEmpty()) {
            throw new DestinoInvalidoException("La dirección IP del servidor no puede estar vacía.");
        }
        if (puerto <= 0 || puerto > 65535) {
            throw new DestinoInvalidoException("El puerto del servidor debe estar entre 1 y 65535.");
        }
        ip = ip.trim();
    }

    public String endpoint() {
        return ip + ":" + puerto;
    }
}
