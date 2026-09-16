<%@page import="java.util.List"%>
<%@page import="modelos.Viaje"%>
<%@page import="modelos.Cartera"%>
<%@page import="modelos.Usuario"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Comprar Boleto</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">
                    <%
                        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
                        Viaje viaje = (Viaje) request.getAttribute("viaje");
                        Cartera cartera = (Cartera) request.getAttribute("cartera");
                        List<Integer> ocupados = (List<Integer>) request.getAttribute("asientosOcupados");

                        Double precio = (Double) request.getAttribute("precio");
                        Integer capacidad = (Integer) request.getAttribute("capacidadBus");

                        double saldo = (cartera != null) ? cartera.getCantidadDinero() : 0.0;
                        boolean saldoSuficiente = saldo >= precio;

                        int asientosLibres = capacidad - (ocupados != null ? ocupados.size() : 0);
                        DateTimeFormatter formatoInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        String fechaActual = LocalDateTime.now().format(formatoInput);
                    %>

                    <div class="row justify-content-center">
                        <div class="col-md-6">
                            <% if (session.getAttribute("error") != null) {%>
                            <div class="alert alert-danger"><%= session.getAttribute("error")%></div>
                            <% session.removeAttribute("error"); %>
                            <% }%>

                            <div class="card shadow-sm border-0 rounded-4">
                                <div class="card-header bg-primary text-white p-4 text-center rounded-top-4">
                                    <h4 class="fw-bold mb-0">Comprar Boleto</h4>
                                    <small>Viaje #<%= viaje.getIdViaje()%></small>
                                </div>

                                <div class="card-body p-4">
                                    <!-- Tarjeta de Usuario y Saldo -->
                                    <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded-3 mb-4 border">
                                        <div>
                                            <div class="text-muted small fw-bold">Comprador</div>
                                            <div class="fw-bold fs-5"><%= usuario.getNombre()%></div>
                                        </div>
                                        <div class="text-end">
                                            <div class="text-muted small fw-bold">Saldo en Cartera</div>
                                            <div class="fs-5 fw-bold <%= saldoSuficiente ? "text-success" : "text-danger"%>">
                                                Q.<%= saldo%>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex justify-content-between mb-4">
                                        <span class="fs-5 text-muted">Precio del Boleto:</span>
                                        <span class="fs-4 fw-bold text-dark">Q.<%= precio%></span>
                                    </div>

                                    <form action="${pageContext.request.contextPath}/Comprar_Boleto" method="POST">
                                        <input type="hidden" name="id_viaje" value="<%= viaje.getIdViaje()%>">
                                        <input type="hidden" name="precio" value="<%= precio%>">
                                        <input type="hidden" name="id_cartera" value="<%= cartera != null ? cartera.getIdCartera() : 0%>">

                                        <div class="mb-3">
                                            <label class="form-label fw-bold">Fecha y Hora de Pago</label>
                                            <input type="datetime-local" class="form-control" name="fecha_pago" value="<%= fechaActual %>" required>
                                        </div>
                                        <div class="mb-4">
                                            <label class="form-label fw-bold">Selecciona tu asiento (<%= asientosLibres%> libres)</label>
                                            <select name="numero_asiento" class="form-select form-select-lg" required>
                                                <option value="" selected disabled>Elige un asiento disponible...</option>
                                                <%
                                                    for (int i = 1; i <= capacidad; i++) {
                                                        if (ocupados == null || !ocupados.contains(i)) {
                                                %>
                                                <option value="<%= i%>">Asiento #<%= i%></option>
                                                <%
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>

                                        <% if (!saldoSuficiente) { %>
                                        <div class="alert alert-warning small fw-bold text-center">
                                            <i class="bi bi-exclamation-triangle-fill"></i> Saldo insuficiente para realizar esta compra.
                                        </div>
                                        <a href="${pageContext.request.contextPath}/Mi_Cartera" class="btn btn-outline-primary w-100 fw-bold py-2">Recargar Cartera</a>
                                        <% } else if (asientosLibres == 0) { %>
                                        <div class="alert alert-danger small fw-bold text-center">
                                            <i class="bi bi-x-circle-fill"></i> Este viaje está totalmente lleno.
                                        </div>
                                        <% } else {%>
                                        <button type="submit" class="btn btn-success w-100 fw-bold py-2 fs-5" onclick="return confirm('¿Confirmar pago de Q.<%= precio%> por el boleto?');">
                                            <i class="bi bi-ticket-perforated"></i> Confirmar Compra
                                        </button>
                                        <% }%>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </body>
</html>