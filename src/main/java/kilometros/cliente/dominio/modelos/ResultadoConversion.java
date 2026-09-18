package kilometros.cliente.dominio.modelos;

import kilometros.cliente.dominio.excepciones.DominioException;
import java.util.Locale;
import java.util.Objects;

public record ResultadoConversion(double millas, String mensaje) {
    public ResultadoConversion {
        if (Double.isNaN(millas) || Double.isInfinite(millas) || millas < 0.0) {
            throw new DominioException("Las millas del resultado deben ser un número válido mayor o igual a cero.");
        }
        Objects.requireNonNull(mensaje, "El mensaje explicativo es obligatorio en el resultado.");
    }

    public String getMillasFormateadas() {
        return String.format(Locale.US, "%.4f", millas);
    }
}
