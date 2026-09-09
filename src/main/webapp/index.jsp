<%@page import="modelos.Usuario"%>
<%@page import="modelos.Enums"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Recuperar el usuario de la sesión, si existe
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Code 'n Buses - Inicio</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <!-- Barra Lateral (Sidebar) -->
                <jsp:include page="Componentes/sidebar.jsp" />

                <!-- Contenido Principal -->
                <div class="col-md-9 col-lg-10 p-5">

                    <!-- Encabezado Principal de la Empresa -->
                    <div class="p-5 mb-4 bg-white rounded-4 shadow-sm border-start border-primary border-5">
                        <div class="container-fluid py-2">
                            <h1 class="display-5 fw-bold text-dark">Bienvenido a Code 'n Buses</h1>
                            <p class="col-md-10 fs-5 text-muted mt-3">
                                Transformamos la manera en la que viajas. Somos la red de transporte líder, dedicada a ofrecerte seguridad, comodidad y puntualidad en cada kilómetro recorrido. 
                            </p>
                            <% if (usuario == null) { %>
                            <a href="LoginyRegistro/registro.jsp" class="btn btn-primary btn-lg rounded-pill px-4 mt-2 shadow-sm">Únete a nosotros</a>
                            <% } else { %>
                            <a href="PaginasUsuarios/viajes_regulares.jsp" class="btn btn-primary btn-lg rounded-pill px-4 mt-2 shadow-sm">Ver próximos viajes</a>
                            <% }%>
                        </div>
                    </div>

                    <!-- Tarjetas Informativas Redondeadas -->
                    <div class="row align-items-md-stretch g-4">
                        <div class="col-md-4">
                            <div class="h-100 p-4 bg-white rounded-4 shadow-sm text-center">
                                <h3 class="text-primary mb-3">Nuestra Misión</h3>
                                <p class="text-muted">Brindar un servicio de transporte terrestre de pasajeros que supere las expectativas, operando con una flota moderna, tecnología de punta y un equipo humano comprometido con tu seguridad.</p>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="h-100 p-4 bg-white rounded-4 shadow-sm text-center">
                                <h3 class="text-success mb-3">Cobertura</h3>
                                <p class="text-muted">Conectamos las principales terminales del país a través de sucursales estratégicamente ubicadas. Gestionamos rutas interdepartamentales garantizando eficiencia operativa.</p>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="h-100 p-4 bg-white rounded-4 shadow-sm text-center">
                                <h3 class="text-warning mb-3">Servicios Flexibles</h3>
                                <p class="text-muted">Ya sea que necesites un asiento en nuestras rutas regulares para tu día a día, o solicitar el alquiler de un viaje privado para eventos especiales, nos adaptamos a ti.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>