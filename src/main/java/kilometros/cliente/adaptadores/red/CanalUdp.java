package kilometros.cliente.adaptadores.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CanalUdp {

    private DatagramSocket socket;
    private volatile boolean abierto = false;

    public synchronized void abrir(int timeoutMs) throws SocketException {
        if (abierto && Objects.nonNull(socket) && !socket.isClosed()) {
            return;
        }
        socket = new DatagramSocket();
        socket.setSoTimeout(timeoutMs);
        abierto = true;
    }

    public synchronized void cerrar() {
        abierto = false;
        if (Objects.nonNull(socket) && !socket.isClosed()) {
            socket.close();
        }
        socket = null;
    }

    public synchronized boolean isAbierto() {
        return abierto && Objects.nonNull(socket) && !socket.isClosed();
    }

    public synchronized int getPuertoLocal() {
        if (Objects.nonNull(socket) && !socket.isClosed()) {
            return socket.getLocalPort();
        }
        return 0;
    }

    public void enviar(String mensaje, String hostDestino, int puertoDestino) throws IOException {
        DatagramSocket socketActivo = obtenerSocketActivo();
        byte[] bytes = mensaje.getBytes(StandardCharsets.UTF_8);
        InetAddress direccion = InetAddress.getByName(hostDestino);
        DatagramPacket paquete = new DatagramPacket(bytes, bytes.length, direccion, puertoDestino);
        socketActivo.send(paquete);
    }

    public String recibir(int bufferSize) throws IOException {
        DatagramSocket socketActivo = obtenerSocketActivo();
        byte[] buffer = new byte[bufferSize];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socketActivo.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8);
    }

    private synchronized DatagramSocket obtenerSocketActivo() throws SocketException {
        if (Objects.isNull(socket) || socket.isClosed()) {
            throw new SocketException("El socket UDP del cliente no está abierto.");
        }
        return socket;
    }
}
