<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.List"%>
<%@page import="modelos.Cartera"%>
<%@page import="modelos.Transaccion"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mi Cartera</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0 min-vh-100">

                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <%
                        Cartera cartera = (Cartera) request.getAttribute("miCartera");
                        List<Transaccion> historial = (List<Transaccion>) request.getAttribute("miHistorial");
                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        String exitoMsg = (String) request.getAttribute("mensajeExito");
                        if (exitoMsg == null) {
                            exitoMsg = (String) session.getAttribute("mensajeExito");
                        }

                        String errorMsg = (String) request.getAttribute("error");
                        if (errorMsg == null)
                            errorMsg = (String) session.getAttribute("error");
                    %>

                    <% if (exitoMsg != null) {%>
                    <div class="alert alert-success fw-bold"><%= exitoMsg%></div>
                    <% session.removeAttribute("mensajeExito");
                        } %>

                    <% if (errorMsg != null) {%>
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> <%= errorMsg%></div>
                    <% session.removeAttribute("error");
                        }%>

                    <div class="row g-4 mt-1">
                        <!-- Tarjeta de Saldo -->
                        <div class="col-md-4">
                            <div class="card border-0 shadow-sm rounded-4 text-center p-4">
                                <p class="text-muted small fw-bold mb-1">Saldo Disponible</p>
                                <h1 class="fw-bold text-success mb-4">
                                    Q.<%= cartera != null ? String.format("%.2f", cartera.getCantidadDinero()) : "0.00"%>
                                </h1>
                                <button class="btn btn-primary w-100 fw-bold" data-bs-toggle="modal" data-bs-target="#modalRecarga">
                                    <i class="bi bi-plus-circle"></i> Agregar Fondos
                                </button>
                            </div>
                        </div>

                        <!-- Historial de Transacciones -->
                        <div class="col-md-8">
                            <div class="card border-0 shadow-sm rounded-4 p-4">
                                <h5 class="fw-bold mb-4">Historial de Movimientos</h5>

                                <form action="${pageContext.request.contextPath}/Mi_Cartera" method="GET" class="row g-2 mb-4 bg-light p-3 rounded">
                                    <div class="col-md-5">
                                        <label class="form-label small text-muted mb-0">Desde:</label>
                                        <input type="date" class="form-control form-control-sm" name="fechaInicio" value="${param.fechaInicio}">
                                    </div>
                                    <div class="col-md-5">
                                        <label class="form-label small text-muted mb-0">Hasta:</label>
                                        <input type="date" class="form-control form-control-sm" name="fechaFin" value="${param.fechaFin}">
                                    </div>
                                    <div class="col-md-2 d-flex align-items-end gap-2">
                                        <button type="submit" class="btn btn-secondary btn-sm w-50" title="Buscar">
                                            <i class="bi bi-search"></i>
                                        </button>
                                        <a href="${pageContext.request.contextPath}/Mi_Cartera" class="btn btn-outline-danger btn-sm w-50" title="Limpiar Filtros">
                                            <i class="bi bi-eraser"></i>
                                        </a>
                                    </div>
                                    <% if (request.getParameter("fechaInicio") != null) { %>
                                    <div class="col-12 mt-2">
                                        <a href="${pageContext.request.contextPath}/Mi_Cartera" class="small text-decoration-none text-danger">Quitar filtros</a>
                                    </div>
                                    <% } %>
                                </form>

                                <% if (historial != null && !historial.isEmpty()) { %>
                                <div class="list-group list-group-flush">
                                    <% for (Transaccion t : historial) {
                                            boolean esIngreso = t.getTipo().name().equals("RECARGA");
                                    %>
                                    <div class="list-group-item px-0 py-3 d-flex justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1 fw-bold <%= esIngreso ? "text-success" : "text-danger"%>">
                                                <i class="bi <%= esIngreso ? "bi-arrow-down-left-circle-fill" : "bi-arrow-up-right-circle-fill"%> me-2"></i>
                                                <%= t.getTipo().name().replace("_", " ")%>
                                            </h6>
                                            <small class="text-muted"><%= t.getDescripcion()%></small><br>
                                            <small class="text-muted"><i class="bi bi-clock"></i> <%= t.getFechaHora().format(formatoFecha)%></small>
                                        </div>
                                        <span class="fs-5 fw-bold <%= esIngreso ? "text-success" : "text-dark"%>">
                                            <%= esIngreso ? "+" : "-"%> Q.<%= String.format("%.2f", t.getMonto())%>
                                        </span>
                                    </div>
                                    <% } %>
                                </div>
                                <% } else { %>
                                <p class="text-muted text-center py-5">No hay movimientos en este periodo.</p>
                                <% }%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Recarga -->
        <div class="modal fade" id="modalRecarga" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <div class="modal-header border-bottom-0">
                        <h5 class="modal-title fw-bold">Recargar Billetera</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/Mi_Cartera" method="POST">
                        <div class="modal-body py-0">
                            <input type="hidden" name="accion" value="recargar">
                            <label class="form-label fw-bold">Monto a ingresar</label>
                            <div class="input-group input-group-lg mb-3">
                                <span class="input-group-text bg-light fw-bold">Q.</span>
                                <input type="number" class="form-control fw-bold" name="monto" step="0.01" min="1" required placeholder="0.00">
                            </div>
                        </div>
                        <div class="modal-footer border-top-0">
                            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-success fw-bold px-4">Confirmar Recarga</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>