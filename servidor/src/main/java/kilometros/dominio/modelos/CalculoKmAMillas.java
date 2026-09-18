package kilometros.dominio.modelos;

import kilometros.dominio.vo.Kilometros;
import kilometros.dominio.vo.Millas;
import java.util.Locale;
import java.util.Objects;

public class CalculoKmAMillas {
    public static final double FACTOR_KM_A_MILLAS = 0.621371;

    private final Kilometros kilometros;

    public CalculoKmAMillas(Kilometros kilometros) {
        this.kilometros = Objects.requireNonNull(kilometros, "Los kilómetros son obligatorios para el cálculo.");
    }

    public ResultadoConversion calcular() {
        double valorMillas = kilometros.valor() * FACTOR_KM_A_MILLAS;
        Millas millas = new Millas(valorMillas);
        String mensaje = String.format(Locale.US, "%s km equivalen a %s millas", kilometros.formateado(), millas.formateado());
        return new ResultadoConversion(millas, mensaje);
    }

    public Kilometros getKilometros() {
        return kilometros;
    }
}
