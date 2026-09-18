package kilometros.servidor.dominio.vo;

import kilometros.servidor.dominio.excepciones.DominioException;

public record Millas(double valor) {
    public Millas {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0.0) {
            throw new DominioException("Las millas calculadas deben ser un número válido mayor o igual a cero.");
        }
    }

    public String formateado() {
        return String.format(java.util.Locale.US, "%.4f", valor);
    }
}
