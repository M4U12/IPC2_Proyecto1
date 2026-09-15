package modelos;

public class ReporteGanancias {

    private String nombreSucursal;

    private double ingresosBoletos;
    private double ingresosPrivados;

    private double costoCombustible;
    private double costoManoObraTaller;
    private double costoRepuestosTaller;
    private double costoDepreciacion;

    public ReporteGanancias(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public double getTotalIngresos() {
        return ingresosBoletos + ingresosPrivados;
    }

    public double getTotalCostos() {
        return costoCombustible + costoManoObraTaller + costoRepuestosTaller + costoDepreciacion;
    }

    public double getGananciaNeta() {
        return getTotalIngresos() - getTotalCostos();
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public double getIngresosBoletos() {
        return ingresosBoletos;
    }

    public void setIngresosBoletos(double ingresosBoletos) {
        this.ingresosBoletos = ingresosBoletos;
    }

    public double getIngresosPrivados() {
        return ingresosPrivados;
    }

    public void setIngresosPrivados(double ingresosPrivados) {
        this.ingresosPrivados = ingresosPrivados;
    }

    public double getCostoCombustible() {
        return costoCombustible;
    }

    public void setCostoCombustible(double costoCombustible) {
        this.costoCombustible = costoCombustible;
    }

    public double getCostoManoObraTaller() {
        return costoManoObraTaller;
    }

    public void setCostoManoObraTaller(double costoManoObraTaller) {
        this.costoManoObraTaller = costoManoObraTaller;
    }

    public double getCostoRepuestosTaller() {
        return costoRepuestosTaller;
    }

    public void setCostoRepuestosTaller(double costoRepuestosTaller) {
        this.costoRepuestosTaller = costoRepuestosTaller;
    }

    public double getCostoDepreciacion() {
        return costoDepreciacion;
    }

    public void setCostoDepreciacion(double costoDepreciacion) {
        this.costoDepreciacion = costoDepreciacion;
    }
}
