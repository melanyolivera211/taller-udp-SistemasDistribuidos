package kilometros.cliente.dominio.excepciones;

public class KilometrosInvalidosException extends DominioException {
    public KilometrosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
