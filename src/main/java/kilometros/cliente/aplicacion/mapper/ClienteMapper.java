package kilometros.cliente.aplicacion.mapper;

import kilometros.cliente.aplicacion.dto.ConectarCommand;
import kilometros.cliente.aplicacion.dto.ConvertirKmCommand;
import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.vo.DestinoServidor;
import kilometros.cliente.dominio.vo.Kilometros;
import java.util.Objects;

public final class ClienteMapper {

    public DestinoServidor toDestinoServidor(ConectarCommand comando) {
        Objects.requireNonNull(comando, "El comando de conexión no puede ser nulo.");
        return new DestinoServidor(comando.ip(), comando.puerto());
    }

    public Kilometros toKilometros(ConvertirKmCommand comando) {
        Objects.requireNonNull(comando, "El comando de conversión no puede ser nulo.");
        return new Kilometros(comando.kilometros());
    }

    public DatosConversion toDatosConversion(ConvertirKmCommand comando, DestinoServidor destino) {
        Kilometros kilometros = toKilometros(comando);
        return new DatosConversion(kilometros, destino);
    }
}
