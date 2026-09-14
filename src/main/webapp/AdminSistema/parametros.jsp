<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Parámetros</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-5 bg-white rounded-4 shadow-sm">
                        <h2 class="fw-bold mb-4">Configuración del Sistema</h2>

                        <% if (session.getAttribute("mensajeExito") != null) {%>
                        <div class="alert alert-success" role="alert">
                            <%= session.getAttribute("mensajeExito")%>
                        </div>
                        <% session.removeAttribute("mensajeExito"); %>
                        <% } %>

                        <% if (request.getAttribute("error") != null) {%>
                        <div class="alert alert-danger" role="alert">
                            <%= request.getAttribute("error")%>
                        </div>
                        <% } else if (session.getAttribute("error") != null) {%>
                        <div class="alert alert-danger" role="alert">
                            <%= session.getAttribute("error")%>
                        </div>
                        <% session.removeAttribute("error"); %>
                        <% }%>

                        <div class="col-md-6 border rounded p-4 mt-3">
                            <form action="${pageContext.request.contextPath}/Parametros" method="POST">
                                <div class="mb-3">
                                    <label class="form-label fw-bold text-secondary">Depreciación por Kilómetro (Quetzales)</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light fw-bold">Q.</span>
                                        <input type="number" step="0.01" min="0" class="form-control" name="depreciacion" 
                                               value="${depreciacionActual != null ? depreciacionActual : '0.0'}" required>
                                    </div>
                                    <div class="form-text">Este valor es global y se utilizará para calcular automáticamente la devaluación de todos los buses en el sistema.</div>
                                </div>

                                <button type="submit" class="btn btn-primary">Guardar Configuración</button>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </body>
</html>