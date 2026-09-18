package kilometros.cliente.dominio.modelos;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventoCliente {

    private final LocalDateTime fechaHora;
    private final String tipo;
    private final String descripcion;

    public EventoCliente(String tipo, String descripcion) {
        this(LocalDateTime.now(), tipo, descripcion);
    }

    public EventoCliente(LocalDateTime fechaHora, String tipo, String descripcion) {
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora no pueden ser nulas.");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de evento no puede ser nulo.");
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula.");
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
