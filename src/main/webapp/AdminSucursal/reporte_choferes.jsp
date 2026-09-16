<%@page import="java.util.List"%>
<%@page import="modelosReportesSucursal.ReporteChofer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Reporte de Choferes</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />
                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4 d-flex justify-content-between align-items-center">
                        <div>
                            <h2 class="fw-bold"><i class="bi bi-person-badge text-primary"></i> Listado General de Choferes</h2>
                            <p class="text-muted mb-0">Rendimiento y estado actual del personal de conducción.</p>
                            <span class="text-muted fs-5 fw-normal"> <%= request.getAttribute("nombreSucursal") %></span>
                        </div>
                        <form action="${pageContext.request.contextPath}/Reporte_Choferes_Sucursal" method="POST">
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
                                        <th>No. Licencia</th>
                                        <th>Nombre Completo</th>
                                        <th>Tipo Licencia</th>
                                        <th>Vencimiento</th>
                                        <th>Estado</th>
                                        <th>Total Viajes</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<ReporteChofer> reporte = (List<ReporteChofer>) request.getAttribute("reporte");
                                        if (reporte != null && !reporte.isEmpty()) {
                                            for (ReporteChofer r : reporte) {
                                    %>
                                    <tr>
                                        <td class="fw-bold"><%= r.getLicencia()%></td>
                                        <td><%= r.getNombre()%></td>
                                        <td><span class="badge bg-dark"><%= r.getTipoLicencia()%></span></td>
                                        <td><%= r.getFechaVencimiento()%></td>
                                        <td class="fw-bold <%= r.getEstado().equals("Activo") ? "text-success" : "text-danger"%>"><%= r.getEstado()%></td>
                                        <td class="text-center fw-bold fs-5 text-primary"><%= r.getTotalViajes()%></td>
                                    </tr>
                                    <% }
                                } else { %>
                                    <tr><td colspan="6" class="text-center py-4 text-muted">No hay choferes registrados.</td></tr>
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