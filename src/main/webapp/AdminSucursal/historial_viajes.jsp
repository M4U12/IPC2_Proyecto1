<%@page import="java.util.List"%>
<%@page import="modelos.Viaje"%>
<%@page import="modelos.ViajePrivado"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Historial Viajes</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <div class="d-flex align-items-center gap-3 mb-2">
                            <h2 class="fw-bold mb-0">Registro Inalterable  de Viajes</h2>
                        </div>
                        <p class="text-muted">Registro histórico</p>

                        <% if (request.getAttribute("error") != null) {%>
                        <div class="alert alert-danger fw-bold"><%= request.getAttribute("error")%></div>
                        <% } %>
                    </div>

                    <div class="bg-white rounded-4 shadow-sm p-4">
                        <!-- Navegación de Pestañas -->
                        <ul class="nav nav-tabs fw-bold mb-4" id="bitacoraTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active text-primary" id="regulares-tab" data-bs-toggle="tab" data-bs-target="#regulares" type="button" role="tab">
                                    <i class="bi bi-bus-front"></i> Rutas Regulares
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link text-success" id="privados-tab" data-bs-toggle="tab" data-bs-target="#privados" type="button" role="tab">
                                    <i class="bi bi-star-fill"></i> Alquileres Privados
                                </button>
                            </li>
                        </ul>
                        <%
                            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        %>
                        <!-- Contenido de las Pestañas -->
                        <div class="tab-content" id="bitacoraTabsContent">

                            <!-- PESTAÑA 1: REGULARES -->
                            <div class="tab-pane fade show active" id="regulares" role="tabpanel">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle border">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID Viaje</th>
                                                <th>Destino</th>
                                                <th>Salida Real</th>
                                                <th>Llegada Real</th>
                                                <th>Km Recorridos</th>
                                                <th>Gasto Combustible</th>
                                                <th>Estado</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<Viaje> regulares = (List<Viaje>) request.getAttribute("historialRegulares");
                                                if (regulares != null && !regulares.isEmpty()) {
                                                    for (Viaje v : regulares) {
                                                        double kmRecorridos = (v.getKilometrajeLlegada() != null && v.getKilometrajeSalida() != null) ? (v.getKilometrajeLlegada() - v.getKilometrajeSalida()) : 0;
                                            %>
                                            <tr>
                                                <td class="fw-bold text-muted">#<%= v.getIdViaje()%></td>
                                                <td><%= v.getNombreDestino()%></td>
                                                <td><%= (v.getFechaHoraSalidaReal() != null) ? v.getFechaHoraSalidaReal().format(formatoFecha) : "Sin registro"%></td>
                                                <td><%= (v.getFechaHoraLlegadaReal() != null) ? v.getFechaHoraLlegadaReal().format(formatoFecha) : "Sin registro"%></td>
                                                <td class="fw-bold"><%= kmRecorridos%> km</td>
                                                <td class="text-danger fw-bold">Q. <%= v.getGastoCombustible()%></td>
                                                <td><span class="badge bg-secondary"><i class="bi bi-lock-fill"></i> FINALIZADO</span></td>
                                            </tr>
                                            <%      }
                                            } else { %>
                                            <tr><td colspan="6" class="text-center text-muted py-4">No hay registros de viajes regulares finalizados.</td></tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- PESTAÑA 2: PRIVADOS -->
                            <div class="tab-pane fade" id="privados" role="tabpanel">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle border">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID Viaje</th>
                                                <th>Cliente</th>
                                                <th>Ruta</th>
                                                <th>Salida Real</th>
                                                <th>Llegada Real</th>
                                                <th>Km Recorridos</th>
                                                <th>Gasto Combustible</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<ViajePrivado> privados = (List<ViajePrivado>) request.getAttribute("historialPrivados");
                                                if (privados != null && !privados.isEmpty()) {
                                                    for (ViajePrivado vp : privados) {
                                                        double kmRecorridos = (vp.getKilometrajeLlegada() != null && vp.getKilometrajeSalida() != null) ? (vp.getKilometrajeLlegada() - vp.getKilometrajeSalida()) : 0;
                                            %>
                                            <tr>
                                                <td class="fw-bold text-muted">#<%= vp.getIdViajePrivado()%></td>
                                                <td><%= vp.getNombreCliente()%></td>
                                                <td><small class="d-block text-success">Origen: <%= vp.getOrigen()%></small>
                                                    <small class="d-block text-danger">Destino: <%= vp.getDestino()%></small></td>
                                                <td><%= (vp.getFechaHoraSalidaReal() != null) ? vp.getFechaHoraSalidaReal().format(formatoFecha) : "Sin registro"%></td>
                                                <td><%= (vp.getFechaHoraLlegadaReal() != null) ? vp.getFechaHoraLlegadaReal().format(formatoFecha) : "Sin registro"%></td>
                                                <td class="fw-bold"><%= kmRecorridos%> km</td>
                                                <td class="text-danger fw-bold">Q. <%= vp.getGastoCombustible()%></td>
                                            </tr>
                                            <%      }
                                            } else { %>
                                            <tr><td colspan="7" class="text-center text-muted py-4">No hay registros de viajes privados finalizados.</td></tr>
                                            <% }%>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>