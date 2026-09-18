package kilometros.cliente.adaptadores.notificacion;

import kilometros.cliente.dominio.enums.EstadoConexion;
import kilometros.cliente.dominio.modelos.EventoCliente;
import kilometros.cliente.dominio.puertos.salida.PuertoNotificacionCliente;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AdaptadorNotificacionCliente implements PuertoNotificacionCliente {

    private final List<ObservadorCliente> observadores = new CopyOnWriteArrayList<>();

    public void registrarObservador(ObservadorCliente observador) {
        if (Objects.nonNull(observador) && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(ObservadorCliente observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarEvento(EventoCliente evento) {
        Objects.requireNonNull(evento, "El evento es obligatorio.");
        for (ObservadorCliente observador : observadores) {
            observador.onEvento(evento);
        }
    }

    @Override
    public void notificarCambioEstado(EstadoConexion nuevoEstado) {
        Objects.requireNonNull(nuevoEstado, "El estado de conexión es obligatorio.");
        for (ObservadorCliente observador : observadores) {
            observador.onCambioEstado(nuevoEstado);
        }
    }
}
