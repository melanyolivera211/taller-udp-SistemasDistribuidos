package kilometros.servidor.aplicacion.excepciones;

public class ServidorRedException extends RuntimeException {
    public ServidorRedException(String mensaje) {
        super(mensaje);
    }

    public ServidorRedException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
