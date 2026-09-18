package kilometros.cliente.aplicacion.puertos.entrada;

import kilometros.cliente.aplicacion.dto.ConvertirKmCommand;
import kilometros.cliente.dominio.modelos.ResultadoConversion;

public interface ConvertirKmInputPort {

    ResultadoConversion convertir(ConvertirKmCommand comando);
}
