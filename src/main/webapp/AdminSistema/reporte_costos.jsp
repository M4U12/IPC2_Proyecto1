<%@page import="java.util.List"%>
<%@page import="modelos.Sucursal"%>
<%@page import="modelos.ReporteGanancias"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Costos Operativos</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="fw-bold text-danger mb-1"><i class="bi bi-tools me-2"></i> Reporte de Costos Operativos</h2>
                            <p class="text-muted mb-0">Desglose de gastos por categoría operativa.</p>
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-secondary fw-bold" onclick="window.print()"><i class="bi bi-printer"></i> Imprimir</button>
                            <form action="${pageContext.request.contextPath}/Reporte_Costos" method="POST" class="m-0">
                                <input type="hidden" name="fecha_inicio" value="<%= request.getAttribute("fechaInicio")%>">
                                <input type="hidden" name="fecha_fin" value="<%= request.getAttribute("fechaFin")%>">
                                <input type="hidden" name="id_sucursal" value="<%= request.getAttribute("idSucursalSeleccionada")%>">
                                <input type="hidden" name="accion" value="exportar_html">
                                <button type="submit" class="btn btn-outline-danger fw-bold"><i class="bi bi-filetype-html"></i> Exportar a HTML</button>
                            </form>
                        </div>
                    </div>

                    <% if (request.getAttribute("error") != null) {%>
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> <%= request.getAttribute("error")%></div>
                    <% }%>

                    <!-- Panel de Filtros -->
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/Reporte_Costos" method="POST" class="row g-3 align-items-end">

                                <div class="col-md-3">
                                    <label class="form-label fw-bold small text-muted mb-1">Fecha de Inicio</label>
                                    <input type="date" class="form-control" name="fecha_inicio" value="<%= request.getAttribute("fechaInicio")%>">
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold small text-muted mb-1">Fecha de Fin</label>
                                    <input type="date" class="form-control" name="fecha_fin" value="<%= request.getAttribute("fechaFin")%>">
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted mb-1">Filtrar por Sucursal</label>
                                    <select class="form-select" name="id_sucursal">
                                        <option value="0">Todas las sucursales</option>
                                        <%
                                            List<Sucursal> listaSucursales = (List<Sucursal>) request.getAttribute("listaSucursales");
                                            Integer sucursalActivaAttr = (Integer) request.getAttribute("idSucursalSeleccionada");
                                            int sucursalActiva = (sucursalActivaAttr != null) ? sucursalActivaAttr : 0;

                                            if (listaSucursales != null) {
                                                for (Sucursal s : listaSucursales) {
                                        %>
                                        <option value="<%= s.getIdSucursal()%>" <%= (s.getIdSucursal() == sucursalActiva) ? "selected" : ""%>>
                                            <%= s.getNombre()%>
                                        </option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>

                                <div class="col-md-2 d-flex gap-2">
                                    <button type="submit" class="btn btn-primary fw-bold w-50" title="Generar Filtro"><i class="bi bi-search"></i></button>
                                    <a href="${pageContext.request.contextPath}/Reporte_Costos" class="btn btn-outline-secondary fw-bold w-50" title="Limpiar Filtros"><i class="bi bi-eraser"></i></a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <%
                        List<ReporteGanancias> reporte = (List<ReporteGanancias>) request.getAttribute("reporte");
                        if (reporte != null && !reporte.isEmpty()) {

                            Double granTotal = (Double) request.getAttribute("granTotal");
                    %>

                    <!-- Resumen por Categoría -->
                    <div class="row g-2 mb-4">
                        <div class="col-md-3">
                            <div class="card border-0 bg-light shadow-sm text-center p-3">
                                <span class="text-muted small fw-bold">Combustible Total</span>
                                <h5 class="fw-bold text-dark mt-1">Q.<%= String.format("%.2f", request.getAttribute("granCombustible"))%></h5>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card border-0 bg-light shadow-sm text-center p-3">
                                <span class="text-muted small fw-bold">Mano Obra Total</span>
                                <h5 class="fw-bold text-dark mt-1">Q.<%= String.format("%.2f", request.getAttribute("granManoObra"))%></h5>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card border-0 bg-light shadow-sm text-center p-3">
                                <span class="text-muted small fw-bold">Repuestos Total</span>
                                <h5 class="fw-bold text-dark mt-1">Q.<%= String.format("%.2f", request.getAttribute("granRepuestos"))%></h5>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card border-0 bg-light shadow-sm text-center p-3">
                                <span class="text-muted small fw-bold">Depreciación Total</span>
                                <h5 class="fw-bold text-dark mt-1">Q.<%= String.format("%.2f", request.getAttribute("granDepreciacion"))%></h5>
                            </div>
                        </div>
                    </div>

                    <!-- Tabla de Resultados -->
                    <div class="card border-0 shadow-sm rounded-4 p-4">

                        <div class="d-flex justify-content-end mb-3">
                            <h4 class="fw-bold text-danger m-0">Costo Total: Q.<%= String.format("%.2f", granTotal)%></h4>
                        </div>

                        <div class="table-responsive">
                            <table class="table table-hover table-bordered align-middle text-center mb-0">
                                <thead class="table-dark">
                                    <tr>
                                        <th class="align-middle">Sucursal</th>
                                        <th>Gasto Combustible</th>
                                        <th>Mano de Obra Taller</th>
                                        <th>Repuestos Taller</th>
                                        <th>Depreciación Acumulada</th>
                                        <th class="bg-danger text-white">Subtotal Costos</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (ReporteGanancias r : reporte) {%>
                                    <tr>
                                        <td class="fw-bold text-start"><%= r.getNombreSucursal()%></td>
                                        <td class="text-muted"><%= String.format("%.2f", r.getCostoCombustible())%></td>
                                        <td class="text-muted"><%= String.format("%.2f", r.getCostoManoObraTaller())%></td>
                                        <td class="text-muted"><%= String.format("%.2f", r.getCostoRepuestosTaller())%></td>
                                        <td class="text-muted"><%= String.format("%.2f", r.getCostoDepreciacion())%></td>
                                        <td class="fw-bold fs-6 text-danger bg-danger bg-opacity-10"><%= String.format("%.2f", r.getTotalCostos())%></td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% } else { %>
                    <div class="alert alert-light text-center py-5 shadow-sm rounded-4 border-0 mt-4">
                        <i class="bi bi-inbox display-4 text-muted d-block mb-3"></i>
                        <h5 class="text-muted fw-bold">No se registraron costos</h5>
                        <p class="text-muted mb-0">No hay egresos registrados en el sistema para la fecha y sucursal indicadas.</p>
                    </div>
                    <% }%>

                </div>
            </div>
        </div>
    </body>
</html>