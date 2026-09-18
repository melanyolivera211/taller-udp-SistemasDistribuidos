package kilometros.cliente.dominio.vo;

import kilometros.cliente.dominio.excepciones.KilometrosInvalidosException;
import java.util.Locale;

public record Kilometros(double valor) {
    public Kilometros {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0.0) {
            throw new KilometrosInvalidosException("Los kilómetros deben ser un número válido mayor o igual a cero.");
        }
    }

    public String formateado() {
        return String.format(Locale.US, "%.2f", valor);
    }
}
