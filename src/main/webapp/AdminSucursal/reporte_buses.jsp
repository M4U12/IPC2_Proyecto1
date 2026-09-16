<%@page import="java.util.List"%>
<%@page import="modelosReportesSucursal.ReporteBus"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Reporte de Buses</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold"><i class="bi bi-bus-front-fill text-primary"></i> Listado General de Buses</h2>
                        <p class="text-muted">Consulta el estado, kilometraje y viajes totales de tu flota de transporte.</p>
                        <span class="text-muted fs-5 fw-normal"> <%= request.getAttribute("nombreSucursal") %></span>

                        <% if (request.getAttribute("error") != null) {%>
                        <div class="alert alert-danger"><%= request.getAttribute("error")%></div>
                        <% } %>

                        <!-- Controles y Filtros -->
                        <form action="${pageContext.request.contextPath}/Reporte_Buses_Sucursal" method="POST" class="row g-3 align-items-end mt-2 border-top pt-3">

                            <div class="col-md-4">
                                <label class="form-label fw-bold small text-muted">Filtrar por Estado en el Sistema</label>
                                <% String estadoSeleccionado = (String) request.getAttribute("estadoFiltro");%>
                                <select name="estado_filtro" class="form-select">
                                    <option value="" <%= (estadoSeleccionado == null || estadoSeleccionado.isEmpty()) ? "selected" : ""%>>Todos los buses</option>
                                    <option value="true" <%= ("true".equals(estadoSeleccionado)) ? "selected" : ""%>>Solo Buses Activos</option>
                                    <option value="false" <%= ("false".equals(estadoSeleccionado)) ? "selected" : ""%>>Solo Buses de Baja</option>
                                </select>
                            </div>

                            <div class="col-md-3">
                                <button type="submit" class="btn btn-dark w-100 fw-bold"><i class="bi bi-funnel"></i> Aplicar Filtro</button>
                            </div>

                            <div class="col-md-3 ms-auto">
                                <button type="submit" name="accion" value="exportar_html" class="btn btn-outline-success w-100 fw-bold">
                                    <i class="bi bi-filetype-html"></i> Exportar a HTML
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- Tabla de Resultados -->
                    <div class="p-4 bg-white rounded-4 shadow-sm">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle border">
                                <thead class="table-light">
                                    <tr>
                                        <th>Placa</th>
                                        <th>Vehículo</th>
                                        <th>Capacidad</th>
                                        <th>Estado Operativo</th>
                                        <th>Chofer Actual</th>
                                        <th>Kilometraje</th>
                                        <th>Total Viajes</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<ReporteBus> reporte = (List<ReporteBus>) request.getAttribute("reporte");
                                        if (reporte != null && !reporte.isEmpty()) {
                                            for (ReporteBus r : reporte) {
                                    %>
                                    <tr>
                                        <td class="fw-bold"><span class="badge bg-secondary"><%= r.getPlaca()%></span></td>
                                        <td><%= r.getMarca()%> <%= r.getModelo()%></td>
                                        <td><%= r.getCapacidad()%> pasajeros</td>
                                        <td><span class="text-primary fw-bold"><%= r.getEstadoOperativo()%></span></td>
                                        <td><%= r.getNombreChofer()%></td>
                                        <td><%= r.getKilometrajeActual()%> km</td>
                                        <td class="text-center fw-bold fs-5 text-success"><%= r.getTotalViajes()%></td>
                                    </tr>
                                    <%      }
                                    } else {
                                    %>
                                    <tr>
                                        <td colspan="7" class="text-center py-4 text-muted">No se encontraron buses con los filtros seleccionados.</td>
                                    </tr>
                                    <% }%>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </body>
</html>