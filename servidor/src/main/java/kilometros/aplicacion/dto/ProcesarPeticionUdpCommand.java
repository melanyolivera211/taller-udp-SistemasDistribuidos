package kilometros.aplicacion.dto;

import java.util.Objects;

public record ProcesarPeticionUdpCommand(String hostRemoto, int puertoRemoto, String mensaje) {
    public ProcesarPeticionUdpCommand {
        Objects.requireNonNull(hostRemoto, "El host remoto es obligatorio.");
        Objects.requireNonNull(mensaje, "El mensaje recibido es obligatorio.");
    }
}
