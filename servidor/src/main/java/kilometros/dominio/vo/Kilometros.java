package kilometros.dominio.vo;

import kilometros.dominio.excepciones.KilometrosInvalidosException;

public record Kilometros(double valor) {
    public Kilometros {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0.0) {
            throw new KilometrosInvalidosException("Los kilómetros deben ser un número válido mayor o igual a cero.");
        }
    }

    public String formateado() {
        return String.format(java.util.Locale.US, "%.2f", valor);
    }
}
