package kilometros.servidor.dominio.enums;

public enum EstadoServidor {
    ACTIVO("ONLINE"),
    INACTIVO("OFF LINE");

    private final String descripcion;

    EstadoServidor(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
