<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Registro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="../js/validaciones.js"></script>
    </head>
    <body class="bg-light d-flex align-items-center py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card shadow">
                        <div class="card-body p-5">
                            <h3 class="text-center mb-4">Crear Cuenta de Cliente</h3>
                            <!-- muestra errores enviados desde el servlet -->
                            <% if (request.getAttribute("error") != null) {%>
                            <div class="alert alert-danger" role="alert">
                                <%= request.getAttribute("error")%>
                            </div>
                            <% }%>

                            <form action="../UsuarioServlet" method="POST">
                                <input type="hidden" name="accion" value="crearCliente">

                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">DPI</label>
                                        <input type="text" class="form-control" name="dpi" pattern="\d{13}" title="Debe contener exactamente 13 números enteros" maxlength="13" onkeypress="soloNumeros(event)" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">NIT</label>
                                        <input type="text" class="form-control" name="nit" pattern="\d{13}" title="Debe contener exactamente 13 números enteros" maxlength="13" onkeypress="soloNumeros(event)" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Nombre Completo</label>
                                    <input type="text" class="form-control" name="nombre" pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+" title="El nombre solo debe contener letras y espacios" onkeypress="soloLetras(event)" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Teléfono</label>
                                    <input type="text" class="form-control" name="telefono" pattern="\d{8}" title="Debe contener exactamente 8 números enteros" maxlength="8" onkeypress="soloNumeros(event)" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Dirección</label>
                                    <input type="text" class="form-control" name="direccion">
                                </div>

                                <div class="mb-4">
                                    <label class="form-label">Contraseña</label>
                                    <input type="password" class="form-control" name="password" required>
                                </div>

                                <button type="submit" class="btn btn-success w-100">Registrarme</button>
                                <div class="mt-3 text-center">
                                    <a href="login.jsp" class="text-decoration-none">Volver</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>