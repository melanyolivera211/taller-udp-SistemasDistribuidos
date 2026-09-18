package kilometros.cliente.adaptadores.red;

import kilometros.cliente.aplicacion.excepciones.ClienteRedException;
import kilometros.cliente.dominio.excepciones.DominioException;
import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.puertos.salida.ClienteUdpPort;
import kilometros.cliente.dominio.vo.DestinoServidor;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class AdaptadorClienteUdp implements ClienteUdpPort {

    private static final int TIMEOUT_POR_DEFECTO_MS = 3000;
    private static final int BUFFER_SIZE = 2048;

    private final CanalUdp canalUdp;
    private final ProtocoloUdpMapper protocolMapper;
    private final int timeoutMs;

    private volatile boolean conectado = false;
    private volatile DestinoServidor destinoActual = null;

    public AdaptadorClienteUdp(CanalUdp canalUdp, ProtocoloUdpMapper protocolMapper) {
        this(canalUdp, protocolMapper, TIMEOUT_POR_DEFECTO_MS);
    }

    public AdaptadorClienteUdp(CanalUdp canalUdp, ProtocoloUdpMapper protocolMapper, int timeoutMs) {
        this.canalUdp = Objects.requireNonNull(canalUdp, "El canal UDP es obligatorio.");
        this.protocolMapper = Objects.requireNonNull(protocolMapper, "El mapper de protocolo es obligatorio.");
        this.timeoutMs = timeoutMs > 0 ? timeoutMs : TIMEOUT_POR_DEFECTO_MS;
    }

    @Override
    public synchronized void conectar(DestinoServidor destino) {
        Objects.requireNonNull(destino, "El destino del servidor es obligatorio.");

        try {
            if (!canalUdp.isAbierto()) {
                canalUdp.abrir(timeoutMs);
            }

            String payload = protocolMapper.toPayloadConectar();
            canalUdp.enviar(payload, destino.ip(), destino.puerto());

            String respuestaRaw = canalUdp.recibir(BUFFER_SIZE);
            protocolMapper.parseRespuestaConectar(respuestaRaw);

            this.destinoActual = destino;
            this.conectado = true;

        } catch (SocketTimeoutException excepcion) {
            cerrarConexionSilenciosamente();
            throw new ClienteRedException("Tiempo de espera agotado (" + (timeoutMs / 1000) + "s). El servidor UDP no responde en " + destino.endpoint());
        } catch (IOException excepcion) {
            cerrarConexionSilenciosamente();
            throw new ClienteRedException("Error de comunicación de red al conectar: " + excepcion.getMessage(), excepcion);
        } catch (DominioException excepcion) {
            cerrarConexionSilenciosamente();
            throw excepcion;
        }
    }

    @Override
    public synchronized ResultadoConversion convertir(DatosConversion datos) {
        Objects.requireNonNull(datos, "Los datos de conversión son obligatorios.");

        if (!conectado || !canalUdp.isAbierto()) {
            throw new ClienteRedException("No hay una conexión activa con el servidor.");
        }

        DestinoServidor destino = datos.destino();
        try {
            String payload = protocolMapper.toPayloadConvertir(datos.kilometros());
            canalUdp.enviar(payload, destino.ip(), destino.puerto());

            String respuestaRaw = canalUdp.recibir(BUFFER_SIZE);
            return protocolMapper.parseRespuestaConversion(respuestaRaw);

        } catch (SocketTimeoutException excepcion) {
            throw new ClienteRedException("Tiempo de espera agotado (" + (timeoutMs / 1000) + "s) al esperar el cálculo del servidor.");
        } catch (IOException excepcion) {
            throw new ClienteRedException("Error de red al procesar la conversión: " + excepcion.getMessage(), excepcion);
        }
    }

    @Override
    public synchronized void desconectar(DestinoServidor destino) {
        if (!conectado && !canalUdp.isAbierto()) {
            return;
        }

        try {
            if (canalUdp.isAbierto() && Objects.nonNull(destino)) {
                String payload = protocolMapper.toPayloadDesconectar();
                canalUdp.enviar(payload, destino.ip(), destino.puerto());
                try {
                    String respuestaRaw = canalUdp.recibir(BUFFER_SIZE);
                    protocolMapper.parseRespuestaDesconectar(respuestaRaw);
                } catch (IOException ignored) {
                    // Si el servidor ya cerró o el datagrama de confirmación se perdió, cerramos localmente
                }
            }
        } catch (IOException ignored) {
            // Ignorar excepciones al enviar paquete de despedida
        } finally {
            cerrarConexionSilenciosamente();
        }
    }

    @Override
    public synchronized boolean estaConectado() {
        return conectado && canalUdp.isAbierto();
    }

    @Override
    public synchronized DestinoServidor getDestinoActual() {
        return destinoActual;
    }

    private void cerrarConexionSilenciosamente() {
        conectado = false;
        destinoActual = null;
        try {
            canalUdp.cerrar();
        } catch (Exception ignored) {
            // Cierre seguro
        }
    }
}
