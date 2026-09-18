package kilometros.aplicacion.puertos.entrada;

import kilometros.aplicacion.dto.ProcesarPeticionUdpCommand;

public interface ProcesarPeticionUdpInputPort {
    void procesar(ProcesarPeticionUdpCommand comando);
}
