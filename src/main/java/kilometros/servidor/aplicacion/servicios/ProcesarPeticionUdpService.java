package kilometros.servidor.aplicacion.servicios;

import kilometros.servidor.aplicacion.dto.ProcesarPeticionUdpCommand;
import kilometros.servidor.aplicacion.mapper.CalculoMapper;
import kilometros.servidor.aplicacion.mapper.PeticionMapper;
import kilometros.servidor.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import kilometros.servidor.dominio.excepciones.DominioException;
import kilometros.servidor.dominio.modelos.CalculoKmAMillas;
import kilometros.servidor.dominio.modelos.EventoServidor;
import kilometros.servidor.dominio.modelos.RespuestaCliente;
import kilometros.servidor.dominio.modelos.ResultadoConversion;
import kilometros.servidor.dominio.puertos.salida.PuertoNotificacionEvento;
import kilometros.servidor.dominio.puertos.salida.PuertoSalidaRed;
import kilometros.servidor.dominio.vo.Destinatario;
import java.util.Locale;
import java.util.Objects;

public final class ProcesarPeticionUdpService implements ProcesarPeticionUdpInputPort {

    private final PuertoSalidaRed puertoSalidaRed;
    private final PuertoNotificacionEvento puertoNotificacion;
    private final PeticionMapper peticionMapper;
    private final CalculoMapper calculoMapper;

    public ProcesarPeticionUdpService(
            PuertoSalidaRed puertoSalidaRed,
            PuertoNotificacionEvento puertoNotificacion,
            PeticionMapper peticionMapper,
            CalculoMapper calculoMapper) {
        this.puertoSalidaRed = Objects.requireNonNull(puertoSalidaRed, "El puerto de salida de red es obligatorio.");
        this.puertoNotificacion = Objects.requireNonNull(puertoNotificacion, "El puerto de notificación es obligatorio.");
        this.peticionMapper = Objects.requireNonNull(peticionMapper, "El mapper de petición es obligatorio.");
        this.calculoMapper = Objects.requireNonNull(calculoMapper, "El mapper de cálculo es obligatorio.");
    }

    @Override
    public void procesar(ProcesarPeticionUdpCommand comando) {
        Objects.requireNonNull(comando, "El comando es obligatorio.");

        Destinatario destinatario = peticionMapper.toDestinatario(comando);
        String mensaje = comando.mensaje();

        if (Objects.isNull(mensaje) || mensaje.isBlank()) {
            puertoSalidaRed.enviarRespuesta(RespuestaCliente.error(destinatario, "Mensaje vacío recibido."));
            notificarEvento(destinatario.endpoint(), "datos recibidos --> [Vacío]");
            return;
        }

        String comandoTexto = mensaje.trim();

        if (comandoTexto.equalsIgnoreCase("CONECTAR")) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.conectado(destinatario, "Servidor UDP listo para recibir conversiones de Km a Millas.")
            );
            notificarEvento(destinatario.endpoint(), "conectado --> Solicitud de verificación recibida y aceptada");
            return;
        }

        if (comandoTexto.equalsIgnoreCase("DESCONECTAR")) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.desconectado(destinatario, "Sesión finalizada.")
            );
            notificarEvento(destinatario.endpoint(), "desconectado --> Cliente ha cerrado la sesión");
            return;
        }

        String comandoMayus = comandoTexto.toUpperCase(Locale.ROOT);
        if (comandoMayus.startsWith("CONVERTIR;") || comandoMayus.startsWith("CALCULAR;")) {
            procesarConversion(destinatario, comandoTexto);
            return;
        }

        // Si el cliente envía directamente un número (ej. "10" o "15.5") como en soluciones simples, también lo soportamos
        try {
            double kmDirecto = Double.parseDouble(comandoTexto.replace(',', '.'));
            procesarConversionDirecta(destinatario, kmDirecto);
            return;
        } catch (NumberFormatException ignored) {
            // No es un número directo, procesar como comando desconocido
        }

        puertoSalidaRed.enviarRespuesta(
                RespuestaCliente.error(destinatario, "Comando no reconocido por el servidor UDP. Use CONVERTIR;<kilometros>")
        );
        notificarEvento(destinatario.endpoint(), "datos recibidos --> Comando desconocido: [" + comandoTexto + "]");
    }

    private void procesarConversion(Destinatario destinatario, String comandoTexto) {
        String[] partes = comandoTexto.split(";", -1);
        if (partes.length != 2) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.error(destinatario, "Formato inválido. Se esperaba CONVERTIR;<kilometros>")
            );
            notificarEvento(destinatario.endpoint(), "datos recibidos --> Formato incorrecto: " + comandoTexto);
            return;
        }

        try {
            double valorKm = Double.parseDouble(partes[1].trim().replace(',', '.'));
            procesarConversionDirecta(destinatario, valorKm);
        } catch (NumberFormatException excepcion) {
            puertoSalidaRed.enviarRespuesta(
                    RespuestaCliente.error(destinatario, "El valor de kilómetros debe ser numérico.")
            );
            notificarEvento(destinatario.endpoint(), "datos recibidos --> Error de formato numérico: " + comandoTexto);
        }
    }

    private void procesarConversionDirecta(Destinatario destinatario, double valorKm) {
        try {
            CalculoKmAMillas calculo = calculoMapper.toDomain(valorKm);
            ResultadoConversion resultado = calculo.calcular();

            puertoSalidaRed.enviarRespuesta(RespuestaCliente.conversionExitosa(destinatario, resultado));

            String logInfo = String.format(
                    Locale.US,
                    "KILÓMETROS RECIBIDOS: %s km --> RESPUESTA ENVIADA: %s millas (%s)",
                    calculo.getKilometros().formateado(),
                    resultado.getMillasFormateadas(),
                    resultado.mensaje()
            );
            notificarEvento(destinatario.endpoint(), logInfo);

        } catch (DominioException excepcion) {
            puertoSalidaRed.enviarRespuesta(RespuestaCliente.error(destinatario, excepcion.getMessage()));
            notificarEvento(destinatario.endpoint(), "datos recibidos --> Validación fallida: " + excepcion.getMessage());
        }
    }

    private void notificarEvento(String endpoint, String descripcion) {
        puertoNotificacion.notificarEvento(new EventoServidor("EVENTO", endpoint, descripcion));
    }
}
