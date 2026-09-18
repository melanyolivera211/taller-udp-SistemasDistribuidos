package kilometros.adaptadores.red;

import kilometros.adaptadores.red.mapper.UdpNetworkMapper;
import kilometros.adaptadores.red.response.UdpResponse;
import kilometros.dominio.modelos.EventoServidor;
import kilometros.dominio.modelos.RespuestaCliente;
import kilometros.dominio.puertos.salida.PuertoNotificacionEvento;
import kilometros.dominio.puertos.salida.PuertoSalidaRed;
import java.io.IOException;
import java.util.Objects;

public final class AdaptadorSalidaUdp implements PuertoSalidaRed {

    private final CanalUdp canalUdp;
    private final UdpNetworkMapper mapper;
    private final PuertoNotificacionEvento notificador;

    public AdaptadorSalidaUdp(CanalUdp canalUdp, UdpNetworkMapper mapper, PuertoNotificacionEvento notificador) {
        this.canalUdp = Objects.requireNonNull(canalUdp, "El canal UDP es obligatorio.");
        this.mapper = Objects.requireNonNull(mapper, "El mapper UDP es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
    }

    @Override
    public void enviarRespuesta(RespuestaCliente respuesta) {
        Objects.requireNonNull(respuesta, "La respuesta es obligatoria.");
        try {
            UdpResponse udpResponse = mapper.toNetworkResponse(respuesta);
            canalUdp.enviar(udpResponse);
        } catch (IOException excepcion) {
            notificador.notificarEvento(
                    new EventoServidor(
                            "ERROR",
                            respuesta.getDestinatario().endpoint(),
                            "Fallo al enviar datagrama UDP: " + excepcion.getMessage()
                    )
            );
        }
    }
}
