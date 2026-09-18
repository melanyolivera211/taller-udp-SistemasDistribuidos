package kilometros.adaptadores.red.response;

import java.nio.charset.StandardCharsets;

public final class UdpResponse {

    private final String payload;
    private final String ipDestino;
    private final int puertoDestino;

    public UdpResponse(String payload, String ipDestino, int puertoDestino) {
        this.payload = payload;
        this.ipDestino = ipDestino;
        this.puertoDestino = puertoDestino;
    }

    public String getPayload() {
        return payload;
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public byte[] getBytes() {
        return payload.getBytes(StandardCharsets.UTF_8);
    }
}
