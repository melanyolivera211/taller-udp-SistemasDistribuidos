package kilometros.servidor.aplicacion.puertos.entrada;

import kilometros.servidor.aplicacion.dto.ProcesarPeticionUdpCommand;

public interface ProcesarPeticionUdpInputPort {
    void procesar(ProcesarPeticionUdpCommand comando);
}
