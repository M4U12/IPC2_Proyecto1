<%@page import="java.util.List"%>
<%@page import="modelos.Sucursal"%>
<%@page import="modelos.ReporteGanancias"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Reporte de Ganancias</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="fw-bold text-primary mb-1"><i class="bi bi-graph-up-arrow me-2"></i> Reporte de Ganancias</h2>
                            <p class="text-muted mb-0">Consolidado financiero por sucursal en un intervalo de tiempo.</p>
                        </div>
                        <button class="btn btn-outline-secondary" onclick="window.print()"><i class="bi bi-printer"></i> Imprimir</button>
                    </div>
                    <form action="${pageContext.request.contextPath}/Reporte_Ganancias" method="POST" class="m-0">
                        <input type="hidden" name="fecha_inicio" value="<%= request.getAttribute("fechaInicio")%>">
                        <input type="hidden" name="fecha_fin" value="<%= request.getAttribute("fechaFin")%>">
                        <input type="hidden" name="id_sucursal" value="<%= request.getAttribute("idSucursalSeleccionada")%>">
                        <input type="hidden" name="accion" value="exportar_html">
                        <button type="submit" class="btn btn-outline-success fw-bold"><i class="bi bi-filetype-html"></i> Exportar a HTML</button>
                    </form>

                    <% if (request.getAttribute("error") != null) {%>
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> <%= request.getAttribute("error")%></div>
                    <% }%>

                    <!-- Panel de Filtros -->
                    <div class="card border-0 shadow-sm rounded-4 mb-4">
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/Reporte_Ganancias" method="POST" class="row g-3 align-items-end">

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
                                    <button type="submit" class="btn btn-primary fw-bold w-50" title="Aplicar Filtros">
                                        <i class="bi bi-search"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/Reporte_Ganancias" class="btn btn-outline-secondary fw-bold w-50" title="Limpiar Filtros">
                                        <i class="bi bi-eraser"></i>
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <%
                        List<ReporteGanancias> reporte = (List<ReporteGanancias>) request.getAttribute("reporte");
                        if (reporte != null && !reporte.isEmpty()) {

                            Double totalIngresos = (Double) request.getAttribute("granTotalIngresos");
                            Double totalCostos = (Double) request.getAttribute("granTotalCostos");
                            Double totalUtilidad = (Double) request.getAttribute("granGananciaNeta");
                    %>

                    <!-- Tarjetas de Resumen Global -->
                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <div class="card border-0 bg-success bg-opacity-10 shadow-sm rounded-4 h-100 p-3 text-center">
                                <span class="text-success small fw-bold text-uppercase">Total Ingresos</span>
                                <h3 class="fw-bold text-success mb-0 mt-2">Q.<%= String.format(java.util.Locale.US, "%.2f", totalIngresos)%></h3>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="card border-0 bg-danger bg-opacity-10 shadow-sm rounded-4 h-100 p-3 text-center">
                                <span class="text-danger small fw-bold text-uppercase">Total Costos Operativos</span>
                                <h3 class="fw-bold text-danger mb-0 mt-2">Q.<%= String.format(java.util.Locale.US, "%.2f", totalCostos)%></h3>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="card border-0 bg-primary bg-opacity-10 shadow-sm rounded-4 h-100 p-3 text-center">
                                <span class="text-primary small fw-bold text-uppercase">Ganancia Neta Global</span>
                                <h3 class="fw-bold text-primary mb-0 mt-2">Q.<%= String.format(java.util.Locale.US, "%.2f", totalUtilidad)%></h3>
                            </div>
                        </div>
                    </div>

                    <!-- Tabla de Resultados -->
                    <div class="card border-0 shadow-sm rounded-4 p-4">
                        <div class="table-responsive">
                            <table class="table table-hover table-bordered align-middle text-center mb-0">
                                <thead class="table-dark">
                                    <tr>
                                        <th rowspan="2" class="align-middle">Sucursal</th>
                                        <th colspan="3" class="bg-success text-white">Ingresos</th>
                                        <th colspan="5" class="bg-danger text-white">Costos Operativos</th>
                                        <th rowspan="2" class="align-middle bg-primary text-white">Ganancia Neta</th>
                                    </tr>
                                    <tr>
                                        <th class="bg-light text-dark small">Boletos</th>
                                        <th class="bg-light text-dark small">Privados</th>
                                        <th class="bg-light text-dark fw-bold">Subtotal</th>
                                        <th class="bg-light text-dark small">Combustible</th>
                                        <th class="bg-light text-dark small">Mano Obra</th>
                                        <th class="bg-light text-dark small">Repuestos</th>
                                        <th class="bg-light text-dark small">Depreciación</th>
                                        <th class="bg-light text-dark fw-bold">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (ReporteGanancias r : reporte) {%>
                                    <tr>
                                        <td class="fw-bold text-start"><%= r.getNombreSucursal()%></td>

                                        <!-- Desglose Ingresos -->
                                        <td class="text-success"><%= String.format(java.util.Locale.US, "%.2f", r.getIngresosBoletos())%></td>
                                        <td class="text-success"><%= String.format(java.util.Locale.US, "%.2f", r.getIngresosPrivados())%></td>
                                        <td class="fw-bold bg-success bg-opacity-10 text-success"><%= String.format(java.util.Locale.US, "%.2f", r.getTotalIngresos())%></td>

                                        <!-- Desglose Costos -->
                                        <td class="text-danger"><%= String.format(java.util.Locale.US, "%.2f", r.getCostoCombustible())%></td>
                                        <td class="text-danger"><%= String.format(java.util.Locale.US, "%.2f", r.getCostoManoObraTaller())%></td>
                                        <td class="text-danger"><%= String.format(java.util.Locale.US, "%.2f", r.getCostoRepuestosTaller())%></td>
                                        <td class="text-danger"><%= String.format(java.util.Locale.US, "%.2f", r.getCostoDepreciacion())%></td>
                                        <td class="fw-bold bg-danger bg-opacity-10 text-danger"><%= String.format(java.util.Locale.US, "%.2f", r.getTotalCostos())%></td>

                                        <!-- Ganancia Neta de la Fila -->
                                        <td class="fw-bold fs-6 <%= r.getGananciaNeta() >= 0 ? "text-primary" : "text-danger"%>">
                                            <%= String.format(java.util.Locale.US, "%.2f", r.getGananciaNeta())%>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% } else { %>
                    <div class="alert alert-light text-center py-5 shadow-sm rounded-4 border-0 mt-4">
                        <i class="bi bi-inbox display-4 text-muted d-block mb-3"></i>
                        <h5 class="text-muted fw-bold">No se encontraron operaciones financieras</h5>
                        <p class="text-muted mb-0">No hay registros de ingresos ni egresos para la fecha y sucursal seleccionadas.</p>
                    </div>
                    <% }%>

                </div>
            </div>
        </div>
    </body>
</html>