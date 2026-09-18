package kilometros.cliente.dominio.excepciones;

public class DominioException extends RuntimeException {
    public DominioException(String mensaje) {
        super(mensaje);
    }

    public DominioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
