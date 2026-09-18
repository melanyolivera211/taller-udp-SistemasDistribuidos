package kilometros.aplicacion.mapper;

import kilometros.aplicacion.dto.ResultadoConversionDto;
import kilometros.dominio.modelos.CalculoKmAMillas;
import kilometros.dominio.modelos.ResultadoConversion;
import kilometros.dominio.vo.Kilometros;

public final class CalculoMapper {

    public CalculoKmAMillas toDomain(double valorKilometros) {
        return new CalculoKmAMillas(new Kilometros(valorKilometros));
    }

    public ResultadoConversionDto toDto(ResultadoConversion resultado) {
        return new ResultadoConversionDto(
                resultado.millas().valor(),
                resultado.getMillasFormateadas(),
                resultado.mensaje()
        );
    }
}
