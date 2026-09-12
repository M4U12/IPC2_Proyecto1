<%@page import="java.util.List"%>
<%@page import="modelos.Usuario"%>
<%@page import="modelos.Sucursal"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Gestión de Administradores</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">

                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-5 bg-white rounded-4 shadow-sm">
                        <h2 class="fw-bold mb-4">Gestión de Personal Administrativo</h2>

                        <% if (session.getAttribute("mensajeExito") != null) {%>
                        <div class="alert alert-success"><%= session.getAttribute("mensajeExito")%></div>
                        <% session.removeAttribute("mensajeExito"); %>
                        <% } %>
                        <% if (session.getAttribute("error") != null) {%>
                        <div class="alert alert-danger"><%= session.getAttribute("error")%></div>
                        <% session.removeAttribute("error"); %>
                        <% } %>

                        <%
                            List<Usuario> listaAdmins = (List<Usuario>) request.getAttribute("listaAdmins");
                            List<Sucursal> listaSucursales = (List<Sucursal>) request.getAttribute("listaSucursales");
                        %>

                        <div class="table-responsive mt-4">
                            <table class="table table-hover align-middle border">
                                <thead class="table-light">
                                    <tr>
                                        <th>Nombre</th>
                                        <th>DPI / NIT</th>
                                        <th>Contacto</th>
                                        <th>Dirección</th>
                                        <th>Sucursal Actual</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (listaAdmins != null && !listaAdmins.isEmpty()) {
                                            for (Usuario admin : listaAdmins) {%>
                                    <tr>
                                        <td><%= admin.getNombre()%></td>
                                        <td>
                                            <span class="d-block"><%= admin.getDpi()%> (DPI)</span>
                                            <small class="text-muted"><%= admin.getNit() != null ? admin.getNit() : "N/A"%> (NIT)</small>
                                        </td>
                                        <td>  <%= admin.getTelefono()%></td>
                                        <td style="max-width: 150px;">
                                            <span class="d-inline-block text-truncate w-100" title="<%= admin.getDireccion() != null ? admin.getDireccion() : ""%>">
                                                <%= admin.getDireccion()%>
                                            </span>
                                        </td>
                                        <td>
                                            <!-- para escribir el nombre de la sucursal -->
                                            <span class="badge <%= admin.getIdSucursalAsignada() == 0 ? "bg-warning text-dark" : "bg-secondary"%>">
                                                <%= admin.getNombreSucursalAsignada()%>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge <%= admin.isEstado() ? "bg-success" : "bg-danger"%>">
                                                <%= admin.isEstado() ? "Activo" : "Inactivo"%>
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2 mb-2">
                                                <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEditarAdmin<%= admin.getIdUsuario()%>">
                                                    Editar
                                                </button>

                                                <!-- Activar / Desactivar -->
                                                <form action="${pageContext.request.contextPath}/Gestionar_Admin_Sucursal" method="POST" class="m-0">
                                                    <input type="hidden" name="accion" value="cambiarEstado">
                                                    <input type="hidden" name="id_usuario" value="<%= admin.getIdUsuario()%>">
                                                    <input type="hidden" name="id_sucursal_actual" value="<%= admin.getIdSucursalAsignada()%>">
                                                    <input type="hidden" name="nuevo_estado" value="<%= !admin.isEstado()%>">
                                                    <button type="submit" class="btn btn-sm <%= admin.isEstado() ? "btn-outline-danger" : "btn-outline-success"%>" style="width: 90px;">
                                                        <%= admin.isEstado() ? "Desactivar" : "Activar"%>
                                                    </button>
                                                </form>
                                            </div>

                                            <!-- Reasignar Sucursal -->
                                            <form action="${pageContext.request.contextPath}/Gestionar_Admin_Sucursal" method="POST" class="d-flex gap-1 m-0">
                                                <input type="hidden" name="accion" value="reasignar">
                                                <input type="hidden" name="id_usuario" value="<%= admin.getIdUsuario()%>">
                                                <input type="hidden" name="id_sucursal_actual" value="<%= admin.getIdSucursalAsignada()%>">

                                                <select name="id_nueva_sucursal" class="form-select form-select-sm" style="width: 150px;" required <%= !admin.isEstado() ? "disabled" : ""%>>
                                                    <option value="">Mover a...</option>
                                                    <% if (listaSucursales != null) {
                                                            for (Sucursal s : listaSucursales) {
                                                                if (s.getIdSucursal() != admin.getIdSucursalAsignada()) {%>
                                                    <option value="<%= s.getIdSucursal()%>"><%= s.getNombre()%></option>
                                                    <%      }
                                                            }
                                                        }%>
                                                </select>
                                                <button type="submit" class="btn btn-sm btn-primary">Mover</button>
                                            </form>

                                            <!-- Edición Administrador -->
                                            <div class="modal fade" id="modalEditarAdmin<%= admin.getIdUsuario()%>" tabindex="-1" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title fw-bold">Editar Admin: <%= admin.getNombre()%></h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                        </div>
                                                        <form action="${pageContext.request.contextPath}/Gestionar_Admin_Sucursal" method="POST">
                                                            <div class="modal-body">
                                                                <input type="hidden" name="accion" value="editar">
                                                                <input type="hidden" name="id_usuario" value="<%= admin.getIdUsuario()%>">
                                                                <input type="hidden" name="dpi_actual" value="<%= admin.getDpi()%>">
                                                                <input type="hidden" name="nit_actual" value="<%= admin.getNit()%>">
                                                                <input type="hidden" name="telefono_actual" value="<%= admin.getTelefono()%>">

                                                                <div class="mb-3">
                                                                    <label class="form-label text-start d-block">Nombre Completo</label>
                                                                    <input type="text" class="form-control" name="nombre" value="<%= admin.getNombre()%>" pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+" title="Solo letras y espacios permitidos" onkeypress="soloLetras(event)" required>
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label class="form-label text-start d-block">DPI</label>
                                                                    <input type="text" class="form-control" name="dpi" value="<%= admin.getDpi()%>" maxlength="13" pattern="\d{13}" title="Debe contener 13 números" onkeypress="soloNumeros(event)" required>
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label class="form-label text-start d-block">NIT</label>
                                                                    <input type="text" class="form-control" name="nit" value="<%= admin.getNit()%>" maxlength="13" pattern="\d{13}" title="Debe contener 13 números" onkeypress="soloNumeros(event)" required>
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label class="form-label text-start d-block">Teléfono</label>
                                                                    <input type="text" class="form-control" name="telefono" value="<%= admin.getTelefono()%>" maxlength="8" pattern="\d{8}" title="Debe contener 8 números" onkeypress="soloNumeros(event)" required>
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label class="form-label text-start d-block">Dirección Exacta</label>
                                                                    <textarea class="form-control" name="direccion" rows="2" required><%= admin.getDireccion()%></textarea>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer bg-light">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                                                <button type="submit" class="btn btn-primary fw-bold">Guardar</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                    <%  }
                                    } else { %>
                                    <tr>
                                        <td colspan="6" class="text-center text-muted py-4">No hay personal administrativo registrado en el sistema.</td>
                                    </tr>
                                    <% }%>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>