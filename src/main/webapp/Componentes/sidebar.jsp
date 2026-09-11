<%@page import="modelos.Usuario"%>
<%@page import="modelos.Enums"%>
<%
    // Recuperar el usuario de la sesión, si existe
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
%>
<div class="col-md-3 col-lg-2 sidebar py-3">
    <h4 class="text-white text-center mb-4">Code 'n Buses</h4>

    <strong class="text-secondary section-title mt-2 d-block">GENERAL</strong>
    <a href="${pageContext.request.contextPath}/index.jsp" class="bg-primary text-white">Inicio</a>
    <a href="${pageContext.request.contextPath}/PaginasUsuarios/viajes_regulares.jsp">Viajes Disponibles</a>
    <a href="${pageContext.request.contextPath}/PaginasUsuarios/viajes_privados.jsp">Solicitar Viaje Privado</a>

    <% if (usuario == null) { %>
        <hr class="border-secondary mx-3">
        <a href="${pageContext.request.contextPath}/Login" class="text-info">Iniciar Sesión</a>
    <% } else { %>
        <strong class="text-secondary section-title mt-4 d-block">MI CUENTA</strong>
        <a href="${pageContext.request.contextPath}/PaginasUsuarios/comprar_boletos.jsp">Comprar Boletos</a>
        <a href="${pageContext.request.contextPath}/PaginasUsuarios/perfil.jsp">Perfil</a>
        <a href="${pageContext.request.contextPath}/PaginasUsuarios/cartera.jsp">Mi Cartera</a>

        <% if (usuario.getRol() == Enums.RolUsuario.ADMINISTRADOR_SISTEMA) { %>
            <strong class="text-warning section-title mt-4 d-block">ADMIN GLOBAL</strong>
            <a href="${pageContext.request.contextPath}/Registro_Administrador_Sucursal">Añadir Admin Sucursal</a>
            <a href="${pageContext.request.contextPath}/Gestionar_Sucursales">Gestión de Sucursales</a>
            <a href="${pageContext.request.contextPath}/Gestionar_Admin_Sucursal">Administrar Personal</a>
            <a href="${pageContext.request.contextPath}/Parametros">Parámetros</a>
            <a href="${pageContext.request.contextPath}/AdminSistema/reportes_globales.jsp">Reportes Globales</a>
            <a href="${pageContext.request.contextPath}/AdminSistema/mapa_rutas.jsp">Mapa de Rutas</a>
        <% } %>

        <% if (usuario.getRol() == Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) { %>
            <strong class="text-success section-title mt-4 d-block">ADMIN SUCURSAL</strong>
            <a href="${pageContext.request.contextPath}/Gestionar_Choferes">Gestión de Choferes</a>
            <a href="${pageContext.request.contextPath}/Gestionar_Buses">Gestión de Buses</a>
            <a href="${pageContext.request.contextPath}/AdminSucursal/rutas_viajes.jsp">Rutas y Viajes</a>
            <a href="${pageContext.request.contextPath}/AdminSucursal/registros_diarios.jsp">Registros Diarios</a>
            <a href="${pageContext.request.contextPath}/AdminSucursal/alquileres_privados.jsp">Alquileres Privados</a>
            <a href="${pageContext.request.contextPath}/AdminSucursal/reportes_sucursal.jsp">Reportes</a>
        <% } %>

        <hr class="border-secondary mx-3 mt-4">
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="text-danger">Cerrar Sesión</a>
    <% } %>
</div>