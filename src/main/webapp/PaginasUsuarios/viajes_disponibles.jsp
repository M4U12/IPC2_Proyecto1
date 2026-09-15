<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.List"%>
<%@page import="modelos.ViajeDisponibleDetalle"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Viajes Disponibles</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />
                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <div class="text-center mb-5">
                        <h1 class="fw-bold text-primary display-5">Encuentra tu próximo destino</h1>
                        <p class="text-muted fs-5">Explora nuestros viajes programados y asegura tu lugar.</p>
                    </div>

                    <% if (request.getAttribute("error") != null) {%>
                    <div class="alert alert-danger"><%= request.getAttribute("error")%></div>
                    <% } %>

                    <div class="row g-4">
                        <%
                            List<ViajeDisponibleDetalle> listaViajes = (List<ViajeDisponibleDetalle>) request.getAttribute("listaViajes");
                            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");
                            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd MMM, yyyy");

                            if (listaViajes != null && !listaViajes.isEmpty()) {
                                for (ViajeDisponibleDetalle v : listaViajes) {
                        %>

                        <!-- Tarjeta de Viaje -->
                        <div class="col-md-6 col-xl-4">
                            <div class="card trip-card shadow-sm h-100 rounded-4 overflow-hidden">
                                <div class="bg-primary text-white p-3 d-flex justify-content-between align-items-center">
                                    <div>
                                        <small class="text-white-50 d-block text-uppercase fw-bold"><%= v.getFechaHoraSalidaEstimada().format(formatoFecha)%></small>
                                        <span class="fs-4 fw-bold"><i class="bi bi-clock"></i> <%= v.getFechaHoraSalidaEstimada().format(formatoHora)%></span>
                                    </div>
                                    <span class="badge bg-light text-primary border-primary">#<%= v.getIdViaje()%></span>
                                </div>

                                <div class="card-body p-4">
                                    <div class="d-flex align-items-center mb-3">
                                        <div class="text-center w-100">
                                            <div class="text-muted small fw-bold text-uppercase">Sale de</div>
                                            <div class="fw-bold fs-5 text-dark"><%= v.getOrigen()%></div>
                                        </div>
                                        <div class="px-2 text-primary fs-3"><i class="bi bi-arrow-right"></i></div>
                                        <div class="text-center w-100">
                                            <div class="text-muted small fw-bold text-uppercase">Llega a</div>
                                            <div class="fw-bold fs-5 text-dark"><%= v.getDestino()%></div>
                                        </div>
                                    </div>

                                    <hr class="text-muted">

                                    <div class="d-flex justify-content-between align-items-center mt-4">
                                        <div>
                                            <small class="text-muted d-block fw-bold">Tarifa por persona</small>
                                            <span class="price-tag fw-bold fs-5 text-success">Q.<%= String.format(java.util.Locale.US, "%.2f", v.getPrecio())%></span>
                                        </div>

                                        <form action="${pageContext.request.contextPath}/Comprar_Boleto" method="GET" class="m-0">
                                            <input type="hidden" name="id_viaje" value="<%= v.getIdViaje()%>">
                                            <button type="submit" class="btn btn-dark fw-bold px-4 rounded-pill">Reservar</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%      }
                        } else { %>
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-bus-front text-muted" style="font-size: 4rem;"></i>
                            <h4 class="text-muted mt-3 fw-bold">No hay viajes programados en este momento.</h4>
                            <p class="text-muted">Vuelve a consultar más tarde para descubrir nuevas rutas.</p>
                        </div>
                        <% }%>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>