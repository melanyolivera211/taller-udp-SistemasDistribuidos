package kilometros.cliente.dominio.puertos.salida;

import kilometros.cliente.dominio.modelos.DatosConversion;
import kilometros.cliente.dominio.modelos.ResultadoConversion;
import kilometros.cliente.dominio.vo.DestinoServidor;

public interface ClienteUdpPort {

    void conectar(DestinoServidor destino);

    ResultadoConversion convertir(DatosConversion datos);

    void desconectar(DestinoServidor destino);

    boolean estaConectado();

    DestinoServidor getDestinoActual();
}
