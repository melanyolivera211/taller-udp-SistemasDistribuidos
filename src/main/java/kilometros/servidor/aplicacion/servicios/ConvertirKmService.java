package kilometros.servidor.aplicacion.servicios;

import kilometros.servidor.aplicacion.dto.ConvertirKmCommand;
import kilometros.servidor.aplicacion.dto.ResultadoConversionDto;
import kilometros.servidor.aplicacion.excepciones.ComandoInvalidoException;
import kilometros.servidor.aplicacion.mapper.CalculoMapper;
import kilometros.servidor.aplicacion.puertos.entrada.ConvertirKmInputPort;
import kilometros.servidor.dominio.modelos.CalculoKmAMillas;
import kilometros.servidor.dominio.modelos.ResultadoConversion;
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
