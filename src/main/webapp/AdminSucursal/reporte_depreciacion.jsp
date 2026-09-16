<%@page import="java.util.List"%>
<%@page import="modelosReportesSucursal.ReporteDepreciacion"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Reporte de Depreciación</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />
                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4 d-flex justify-content-between align-items-center">
                        <div>
                            <h2 class="fw-bold"><i class="bi bi-graph-down-arrow text-danger"></i> Depreciación por Bus</h2>
                            <p class="text-muted mb-0">Cálculo del desgaste financiero acumulado por kilómetro recorrido.</p>
                            <span class="text-muted fs-5 fw-normal"> <%= request.getAttribute("nombreSucursal") %></span>
                        </div>
                        <form action="${pageContext.request.contextPath}/Reporte_Depreciacion_Sucursal" method="POST">
                            <button type="submit" name="accion" value="exportar_html" class="btn btn-outline-success fw-bold">
                                <i class="bi bi-filetype-html"></i> Exportar a HTML
                            </button>
                        </form>
                    </div>

                    <div class="p-4 bg-white rounded-4 shadow-sm">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle border">
                                <thead class="table-light">
                                    <tr>
                                        <th>No. de Placa</th>
                                        <th>Kilómetros Recorridos</th>
                                        <th>Tarifa por Km</th>
                                        <th>Depreciación Acumulada</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<ReporteDepreciacion> reporte = (List<ReporteDepreciacion>) request.getAttribute("reporte");
                                        if (reporte != null && !reporte.isEmpty()) {
                                            for (ReporteDepreciacion r : reporte) {
                                    %>
                                    <tr>
                                        <td class="fw-bold"><span class="badge bg-secondary"><%= r.getPlaca()%></span></td>
                                        <td><%= String.format("%.2f", r.getKilometrosRecorridos())%> km</td>
                                        <td>Q. <%= String.format("%.2f", r.getDepreciacionPorKm())%></td>
                                        <td class="fw-bold text-danger">Q. <%= String.format("%.2f", r.getDepreciacionTotal())%></td>
                                    </tr>
                                    <% }
                                } else { %>
                                    <tr><td colspan="4" class="text-center py-4 text-muted">No se encontraron datos de desgaste.</td></tr>
                                    <% } %>
                                </tbody>
                                <% Double granTotal = (Double) request.getAttribute("granTotalDepreciacion");
                                if (granTotal != null && granTotal > 0) {%>
                                <tfoot>
                                    <tr class="table-secondary fw-bold fs-5">
                                        <td colspan="3" class="text-end">TOTAL DE DEPRECIACIÓN:</td>
                                        <td class="text-danger">Q. <%= String.format("%.2f", granTotal)%></td>
                                    </tr>
                                </tfoot>
                                <% }%>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>