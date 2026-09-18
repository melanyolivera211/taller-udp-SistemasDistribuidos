package kilometros.servidor.dominio.modelos;

import java.time.LocalDateTime;

public final class EventoServidor {
    private final LocalDateTime fechaHora;
    private final String categoria;
    private final String endpoint;
    private final String descripcion;

    public EventoServidor(String categoria, String endpoint, String descripcion) {
        this.fechaHora = LocalDateTime.now();
        this.categoria = categoria;
        this.endpoint = endpoint;
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
