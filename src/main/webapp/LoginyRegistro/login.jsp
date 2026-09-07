<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Login</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="../js/validaciones.js"></script>
    </head>
    <body class="bg-light d-flex align-items-center vh-100">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-5">
                    <div class="card shadow">
                        <div class="card-body p-5">
                            <h3 class="text-center mb-4">Inicio de Sesión</h3>

                            <!-- muestra errores enviados desde el servlet -->
                            <% if (request.getAttribute("error") != null) {%>
                            <div class="alert alert-danger" role="alert">
                                <%= request.getAttribute("error")%>
                            </div>
                            <% }%>

                            <form action="../LoginServlet" method="POST">
                                <div class="mb-3">
                                    <label class="form-label">DPI</label>
                                    <input type="text" class="form-control" name="dpi" pattern="\d{13}" title="Debe contener exactamente 13 números enteros" maxlength="13" onkeypress="soloNumeros(event)" required>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label">Contraseña</label>
                                    <input type="password" class="form-control" name="password" required>
                                </div>

                                <button type="submit" class="btn btn-primary w-100">Ingresar</button>
                                <div class="mt-3 text-center">
                                    <p>¿No tienes cuenta? <a href="registro.jsp" class="text-decoration-none">Regístrate aquí</a></p>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>