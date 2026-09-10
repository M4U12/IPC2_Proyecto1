<%@page import="java.util.List"%>
<%@page import="modelos.Usuario"%>
<%@page import="modelos.Sucursal"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Gestión de Administradores - Code 'n Buses</title>
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
                                        <th>ID</th>
                                        <th>Nombre</th>
                                        <th>DPI</th>
                                        <th>Sucursal Actual</th>
                                        <th>Estado</th>
                                        <th>Acciones Rápidas</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (listaAdmins != null && !listaAdmins.isEmpty()) {
                                        for (Usuario admin : listaAdmins) {%>
                                    <tr>
                                        <td><%= admin.getIdUsuario()%></td>
                                        <td><%= admin.getNombre()%></td>
                                        <td><%= admin.getDpi()%></td>
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
                                            <div class="d-flex gap-3">

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

                                                <!-- Reasignar Sucursal -->
                                                <form action="${pageContext.request.contextPath}/Gestionar_Admin_Sucursal" method="POST" class="d-flex gap-1 m-0">
                                                    <input type="hidden" name="accion" value="reasignar">
                                                    <input type="hidden" name="id_usuario" value="<%= admin.getIdUsuario()%>">
                                                    <input type="hidden" name="id_sucursal_actual" value="<%= admin.getIdSucursalAsignada()%>">

                                                    <select name="id_nueva_sucursal" class="form-select form-select-sm" style="width: 150px;" required>
                                                        <option value="">Mover a...</option>
                                                        <% if (listaSucursales != null) {
                                                                for (Sucursal s : listaSucursales) {
                                                                    if (s.getIdSucursal() != admin.getIdSucursalAsignada()) {%>
                                                        <option value="<%= s.getIdSucursal()%>"><%= s.getNombre()%></option>
                                                        <%      }
                                                                }
                                                            } %>
                                                    </select>
                                                    <button type="submit" class="btn btn-sm btn-primary">Mover</button>
                                                </form>

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