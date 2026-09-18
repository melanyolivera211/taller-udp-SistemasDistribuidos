package kilometros.cliente.aplicacion.puertos.entrada;

import kilometros.cliente.aplicacion.dto.ConectarCommand;
import kilometros.cliente.dominio.vo.DestinoServidor;

public interface GestionarConexionInputPort {

    void conectar(ConectarCommand comando);

    void desconectar();

    boolean estaConectado();

    DestinoServidor getDestinoActual();
}
