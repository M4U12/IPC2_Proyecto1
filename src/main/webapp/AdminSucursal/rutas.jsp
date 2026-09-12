<%@page import="java.util.List"%>
<%@page import="modelos.Ruta"%>
<%@page import="modelos.Sucursal"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Gestión de Rutas - Code 'n Buses</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold">Rutas de Viaje</h2>
                        <p class="text-muted">Define los trayectos comerciales que inician en tu sucursal.</p>

                        <% if (session.getAttribute("mensajeExito") != null) {%>
                        <div class="alert alert-success"><%= session.getAttribute("mensajeExito")%></div>
                        <% session.removeAttribute("mensajeExito"); %>
                        <% } %>
                        <% if (session.getAttribute("error") != null) {%>
                        <div class="alert alert-danger"><%= session.getAttribute("error")%></div>
                        <% session.removeAttribute("error"); %>
                        <% } %>

                        <%
                            List<Ruta> listaRutas = (List<Ruta>) request.getAttribute("listaRutas");
                            List<Sucursal> listaSucursales = (List<Sucursal>) request.getAttribute("listaSucursales");
                            int miSucursal = ((modelos.Usuario) session.getAttribute("usuarioLogueado")).getIdSucursalAsignada();
                        %>
                    </div>

                    <div class="row g-4">
                        <!-- Formulario -->
                        <div class="col-md-4">
                            <div class="p-4 bg-white rounded-4 shadow-sm">
                                <h5 class="fw-bold mb-3">Nueva Ruta</h5>
                                <form action="${pageContext.request.contextPath}/Gestionar_Rutas" method="POST">
                                    <input type="hidden" name="accion" value="crear">

                                    <div class="mb-3">
                                        <label class="form-label text-muted small fw-bold">Origen</label>
                                        <input type="text" class="form-control bg-light text-secondary" value="Mi Sucursal Actual" readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Destino</label>
                                        <select class="form-select" name="id_destino" required>
                                            <option value="">Selecciona una sucursal...</option>
                                            <% if (listaSucursales != null) {
                                                    for (Sucursal s : listaSucursales) {
                                                        if (s.getIdSucursal() != miSucursal) {%>
                                            <option value="<%= s.getIdSucursal()%>"><%= s.getNombre()%></option>
                                            <%      }
                                                    }
                                                } %>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Distancia Estimada (Km)</label>
                                        <input type="number" step="0.1" min="1" class="form-control" name="distancia_km" required>
                                    </div>
                                    <div class="mb-4">
                                        <label class="form-label">Precio del Boleto (Q)</label>
                                        <input type="number" step="0.01" min="1" class="form-control" name="precio" required>
                                    </div>

                                    <button type="submit" class="btn btn-primary w-100 fw-bold">Guardar Ruta</button>
                                </form>
                            </div>
                        </div>

                        <!-- Tabla -->
                        <div class="col-md-8">
                            <div class="p-4 bg-white rounded-4 shadow-sm h-100">
                                <h5 class="fw-bold mb-3">Catálogo de Trayectos</h5>
                                <table class="table table-hover align-middle border">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Destino</th>
                                            <th>Distancia</th>
                                            <th>Precio Boleto</th>
                                            <th>Acciones</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (listaRutas != null && !listaRutas.isEmpty()) {
                                                for (Ruta r : listaRutas) {
                                                    String nombreDestino = "Desconocido";
                                                    if (listaSucursales != null) {
                                                        for (Sucursal s : listaSucursales) {
                                                            if (s.getIdSucursal() == r.getIdDestino()) {
                                                                nombreDestino = s.getNombre();
                                                                break;
                                                            }
                                                        }
                                                    }
                                        %>
                                        <tr>
                                            <td> <%= nombreDestino%></td>
                                            <td><%= r.getDistanciaKm()%> Km</td>
                                            <td class="text-success fw-bold">Q. <%= r.getPrecio()%></td>
                                            <td>
                                                <div class="d-flex gap-2">
                                                    <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEditarRuta<%= r.getIdRuta()%>">Editar</button>

                                                    <form action="${pageContext.request.contextPath}/Gestionar_Rutas" method="POST" class="m-0" onsubmit="return confirm('¿Seguro que deseas eliminar esta ruta?');">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="id_ruta" value="<%= r.getIdRuta()%>">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger">Eliminar</button>
                                                    </form>
                                                </div>

                                                <!-- Edición -->
                                                <div class="modal fade" id="modalEditarRuta<%= r.getIdRuta()%>" tabindex="-1" aria-hidden="true">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title fw-bold">Editar Ruta hacia <%= nombreDestino%></h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <form action="${pageContext.request.contextPath}/Gestionar_Rutas" method="POST">
                                                                <div class="modal-body">
                                                                    <input type="hidden" name="accion" value="editar">
                                                                    <input type="hidden" name="id_ruta" value="<%= r.getIdRuta()%>">

                                                                    <div class="mb-3">
                                                                        <label class="form-label text-start d-block">Destino</label>
                                                                        <select class="form-select" name="id_destino" required>
                                                                            <% if (listaSucursales != null) {
                                                                                    for (Sucursal s : listaSucursales) {
                                                                                        if (s.getIdSucursal() != miSucursal) {%>
                                                                            <option value="<%= s.getIdSucursal()%>" <%= s.getIdSucursal() == r.getIdDestino() ? "selected" : ""%>><%= s.getNombre()%></option>
                                                                            <%      }
                                                                                    }
                                                                                }%>
                                                                        </select>
                                                                    </div>
                                                                    <div class="mb-3">
                                                                        <label class="form-label text-start d-block">Distancia (Km)</label>
                                                                        <input type="number" step="0.1" min="1" class="form-control" name="distancia_km" value="<%= r.getDistanciaKm()%>" required>
                                                                    </div>
                                                                    <div class="mb-3">
                                                                        <label class="form-label text-start d-block">Precio (Q)</label>
                                                                        <input type="number" step="0.01" min="1" class="form-control" name="precio" value="<%= r.getPrecio()%>" required>
                                                                    </div>
                                                                </div>
                                                                <div class="modal-footer bg-light">
                                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                                                    <button type="submit" class="btn btn-primary fw-bold">Guardar Cambios</button>
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
                                            <td colspan="5" class="text-center text-muted py-4">No hay rutas registradas.</td>
                                        </tr>
                                        <% }%>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>