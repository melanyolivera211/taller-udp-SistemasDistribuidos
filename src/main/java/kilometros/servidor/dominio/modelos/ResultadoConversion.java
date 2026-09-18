package kilometros.servidor.dominio.modelos;

import kilometros.servidor.dominio.vo.Millas;
import java.util.Objects;

public record ResultadoConversion(Millas millas, String mensaje) {
    public ResultadoConversion {
        Objects.requireNonNull(millas, "Las millas son obligatorias en el resultado.");
        Objects.requireNonNull(mensaje, "El mensaje explicativo es obligatorio en el resultado.");
    }

    public String getMillasFormateadas() {
        return millas.formateado();
    }
}
