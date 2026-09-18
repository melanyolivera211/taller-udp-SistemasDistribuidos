package kilometros.dominio.puertos.salida;

import kilometros.aplicacion.excepciones.ServidorRedException;
import kilometros.dominio.vo.PuertoRed;

/**
 * Puerto de salida para controlar el ciclo de vida de la infraestructura de red del servidor.
 */
public interface ControladorServidorRedPort {
    void iniciar(PuertoRed puerto) throws ServidorRedException;

    void detener();

    boolean isActivo();

    PuertoRed getPuertoActual();
}
