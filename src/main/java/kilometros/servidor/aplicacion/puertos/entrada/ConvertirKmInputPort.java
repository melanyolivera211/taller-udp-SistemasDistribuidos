package kilometros.servidor.aplicacion.puertos.entrada;

import kilometros.servidor.aplicacion.dto.ConvertirKmCommand;
import kilometros.servidor.aplicacion.dto.ResultadoConversionDto;

public interface ConvertirKmInputPort {
    ResultadoConversionDto convertir(ConvertirKmCommand comando);
}
