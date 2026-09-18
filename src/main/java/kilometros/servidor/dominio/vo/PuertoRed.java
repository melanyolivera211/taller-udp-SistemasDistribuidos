package kilometros.servidor.dominio.vo;

import kilometros.servidor.dominio.excepciones.PuertoInvalidoException;

public record PuertoRed(int valor) {
    public static final int PUERTO_POR_DEFECTO = 9007;

    public PuertoRed {
        if (valor < 1024 || valor > 65535) {
            throw new PuertoInvalidoException("El puerto de red debe estar en el rango de puertos disponibles (1024 - 65535).");
        }
    }

    public static PuertoRed porDefecto() {
        return new PuertoRed(PUERTO_POR_DEFECTO);
    }
}
