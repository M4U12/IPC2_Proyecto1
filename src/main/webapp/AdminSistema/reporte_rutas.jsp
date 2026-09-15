<%@page import="java.util.List"%>
<%@page import="modelos.ReporteRutasDemanda"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Rutas Demandadas</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="fw-bold text-primary mb-1"><i class="bi bi-geo-alt me-2"></i> Rutas Más Demandadas</h2>
                            <p class="text-muted mb-0">Ranking de destinos basado en la cantidad de boletos vendidos.</p>
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-secondary fw-bold" onclick="window.print()"><i class="bi bi-printer"></i> Imprimir</button>
                            <form action="${pageContext.request.contextPath}/Reporte_Rutas" method="POST" class="m-0">
                                <input type="hidden" name="fecha_inicio" value="<%= request.getAttribute("fechaInicio")%>">
                                <input type="hidden" name="fecha_fin" value="<%= request.getAttribute("fechaFin")%>">
                                <input type="hidden" name="accion" value="exportar_html">
                                <button type="submit" class="btn btn-outline-success fw-bold"><i class="bi bi-filetype-html"></i> Exportar a HTML</button>
                            </form>
                        </div>
                    </div>

                    <% if (request.getAttribute("error") != null) {%>
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> <%= request.getAttribute("error")%></div>
                    <% }%>

                    <!-- Panel de Filtros -->
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/Reporte_Rutas" method="POST" class="row g-3 align-items-end justify-content-center">

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted mb-1">Fecha de Inicio</label>
                                    <input type="date" class="form-control" name="fecha_inicio" value="<%= request.getAttribute("fechaInicio")%>">
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted mb-1">Fecha de Fin</label>
                                    <input type="date" class="form-control" name="fecha_fin" value="<%= request.getAttribute("fechaFin")%>">
                                </div>

                                <div class="col-md-2 d-flex gap-2">
                                    <button type="submit" class="btn btn-primary fw-bold w-50" title="Aplicar Filtros"><i class="bi bi-search"></i></button>
                                    <a href="${pageContext.request.contextPath}/Reporte_Rutas" class="btn btn-outline-secondary fw-bold w-50" title="Limpiar Filtros"><i class="bi bi-eraser"></i></a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <%
                        List<ReporteRutasDemanda> reporte = (List<ReporteRutasDemanda>) request.getAttribute("reporte");
                        if (reporte != null && !reporte.isEmpty()) {
                    %>

                    <div class="card border-0 shadow-sm rounded-4 p-4">
                        <div class="table-responsive">
                            <table class="table table-hover table-bordered align-middle text-center mb-0">
                                <thead class="table-dark">
                                    <tr>
                                        <th class="bg-primary text-white">Puesto</th>
                                        <th>Sucursal Origen</th>
                                        <th>Sucursal Destino</th>
                                        <th>Tarifa (Q)</th>
                                        <th class="bg-success text-white">Boletos Vendidos</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        int posicion = 1;
                                        for (ReporteRutasDemanda r : reporte) {
                                    %>
                                    <tr>
                                        <td class="fw-bold fs-5">#<%= posicion%></td>
                                        <td class="fw-bold"><%= r.getOrigen()%></td>
                                        <td class="fw-bold"><%= r.getDestino()%></td>
                                        <td class="text-muted">Q.<%= String.format("%.2f", r.getPrecio())%></td>
                                        <td class="text-success fw-bold fs-4 bg-success bg-opacity-10"><%= r.getTotalBoletosVendidos()%></td>
                                    </tr>
                                    <%
                                            posicion++;
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% } else { %>
                    <div class="alert alert-light text-center py-5 shadow-sm rounded-4 border-0 mt-4">
                        <i class="bi bi-map display-4 text-muted d-block mb-3"></i>
                        <h5 class="text-muted fw-bold">No hay boletos vendidos</h5>
                        <p class="text-muted mb-0">Aún no existen registros de viajes para el período seleccionado.</p>
                    </div>
                    <% }%>

                </div>
            </div>
        </div>
    </body>
</html>