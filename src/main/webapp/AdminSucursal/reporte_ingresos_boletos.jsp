<%@page import="java.util.List"%>
<%@page import="modelos.Bus"%>
<%@page import="modelos.Ruta"%>
<%@page import="modelosReportesSucursal.ReporteIngresoBoleto"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Ingresos por Boletos</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />
                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold"><i class="bi bi-ticket-perforated-fill text-success"></i> Ingresos por Boletos</h2>
                        <p class="text-muted">Consulta el detalle financiero de rutas regulares.</p>
                        <span class="text-muted fs-5 fw-normal"> <%= request.getAttribute("nombreSucursal") %></span>

                        <form action="${pageContext.request.contextPath}/Reporte_Ingreso_Boletos_Sucursal" method="POST" class="row g-3 align-items-end mt-2 border-top pt-3">
                            <div class="col-md-2">
                                <label class="form-label fw-bold small">Fecha Inicio</label>
                                <input type="date" name="fecha_inicio" class="form-control" value="<%= request.getAttribute("fechaInicio")%>">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-bold small">Fecha Fin</label>
                                <input type="date" name="fecha_fin" class="form-control" value="<%= request.getAttribute("fechaFin")%>">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-bold small">Filtrar por Ruta</label>
                                <select name="id_ruta" class="form-select">
                                    <option value="">Todas las rutas</option>
                                    <% List<Ruta> rutas = (List<Ruta>) request.getAttribute("listaRutas");
                                    Integer rutaSel = (Integer) request.getAttribute("idRutaSeleccionada");
                                    if (rutas != null) {
                                        for (Ruta r : rutas) {%>
                                    <option value="<%= r.getIdRuta()%>" <%= (rutaSel != null && rutaSel == r.getIdRuta()) ? "selected" : ""%>>ID <%= r.getIdRuta()%> (Dist: <%= r.getDistanciaKm()%>km)</option>
                                    <% }
                                    } %>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-bold small">Filtrar por Bus</label>
                                <select name="id_bus" class="form-select">
                                    <option value="">Todos los buses</option>
                                    <% List<Bus> buses = (List<Bus>) request.getAttribute("listaBuses");
                                    Integer busSel = (Integer) request.getAttribute("idBusSeleccionado");
                                    if (buses != null) {
                                        for (Bus b : buses) {%>
                                    <option value="<%= b.getIdBus()%>" <%= (busSel != null && busSel == b.getIdBus()) ? "selected" : ""%>><%= b.getPlaca()%></option>
                                    <% }
                                    } %>
                                </select>
                            </div>
                            <div class="col-md-3 d-flex gap-2">
                                <button type="submit" class="btn btn-dark fw-bold w-50"><i class="bi bi-funnel"></i> Filtrar</button>
                                <button type="submit" name="accion" value="exportar_html" class="btn btn-outline-success fw-bold w-50"><i class="bi bi-file-earmark-arrow-down"></i> HTML</button>
                            </div>
                        </form>
                    </div>

                    <div class="p-4 bg-white rounded-4 shadow-sm">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle border">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID Viaje</th>
                                        <th>Ruta</th>
                                        <th>Fecha de Salida</th>
                                        <th>Boletos Vendidos</th>
                                        <th>Ingreso Generado</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<ReporteIngresoBoleto> reporte = (List<ReporteIngresoBoleto>) request.getAttribute("reporte");
                                        if (reporte != null && !reporte.isEmpty()) {
                                            for (ReporteIngresoBoleto r : reporte) {
                                    %>
                                    <tr>
                                        <td class="fw-bold text-secondary">#<%= r.getIdViaje()%></td>
                                        <td><%= r.getRuta()%></td>
                                        <td><%= r.getFechaViaje().toLocalDate()%> <small class="text-muted"><%= r.getFechaViaje().toLocalTime()%></small></td>
                                        <td class="fw-bold"><%= r.getCantidadBoletos()%></td>
                                        <td class="fw-bold text-success">Q. <%= String.format("%.2f", r.getIngresoTotal())%></td>
                                    </tr>
                                    <% }
                                } else { %>
                                    <tr><td colspan="5" class="text-center py-4 text-muted">No se encontraron ventas en este filtro.</td></tr>
                                    <% } %>
                                </tbody>
                                <% Double granIngreso = (Double) request.getAttribute("granTotalIngresos");
                                if (granIngreso != null && granIngreso > 0) {%>
                                <tfoot>
                                    <tr class="table-success fw-bold fs-5">
                                        <td colspan="3" class="text-end">TOTALES DEL REPORTE:</td>
                                        <td><%= request.getAttribute("totalBoletos")%></td>
                                        <td class="text-success">Q. <%= String.format("%.2f", granIngreso)%></td>
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