package kilometros.cliente.dominio.modelos;

import kilometros.cliente.dominio.vo.DestinoServidor;
import kilometros.cliente.dominio.vo.Kilometros;
import java.util.Objects;

public record DatosConversion(Kilometros kilometros, DestinoServidor destino) {
    public DatosConversion {
        Objects.requireNonNull(kilometros, "Los kilómetros no pueden ser nulos.");
        Objects.requireNonNull(destino, "El destino del servidor no puede ser nulo.");
    }
}
