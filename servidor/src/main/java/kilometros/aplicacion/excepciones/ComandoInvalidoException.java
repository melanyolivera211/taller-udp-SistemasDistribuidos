package kilometros.aplicacion.excepciones;

public class ComandoInvalidoException extends RuntimeException {
    public ComandoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
