package kilometros.dominio.vo;

import kilometros.dominio.excepciones.DestinatarioInvalidoException;
import java.util.Objects;

public record Destinatario(String ip, int puerto) {
    public Destinatario {
        if (Objects.isNull(ip) || ip.isBlank()) {
            throw new DestinatarioInvalidoException("La dirección IP del destinatario no puede ser nula ni vacía.");
        }
        if (puerto < 1 || puerto > 65535) {
            throw new DestinatarioInvalidoException("El puerto del destinatario debe estar en el rango 1 - 65535.");
        }
    }

    public String endpoint() {
        return ip + ":" + puerto;
    }
}
