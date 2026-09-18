package kilometros.aplicacion.servicios;

import kilometros.aplicacion.dto.ConvertirKmCommand;
import kilometros.aplicacion.dto.ResultadoConversionDto;
import kilometros.aplicacion.excepciones.ComandoInvalidoException;
import kilometros.aplicacion.mapper.CalculoMapper;
import kilometros.aplicacion.puertos.entrada.ConvertirKmInputPort;
import kilometros.dominio.modelos.CalculoKmAMillas;
import kilometros.dominio.modelos.ResultadoConversion;
import java.util.Objects;

public final class ConvertirKmService implements ConvertirKmInputPort {

    private final CalculoMapper mapper;

    public ConvertirKmService(CalculoMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "El mapper es obligatorio.");
    }

    @Override
    public ResultadoConversionDto convertir(ConvertirKmCommand comando) {
        if (Objects.isNull(comando)) {
            throw new ComandoInvalidoException("El comando de conversión no puede ser nulo.");
        }
        CalculoKmAMillas calculo = mapper.toDomain(comando.kilometros());
        ResultadoConversion resultado = calculo.calcular();
        return mapper.toDto(resultado);
    }
}
