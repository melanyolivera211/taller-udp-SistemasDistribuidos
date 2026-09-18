package kilometros.servidor.adaptadores.red;

import kilometros.servidor.dominio.enums.EstadoServidor;
import kilometros.servidor.dominio.modelos.EventoServidor;
import kilometros.servidor.dominio.puertos.salida.PuertoNotificacionEvento;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AdaptadorNotificacionEvento implements PuertoNotificacionEvento {

    private final List<ObservadorServidor> observadores = new CopyOnWriteArrayList<>();

    public void registrarObservador(ObservadorServidor observador) {
        if (Objects.nonNull(observador) && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(ObservadorServidor observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarEvento(EventoServidor evento) {
        Objects.requireNonNull(evento, "El evento es obligatorio.");
        for (ObservadorServidor observador : observadores) {
            observador.onEvento(evento);
        }
    }

    @Override
    public void notificarCambioEstado(EstadoServidor nuevoEstado, int puerto) {
        Objects.requireNonNull(nuevoEstado, "El estado es obligatorio.");
        for (ObservadorServidor observador : observadores) {
            observador.onCambioEstado(nuevoEstado, puerto);
        }
    }
}
