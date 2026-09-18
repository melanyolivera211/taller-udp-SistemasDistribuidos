package kilometros.cliente.aplicacion.excepciones;

public class ClienteRedException extends RuntimeException {
    public ClienteRedException(String mensaje) {
        super(mensaje);
    }

    public ClienteRedException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
