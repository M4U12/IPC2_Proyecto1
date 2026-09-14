<%@page import="modelos.Sucursal"%>
<%@page import="java.util.List"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Solicitar Viaje Privado</title>
        <jsp:include page="/Componentes/recursos.jsp" />
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <%
                        String errorMsg = (String) session.getAttribute("error");
                        DateTimeFormatter formatoInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        String fechaActual = LocalDateTime.now().format(formatoInput);
                    %>

                    <% if (errorMsg != null) {%>
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> <%= errorMsg%></div>
                    <% session.removeAttribute("error"); %>
                    <% } %>

                    <div class="row justify-content-center">
                        <div class="col-xl-10">
                            <div class="card shadow-sm border-0 rounded-4 overflow-hidden">
                                <div class="row g-0">
                                    <!-- Columna del Formulario -->
                                    <div class="col-lg-5 p-4 border-end">
                                        <div class="text-center mb-4">
                                            <i class="bi bi-bus-front display-5 text-primary"></i>
                                            <h4 class="fw-bold mt-2">Cotizar Alquiler</h4>
                                            <p class="text-muted small">Mueve los marcadores en el mapa para establecer tu ruta.</p>
                                        </div>

                                        <form action="${pageContext.request.contextPath}/Solicitar_Privado" method="POST">
                                            <div class="mb-3">
                                                <label class="form-label fw-bold small text-muted">¿Qué sucursal administrará tu viaje?</label>
                                                <select class="form-select" name="id_sucursal" required>
                                                    <option value="" selected disabled>Selecciona una sucursal...</option>
                                                    <%
                                                        List<Sucursal> sucursales = (List<Sucursal>) request.getAttribute("listaSucursales");
                                                        if (sucursales != null) {
                                                            for (Sucursal s : sucursales) {
                                                    %>
                                                    <option value="<%= s.getIdSucursal()%>"><%= s.getNombre()%> - <%= s.getDireccion()%></option>
                                                    <%      }
                                                        }
                                                    %>
                                                </select>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-bold small text-success"><i class="bi bi-geo-alt-fill"></i> Origen (Punto A)</label>
                                                <textarea class="form-control bg-light" id="inputOrigen" name="origen" rows="2" readonly required placeholder="Arrastra el marcador verde..."></textarea>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-bold small text-danger"><i class="bi bi-flag-fill"></i> Destino (Punto B)</label>
                                                <textarea class="form-control bg-light" id="inputDestino" name="destino" rows="2" readonly required placeholder="Arrastra el marcador rojo..."></textarea>
                                            </div>

                                            <div class="row g-2 mb-4">
                                                <div class="col-md-7">
                                                    <label class="form-label fw-bold small">Salida Esperada</label>
                                                    <input type="datetime-local" class="form-control" name="fecha_salida" min="<%= fechaActual%>" required>
                                                </div>
                                                <div class="col-md-5">
                                                    <label class="form-label fw-bold small">Pasajeros</label>
                                                    <input type="number" class="form-control" name="pasajeros" min="1" required>
                                                </div>
                                            </div>

                                            <button type="submit" class="btn btn-primary w-100 fw-bold py-2">Enviar Solicitud de Cotización</button>
                                        </form>
                                    </div>

                                    <!-- Columna del Mapa (Leaflet) -->
                                    <div class="col-lg-7 position-relative">
                                        <div id="mapaPrivado" style="height: 100%; min-height: 500px; width: 100%;"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        <script src="${pageContext.request.contextPath}/js/mapa.js"></script>
    </body>
</html>