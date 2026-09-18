package kilometros.cliente.aplicacion.servicios;

import kilometros.cliente.aplicacion.dto.ConectarCommand;
import kilometros.cliente.aplicacion.dto.ConvertirKmCommand;
import kilometros.cliente.aplicacion.excepciones.ClienteRedException;
import kilometros.cliente.aplicacion.mapper.ClienteMapper;
import kilometros.cliente.aplicacion.puertos.entrada.ConvertirKmInputPort;
import kilometros.cliente.aplicacion.puertos.entrada.GestionarConexionInputPort;
import kilometros.cliente.dominio.enums.EstadoConexion;
import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.modelos.EventoCliente;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.puertos.salida.ClienteUdpPort;
import kilometros.cliente.dominio.puertos.salida.PuertoNotificacionCliente;
import kilometros.cliente.dominio.vo.DestinoServidor;
import java.util.Objects;

public final class ClienteKmService implements ConvertirKmInputPort, GestionarConexionInputPort {

    private final ClienteUdpPort clienteUdpPort;
    private final PuertoNotificacionCliente notificador;
    private final ClienteMapper mapper;

    public ClienteKmService(
            ClienteUdpPort clienteUdpPort,
            PuertoNotificacionCliente notificador,
            ClienteMapper mapper) {
        this.clienteUdpPort = Objects.requireNonNull(clienteUdpPort, "El puerto de salida UDP es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
        this.mapper = Objects.requireNonNull(mapper, "El mapper es obligatorio.");
    }

    @Override
    public void conectar(ConectarCommand comando) {
        Objects.requireNonNull(comando, "El comando de conexión no puede ser nulo.");
        DestinoServidor destino = mapper.toDestinoServidor(comando);

        notificador.notificarEvento(new EventoCliente("CONEXIÓN", "Intentando verificar servidor en " + destino.endpoint() + "..."));

        try {
            clienteUdpPort.conectar(destino);
            notificador.notificarCambioEstado(EstadoConexion.CONECTADO);
            notificador.notificarEvento(new EventoCliente("CONECTADO", "Servidor UDP verificado en " + destino.endpoint()));
        } catch (RuntimeException excepcion) {
            notificador.notificarCambioEstado(EstadoConexion.DESCONECTADO);
            notificador.notificarEvento(new EventoCliente("ERROR", "No se pudo conectar con el servidor: " + excepcion.getMessage()));
            throw excepcion;
        }
    }

    @Override
    public void desconectar() {
        if (!clienteUdpPort.estaConectado()) {
            return;
        }

        DestinoServidor destino = clienteUdpPort.getDestinoActual();
        try {
            if (Objects.nonNull(destino)) {
                clienteUdpPort.desconectar(destino);
            }
        } catch (RuntimeException excepcion) {
            notificador.notificarEvento(new EventoCliente("AVISO", "Aviso durante desconexión: " + excepcion.getMessage()));
        } finally {
            notificador.notificarCambioEstado(EstadoConexion.DESCONECTADO);
            notificador.notificarEvento(new EventoCliente("DESCONECTADO", "Sesión UDP finalizada."));
        }
    }

    @Override
    public boolean estaConectado() {
        return clienteUdpPort.estaConectado();
    }

    @Override
    public DestinoServidor getDestinoActual() {
        return clienteUdpPort.getDestinoActual();
    }

    @Override
    public ResultadoConversion convertir(ConvertirKmCommand comando) {
        Objects.requireNonNull(comando, "El comando de conversión no puede ser nulo.");

        if (!clienteUdpPort.estaConectado()) {
            throw new ClienteRedException("Debe verificar la conexión con el servidor UDP antes de convertir.");
        }

        DestinoServidor destino = clienteUdpPort.getDestinoActual();
        DatosConversion datos = mapper.toDatosConversion(comando, destino);

        notificador.notificarEvento(new EventoCliente("PETICIÓN", "Enviando a convertir: " + datos.kilometros().formateado() + " km hacia " + destino.endpoint()));

        try {
            ResultadoConversion resultado = clienteUdpPort.convertir(datos);
            notificador.notificarEvento(new EventoCliente("RESPUESTA", "Recibido del servidor: " + resultado.getMillasFormateadas() + " millas"));
            return resultado;
        } catch (RuntimeException excepcion) {
            notificador.notificarEvento(new EventoCliente("ERROR", "Fallo al convertir: " + excepcion.getMessage()));
            throw excepcion;
        }
    }
}
