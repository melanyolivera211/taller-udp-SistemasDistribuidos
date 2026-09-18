package kilometros.aplicacion.puertos.entrada;

import kilometros.aplicacion.dto.ConvertirKmCommand;
import kilometros.aplicacion.dto.ResultadoConversionDto;

public interface ConvertirKmInputPort {
    ResultadoConversionDto convertir(ConvertirKmCommand comando);
}
