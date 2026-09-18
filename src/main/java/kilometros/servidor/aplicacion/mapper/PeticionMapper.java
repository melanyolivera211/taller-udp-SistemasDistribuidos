package kilometros.servidor.aplicacion.mapper;

import kilometros.servidor.aplicacion.dto.ProcesarPeticionUdpCommand;
import kilometros.servidor.dominio.vo.Destinatario;
import java.util.Objects;

public final class PeticionMapper {

    public Destinatario toDestinatario(ProcesarPeticionUdpCommand comando) {
        Objects.requireNonNull(comando, "El comando no puede ser nulo.");
        return new Destinatario(comando.hostRemoto(), comando.puertoRemoto());
    }
}
