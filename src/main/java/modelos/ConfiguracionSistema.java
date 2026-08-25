package modelos;

public class ConfiguracionSistema {
    private int idConfiguracion;
    private double depreciacionPorKm;

    public ConfiguracionSistema() {
    }

    public ConfiguracionSistema(int idConfiguracion, double depreciacionPorKm) {
        this.idConfiguracion = idConfiguracion;
        this.depreciacionPorKm = depreciacionPorKm;
    }

    public int getIdConfiguracion() {
        return idConfiguracion;
    }

    public double getDepreciacionPorKm() {
        return depreciacionPorKm;
    }

    public void setIdConfiguracion(int idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public void setDepreciacionPorKm(double depreciacionPorKm) {
        this.depreciacionPorKm = depreciacionPorKm;
    }
}
