package kilometros.servidor.dominio.puertos.salida;

import kilometros.servidor.aplicacion.excepciones.ServidorRedException;
import kilometros.servidor.dominio.vo.PuertoRed;

/**
 * Puerto de salida para controlar el ciclo de vida de la infraestructura de red del servidor.
 */
public interface ControladorServidorRedPort {
    void iniciar(PuertoRed puerto) throws ServidorRedException;

    void detener();

    boolean isActivo();

    PuertoRed getPuertoActual();
}
