<%@page import="modelos.Usuario"%>
<%@page import="modelos.Enums"%>
<%
    // Recuperar el usuario de la sesión, si existe
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
%>
<div class="col-md-3 col-lg-2 sidebar py-3">
    <h4 class="text-white text-center mb-4">Code 'n Buses</h4>

    <strong class="text-secondary section-title mt-2 d-block">GENERAL</strong>
    <a href="<%= request.getContextPath() %>/index.jsp" class="bg-primary text-white">Inicio</a>
    <a href="<%= request.getContextPath() %>/PaginasUsuarios/viajes_regulares.jsp">Viajes Disponibles</a>
    <a href="<%= request.getContextPath() %>/PaginasUsuarios/viajes_privados.jsp">Solicitar Viaje Privado</a>

    <% if (usuario == null) { %>
        <hr class="border-secondary mx-3">
        <a href="<%= request.getContextPath() %>/LoginServlet" class="text-info">Iniciar Sesión</a>
    <% } else { %>
        <strong class="text-secondary section-title mt-4 d-block">MI CUENTA</strong>
        <a href="<%= request.getContextPath() %>/PaginasUsuarios/comprar_boletos.jsp">Comprar Boletos</a>
        <a href="<%= request.getContextPath() %>/PaginasUsuarios/perfil.jsp">Perfil</a>
        <a href="<%= request.getContextPath() %>/PaginasUsuarios/cartera.jsp">Mi Cartera</a>

        <% if (usuario.getRol() == Enums.RolUsuario.ADMINISTRADOR_SISTEMA) { %>
            <strong class="text-warning section-title mt-4 d-block">ADMIN GLOBAL</strong>
            <a href="<%= request.getContextPath() %>/AdminPersonalServlet">Añadir Admin Sucursal</a>
            <a href="<%= request.getContextPath() %>/SucursalServlet">Gestión de Sucursales</a>
            <a href="<%= request.getContextPath() %>/AdminSistema/parametros.jsp">Parámetros</a>
            <a href="<%= request.getContextPath() %>/AdminSistema/reportes_globales.jsp">Reportes Globales</a>
            <a href="<%= request.getContextPath() %>/AdminSistema/mapa_rutas.jsp">Mapa de Rutas</a>
        <% } %>

        <% if (usuario.getRol() == Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) { %>
            <strong class="text-success section-title mt-4 d-block">ADMIN SUCURSAL</strong>
            <a href="<%= request.getContextPath() %>/AdminSucursal/gestion_buses.jsp">Buses y Choferes</a>
            <a href="<%= request.getContextPath() %>/AdminSucursal/rutas_viajes.jsp">Rutas y Viajes</a>
            <a href="<%= request.getContextPath() %>/AdminSucursal/registros_diarios.jsp">Registros Diarios</a>
            <a href="<%= request.getContextPath() %>/AdminSucursal/alquileres_privados.jsp">Alquileres Privados</a>
            <a href="<%= request.getContextPath() %>/AdminSucursal/reportes_sucursal.jsp">Reportes</a>
        <% } %>

        <hr class="border-secondary mx-3 mt-4">
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="text-danger">Cerrar Sesión</a>
    <% } %>
</div>