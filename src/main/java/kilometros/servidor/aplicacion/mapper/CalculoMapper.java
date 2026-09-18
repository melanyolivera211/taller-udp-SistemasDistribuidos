package kilometros.servidor.aplicacion.mapper;

import kilometros.servidor.aplicacion.dto.ResultadoConversionDto;
import kilometros.servidor.dominio.modelos.CalculoKmAMillas;
import kilometros.servidor.dominio.modelos.ResultadoConversion;
import kilometros.servidor.dominio.vo.Kilometros;

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
