<%@page import="java.util.List"%>
<%@page import="modelosReportesSucursal.ReporteIngresoAlquiler"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Ingresos por Alquileres</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />
                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold"><i class="bi bi-star-fill text-warning"></i> Ingresos por Alquileres Privados</h2>
                        <p class="text-muted">Desglose de contratos y cobros por viajes chárter.</p>
                        <span class="text-muted fs-5 fw-normal"> <%= request.getAttribute("nombreSucursal") %></span>

                        <form action="${pageContext.request.contextPath}/Reporte_Ingreso_Alquiler_Sucursal" method="POST" class="row g-3 align-items-end mt-2 border-top pt-3">
                            <div class="col-md-3">
                                <label class="form-label fw-bold small">Fecha Inicio</label>
                                <input type="date" name="fecha_inicio" class="form-control" value="<%= request.getAttribute("fechaInicio")%>">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-bold small">Fecha Fin</label>
                                <input type="date" name="fecha_fin" class="form-control" value="<%= request.getAttribute("fechaFin")%>">
                            </div>
                            <div class="col-md-5 ms-auto d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/Reporte_Ingreso_Alquiler_Sucursal" class="btn btn-secondary fw-bold w-100"><i class="bi bi-eraser"></i> Limpiar</a>
                            <button type="submit" class="btn btn-dark fw-bold w-100"><i class="bi bi-funnel"></i> Filtrar</button>
                            <button type="submit" name="accion" value="exportar_html" class="btn btn-outline-success fw-bold w-100"><i class="bi bi-file-earmark-arrow-down"></i> HTML</button>
                        </div>
                        </form>
                    </div>

                    <div class="p-4 bg-white rounded-4 shadow-sm">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle border">
                                <thead class="table-light">
                                    <tr>
                                        <th>Cliente</th>
                                        <th>Ruta Contratada</th>
                                        <th>Fecha de Salida</th>
                                        <th>Placa Asignada</th>
                                        <th>Precio</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<ReporteIngresoAlquiler> reporte = (List<ReporteIngresoAlquiler>) request.getAttribute("reporte");
                                        if (reporte != null && !reporte.isEmpty()) {
                                            for (ReporteIngresoAlquiler r : reporte) {
                                    %>
                                    <tr>
                                        <td class="fw-bold"><%= r.getCliente()%></td>
                                        <td><%= r.getOrigen()%> <i class="bi bi-arrow-right text-muted mx-1"></i> <%= r.getDestino()%></td>
                                        <td><%= r.getFechaSalida().toLocalDate()%> <small class="text-muted"><%= r.getFechaSalida().toLocalTime()%></small></td>
                                        <td><span class="badge bg-secondary"><%= r.getPlacaBus()%></span></td>
                                        <td class="fw-bold text-warning-emphasis">Q. <%= String.format("%.2f", r.getPrecioTotal())%></td>
                                    </tr>
                                    <% }
                                } else { %>
                                    <tr><td colspan="5" class="text-center py-4 text-muted">No se encontraron alquileres en las fechas seleccionadas.</td></tr>
                                    <% } %>
                                </tbody>
                                <% Double granAlquiler = (Double) request.getAttribute("granTotalAlquileres");
                                if (granAlquiler != null && granAlquiler > 0) {%>
                                <tfoot>
                                    <tr class="table-warning fw-bold fs-5">
                                        <td colspan="4" class="text-end">GRAN TOTAL DE INGRESOS:</td>
                                        <td class="text-warning-emphasis">Q. <%= String.format("%.2f", granAlquiler)%></td>
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