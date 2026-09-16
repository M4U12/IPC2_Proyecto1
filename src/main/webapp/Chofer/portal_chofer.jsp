<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="modelos.Chofer"%>
<%@page import="modelos.Viaje"%>
<%@page import="modelos.ViajePrivado"%>
<%@page import="modelos.Enums"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Portal de Choferes</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="d-flex align-items-center min-vh-100 py-5">
        
        <div class="container">

            <%
                String error = (String) session.getAttribute("error");
                String exito = (String) session.getAttribute("mensajeExito");
                if (error != null) {%><div class="alert alert-danger portal-card mb-4 text-center fw-bold"><i class="bi bi-x-circle-fill"></i> <%= error%></div><% session.removeAttribute("error");
                    }
                    if (exito != null) {%><div class="alert alert-success portal-card mb-4 text-center fw-bold"><i class="bi bi-check-circle-fill"></i> <%= exito%></div><% session.removeAttribute("mensajeExito");
                    }

                    Chofer choferActivo = (Chofer) session.getAttribute("choferLogueado");
                    DateTimeFormatter formatoInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    String ahora = LocalDateTime.now().format(formatoInput);

                    if (choferActivo == null) {
            %>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary fw-bold position-absolute top-0 start-0 m-4 shadow-sm">
                <i class="bi bi-arrow-left me-1"></i> Volver al Inicio
            </a>
            <!-- PANTALLA DE LOGIN -->
            <div class="card border-0 bg-white p-5 portal-card">
                <div class="text-center mb-4">
                    <i class="bi bi-person-badge text-primary" style="font-size: 4rem;"></i>
                    <h2 class="fw-bold mt-2">Kiosco de Choferes</h2>
                    <p class="text-muted">Ingresa tu número de licencia para acceder a tu itinerario.</p>
                </div>
                <form action="${pageContext.request.contextPath}/Portal_Chofer" method="POST">
                    <input type="hidden" name="accion" value="login">
                    <div class="mb-4">
                        <label class="form-label fw-bold text-muted">Número de Licencia</label>
                        <input type="text" class="form-control form-control-lg text-center fw-bold" name="num_licencia" placeholder="Ej: 12345678" required autocomplete="off" style="font-size: 1.5rem; letter-spacing: 2px;">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 btn-gigante"><i class="bi bi-box-arrow-in-right"></i> Acceder a mi Turno</button>
                </form>
            </div>
            <% } else {
                //  PANEL DEL CHOFER 
                Viaje vRegular = (Viaje) request.getAttribute("viajeRegular");
                ViajePrivado vPrivado = (ViajePrivado) request.getAttribute("viajePrivado");
                Double kmBus = (Double) request.getAttribute("kmActualBus");
            %>
            <div class="card border-0 bg-white p-4 portal-card position-relative">
                <form action="${pageContext.request.contextPath}/Portal_Chofer" method="POST" class="position-absolute top-0 end-0 m-3">
                    <input type="hidden" name="accion" value="logout">
                    <button type="submit" class="btn btn-sm btn-outline-danger fw-bold"><i class="bi bi-power"></i> Salir</button>
                </form>

                <div class="d-flex align-items-center mb-4 mt-2">
                    <% if (choferActivo.getFoto() == null || choferActivo.getFoto().isEmpty()) { %>
                        <img src="${pageContext.request.contextPath}/assets/default_user.png" 
                             alt="Foto Chofer" class="rounded-circle me-3 border border-3 border-primary shadow-sm" 
                             style="width: 85px; height: 85px; object-fit: cover; background-color: #e9ecef;">
                    <% } else { %>
                        <img src="data:image/jpeg;base64,<%= choferActivo.getFoto() %>" 
                             alt="Foto Chofer" class="rounded-circle me-3 border border-3 border-primary shadow-sm" 
                             style="width: 85px; height: 85px; object-fit: cover; background-color: #e9ecef;">
                    <% } %>
                    <div>
                        <h4 class="fw-bold mb-0">Hola, <%= choferActivo.getNombre()%></h4>
                        <span class="badge bg-primary mt-1">Licencia: <%= choferActivo.getNumLicencia()%></span>
                    </div>
                </div>

                <% if (vRegular == null && vPrivado == null) { %>
                <!-- Sin Viajes -->
                <div class="text-center py-5 bg-light rounded-4">
                    <i class="bi bi-cup-hot text-muted" style="font-size: 3rem"></i>
                    <h5 class="fw-bold mt-3">Sin viajes activos</h5>
                    <p class="text-muted mb-0">No tienes ninguna ruta programada o en curso. Espera instrucciones en la terminal.</p>
                </div>
                <% } else if (vRegular != null) {
                    boolean esSalida = (vRegular.getEstadoViaje() == Enums.EstadoViaje.PROGRAMADO);
                %>
                <!-- Viaje Regular -->
                <div class="alert alert-info border-0 rounded-4 p-4">
                    <h5 class="fw-bold text-info-emphasis mb-3"><i class="bi bi-signpost-split-fill"></i> Ruta Asignada</h5>
                    <form action="${pageContext.request.contextPath}/Portal_Chofer" method="POST">
                        <input type="hidden" name="accion" value="<%= esSalida ? "iniciar_regular" : "finalizar_regular"%>">
                        <input type="hidden" name="id_viaje" value="<%= vRegular.getIdViaje()%>">

                        <% if (!esSalida) {%>
                        <input type="hidden" name="id_bus" value="<%= vRegular.getIdBus()%>">
                        <input type="hidden" name="id_chofer" value="<%= choferActivo.getIdChofer()%>">
                        <% }%>

                        <div class="mb-3">
                            <label class="fw-bold small">Kilometraje <%= esSalida ? "de Salida" : "Final (Llegada)"%></label>
                            <input type="number" step="0.1" name="<%= esSalida ? "km_salida" : "km_llegada"%>" class="form-control form-control-lg fw-bold" 
                                   min="<%= esSalida ? kmBus : vRegular.getKilometrajeSalida()%>" 
                                   value="<%= esSalida ? kmBus : vRegular.getKilometrajeSalida()%>" required>
                        </div>

                        <% if (!esSalida) { %>
                        <div class="mb-3">
                            <label class="fw-bold small">Gasto de Combustible (Q)</label>
                            <input type="number" step="0.01" min="0" name="gasto_combustible" class="form-control form-control-lg fw-bold" required>
                        </div>
                        <% }%>

                        <div class="mb-4">
                            <label class="fw-bold small">Hora Real de <%= esSalida ? "Salida" : "Llegada"%></label>
                            <input type="datetime-local" name="fecha_hora" class="form-control form-control-lg" value="<%= ahora%>" required>
                        </div>

                        <button type="submit" class="btn <%= esSalida ? "btn-success" : "btn-dark"%> w-100 btn-gigante">
                            <i class="bi <%= esSalida ? "bi-play-fill" : "bi-stop-fill"%>"></i> <%= esSalida ? "Iniciar Ruta" : "Finalizar Ruta"%>
                        </button>
                    </form>
                </div>
                <% } else if (vPrivado != null) {
                    boolean esSalida = (vPrivado.getEstado() == Enums.EstadoViaje.PAGADA);
                %>
                <!-- Viaje Privado -->
                <div class="alert alert-warning border-0 rounded-4 p-4">
                    <h5 class="fw-bold text-warning-emphasis mb-3"><i class="bi bi-star-fill"></i> Servicio Privado</h5>
                    <p class="mb-3 fw-bold small"><i class="bi bi-geo-alt"></i> <%= vPrivado.getOrigen()%> <i class="bi bi-arrow-right"></i> <%= vPrivado.getDestino()%></p>

                    <form action="${pageContext.request.contextPath}/Portal_Chofer" method="POST">
                        <input type="hidden" name="accion" value="<%= esSalida ? "iniciar_privado" : "finalizar_privado"%>">
                        <input type="hidden" name="id_viaje" value="<%= vPrivado.getIdViajePrivado()%>">

                        <% if (!esSalida) {%>
                        <input type="hidden" name="id_bus" value="<%= vPrivado.getIdBus()%>">
                        <input type="hidden" name="id_chofer" value="<%= choferActivo.getIdChofer()%>">
                        <% }%>

                        <div class="mb-3">
                            <label class="fw-bold small">Kilometraje <%= esSalida ? "de Salida" : "Final (Llegada)"%></label>
                            <input type="number" step="0.1" name="<%= esSalida ? "km_salida" : "km_llegada"%>" class="form-control form-control-lg fw-bold" 
                                   min="<%= esSalida ? kmBus : vPrivado.getKilometrajeSalida()%>" 
                                   value="<%= esSalida ? kmBus : vPrivado.getKilometrajeSalida()%>" required>
                        </div>

                        <% if (!esSalida) { %>
                        <div class="mb-3">
                            <label class="fw-bold small">Gasto de Combustible (Q)</label>
                            <input type="number" step="0.01" min="0" name="gasto_combustible" class="form-control form-control-lg fw-bold" required>
                        </div>
                        <% }%>

                        <div class="mb-4">
                            <label class="fw-bold small">Hora Real de <%= esSalida ? "Salida" : "Llegada"%></label>
                            <input type="datetime-local" name="fecha_hora" class="form-control form-control-lg" value="<%= ahora%>" required>
                        </div>

                        <button type="submit" class="btn <%= esSalida ? "btn-success" : "btn-dark"%> w-100 btn-gigante">
                            <i class="bi <%= esSalida ? "bi-play-fill" : "bi-stop-fill"%>"></i> <%= esSalida ? "Iniciar Viaje Privado" : "Finalizar Viaje Privado"%>
                        </button>
                    </form>
                </div>
                <% } %>
            </div>
            <% }%>

        </div>
    </body>
</html>