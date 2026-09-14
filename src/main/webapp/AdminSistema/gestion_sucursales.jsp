<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="modelos.Sucursal"%>
<%@page import="dao.SucursalDAO"%>
<%@page import="dao.AdminSucursalDAO"%>
<%@page import="modelos.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Sucursales</title>
        <jsp:include page="/Componentes/recursos.jsp" />
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">

                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-5 bg-white rounded-4 shadow-sm">
                        <h2 class="fw-bold mb-4">Gestión de Sucursales</h2>

                        <% if (session.getAttribute("mensajeExito") != null) {%>
                        <div class="alert alert-success"><%= session.getAttribute("mensajeExito")%></div>
                        <% session.removeAttribute("mensajeExito"); %>
                        <% } %>
                        <% if (session.getAttribute("error") != null) {%>
                        <div class="alert alert-danger"><%= session.getAttribute("error")%></div>
                        <% session.removeAttribute("error"); %>
                        <% } %>

                        <%
                            List<Usuario> adminsDisponibles = (List<Usuario>) request.getAttribute("adminsDisponibles");
                            List<Sucursal> listaSucursales = (List<Sucursal>) request.getAttribute("listaSucursales");

                            if (adminsDisponibles == null) {
                                adminsDisponibles = new ArrayList<>();
                            }
                            if (listaSucursales == null) {
                                listaSucursales = new ArrayList<>();
                            }

                            boolean hayAdmins = !adminsDisponibles.isEmpty();
                        %>

                        <div class="row">
                            <div class="col-md-5">
                                <h5 class="text-muted mb-3">Registrar Nueva Sucursal</h5>

                                <% if (!hayAdmins) { %>
                                <div class="alert alert-warning small">
                                    <strong>Atención:</strong> No hay administradores de sucursal disponibles. Debe crear uno antes.
                                </div>
                                <% }%>

                                <form action="${pageContext.request.contextPath}/Gestionar_Sucursales" method="POST">
                                    <input type="hidden" name="accion" value="crear">
                                    <input type="hidden" id="lat-input" name="latitud" value="14.83472" required>
                                    <input type="hidden" id="lng-input" name="longitud" value="-91.51805" required>

                                    <div class="mb-3">
                                        <label class="form-label fw-bold small text-muted">Nombre de la Sucursal</label>
                                        <input type="text" class="form-control" name="nombre" <%= !hayAdmins ? "disabled" : ""%> required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-bold small text-muted">Teléfono</label>
                                        <input type="text" class="form-control" name="telefono" maxlength="8" pattern="\d{8}" title="Debe contener exactamente 8 números enteros" onkeypress="soloNumeros(event)" <%= !hayAdmins ? "disabled" : ""%> required>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label fw-bold small text-muted">Ubicación en el Mapa</label>
                                        <div id="mapa-sucursal" class="border rounded shadow-sm mb-2" style="height: 250px; z-index: 1;"></div>
                                        <small class="text-primary fw-bold"><i class="bi bi-info-circle"></i> Haz clic en el mapa para establecer la dirección.</small>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label fw-bold small text-muted">Dirección Exacta</label>
                                        <textarea class="form-control bg-light" id="direccion-input" name="direccion" rows="2" readonly <%= !hayAdmins ? "disabled" : ""%> required></textarea>
                                    </div>
                                    <div class="mb-4">
                                        <label class="form-label text-primary fw-bold">Asignar Administrador</label>
                                        <select class="form-select border-primary" name="id_admin" <%= !hayAdmins ? "disabled" : ""%> required>
                                            <% if (!hayAdmins) { %>
                                            <option value="">No hay administradores disponibles</option>
                                            <% } else {
                                                for (Usuario a : adminsDisponibles) {%>
                                            <option value="<%= a.getIdUsuario()%>"><%= a.getNombre()%> (DPI: <%= a.getDpi()%>)</option>
                                            <%  }
                                                }%>
                                        </select>
                                    </div>
                                    <button type="submit" class="btn btn-primary w-100 fw-bold" <%= !hayAdmins ? "disabled" : ""%>>Guardar y Asignar</button>
                                </form>
                            </div>

                            <div class="col-md-7">
                                <h5 class="text-muted mb-3">Sucursales Activas</h5>
                                <table class="table table-hover border">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Nombre</th>
                                            <th>Teléfono</th>
                                            <th>Dirección</th>
                                            <th>Acción</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (listaSucursales.isEmpty()) { %>
                                        <tr>
                                            <td colspan="4" class="text-center text-muted py-4">Aún no hay sucursales registradas.</td>
                                        </tr>
                                        <% } else {
                                            for (Sucursal s : listaSucursales) {%>
                                        <tr>
                                            <td class="fw-bold text-primary"><%= s.getNombre()%></td>
                                            <td><%= s.getTelefono()%></td>
                                            <td class="small"><%= s.getDireccion()%></td>
                                            <td><button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEditarSucursal<%= s.getIdSucursal()%>"><i class="bi bi-pencil-square"></i> Editar</button></td>

                                            <!--Edición Sucursal -->
                                    <div class="modal fade" id="modalEditarSucursal<%= s.getIdSucursal()%>" tabindex="-1" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title fw-bold">Editar Sucursal</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/Gestionar_Sucursales" method="POST">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="accion" value="editar">
                                                        <input type="hidden" name="id_sucursal" value="<%= s.getIdSucursal()%>">
                                                        <input type="hidden" name="telefono_actual" value="<%= s.getTelefono()%>">
                                                        <input type="hidden" class="lat-editar-input" name="latitud" value="<%= s.getLatitud()%>" required>
                                                        <input type="hidden" class="lng-editar-input" name="longitud" value="<%= s.getLongitud()%>" required>

                                                        <div class="mb-3">
                                                            <label class="form-label text-start d-block fw-bold small text-muted">Nombre</label>
                                                            <input type="text" class="form-control" name="nombre" value="<%= s.getNombre()%>" required>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label class="form-label text-start d-block fw-bold small text-muted">Teléfono</label>
                                                            <input type="text" class="form-control" name="telefono" value="<%= s.getTelefono()%>" maxlength="8" pattern="\d{8}" title="Debe contener 8 números" onkeypress="soloNumeros(event)" required>
                                                        </div>

                                                        <!-- Contenedor del Mapa en el Modal de Edición -->
                                                        <div class="mb-3">
                                                            <label class="form-label fw-bold small text-muted">Actualizar Ubicación (Opcional)</label>
                                                            <div class="mapa-editar-sucursal border rounded shadow-sm mb-2" style="height: 200px; z-index: 1;" data-lat="<%= s.getLatitud()%>" data-lng="<%= s.getLongitud()%>"></div>
                                                            <small class="text-primary fw-bold"><i class="bi bi-info-circle"></i> Haz clic en el mapa si necesitas cambiar la dirección.</small>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label class="form-label text-start d-block fw-bold small text-muted">Dirección Exacta</label>
                                                            <textarea class="form-control bg-light direccion-editar-input" name="direccion" rows="2" readonly required><%= s.getDireccion()%></textarea>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer bg-light">
                                                        <button type="button" class="btn btn-secondary fw-bold" data-bs-dismiss="modal">Cancelar</button>
                                                        <button type="submit" class="btn btn-primary fw-bold">Guardar</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    </td>
                                    </tr>
                                    <%  }
                                        }%>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
        <script src="${pageContext.request.contextPath}/js/mapa.js"></script>
    </body>
</html>