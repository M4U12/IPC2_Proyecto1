<%@page import="java.util.List"%>
<%@page import="modelos.Bus"%>
<%@page import="modelos.Chofer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Flota - Code 'n Buses</title>
    <jsp:include page="/Componentes/recursos.jsp" />
</head>
<body>
    <div class="container-fluid p-0">
        <div class="row g-0">
            <jsp:include page="/Componentes/sidebar.jsp" />
            
            <div class="col-md-9 col-lg-10 p-5">
                <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                    <h2 class="fw-bold">Gestión de Flota Local</h2>
                    <p class="text-muted">Administra los autobuses asignados a tu sucursal.</p>
                    
                    <% if (session.getAttribute("mensajeExito") != null) { %>
                        <div class="alert alert-success"><%= session.getAttribute("mensajeExito") %></div>
                        <% session.removeAttribute("mensajeExito"); %>
                    <% } %>
                    <% if (session.getAttribute("error") != null) { %>
                        <div class="alert alert-danger"><%= session.getAttribute("error") %></div>
                        <% session.removeAttribute("error"); %>
                    <% } %>
                    
                    <% 
                    List<Bus> listaBuses = (List<Bus>) request.getAttribute("listaBuses");
                    List<Chofer> listaChoferes = (List<Chofer>) request.getAttribute("listaChoferes");
                    %>
                </div>

                <div class="row g-4">
                    <!-- Registro -->
                    <div class="col-md-4">
                        <div class="p-4 bg-white rounded-4 shadow-sm">
                            <h5 class="fw-bold mb-3">Registrar Unidad</h5>
                            <form action="${pageContext.request.contextPath}/Gestionar_Buses" method="POST" enctype="multipart/form-data">
                                <input type="hidden" name="accion" value="crear">
                                
                                <div class="mb-3 text-center">
                                    <label class="form-label fw-bold d-block">Fotografía del Bus</label>
                                    <input type="file" class="form-control form-control-sm" name="foto" accept="image/png, image/jpeg" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Placa</label>
                                    <input type="text" class="form-control text-uppercase" name="placa" maxlength="7" pattern="[Cc][0-9]{3}[A-Za-z]{3}" title="Debe iniciar con C, seguido de 3 números y 3 letras (Ej. C123ABC)" required>
                                </div>
                                <div class="row">
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Marca</label>
                                        <input type="text" class="form-control" name="marca" required>
                                    </div>
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Modelo</label>
                                        <input type="text" class="form-control" name="modelo" required>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Año</label>
                                        <input type="number" class="form-control" name="anio_fabricacion" min="1990" max="2030" required>
                                    </div>
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Capacidad (Asientos)</label>
                                        <input type="number" class="form-control" name="capacidad" min="10" required>
                                    </div>
                                </div>
                                
                                <button type="submit" class="btn btn-primary w-100 fw-bold">Registrar Bus</button>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Tabla de Buses -->
                    <div class="col-md-8">
                        <div class="p-4 bg-white rounded-4 shadow-sm h-100">
                            <h5 class="fw-bold mb-3">Flota Activa</h5>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle border">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Vehículo</th>
                                            <th>Detalles</th>
                                            <th>Asientos</th>
                                            <th>Estado</th>
                                            <th>Kilometraje</th>
                                            <th>Acción</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (listaBuses != null && !listaBuses.isEmpty()) {
                                            for (Bus b : listaBuses) { %>
                                            <tr>
                                                <td>
                                                    <% if (b.getFoto() == null || b.getFoto().isEmpty()) { %>
                                                        <div class="bg-secondary text-white rounded d-flex align-items-center justify-content-center" style="width: 80px; height: 50px;">N/A</div>
                                                    <% } else { %>
                                                        <img src="data:image/jpeg;base64,<%= b.getFoto() %>" alt="Bus" width="80" height="50" class="rounded" style="object-fit: cover;">
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <span class="fw-bold text-uppercase"><%= b.getPlaca() %></span> <br>
                                                    <small class="text-muted"><%= b.getMarca() %> <%= b.getModelo() %> (<%= b.getAnioFabricacion() %>)</small>
                                                </td>
                                                <td class="text-center fw-semibold"><%= b.getCapacidad() %></td>
                                                
                                                <td>
                                                    <span class="badge <%= b.isEstado() ? "bg-success" : "bg-danger" %> mb-1 d-block"><%= b.isEstado() ? "Activo" : "De Baja" %></span>
                                                    <small class="fw-bold text-secondary"><%= b.getEstadoOperativo() %></small>
                                                </td>
                                                <td class="text muted"><%= b.getKilometrajeActual()%></td>
                                                <td>
                                                    <div class="d-flex gap-2">
                                                        <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEditarBus<%= b.getIdBus() %>">
                                                            Editar
                                                        </button>
                                                        <form action="${pageContext.request.contextPath}/Gestionar_Buses" method="POST" class="m-0">
                                                            <input type="hidden" name="accion" value="cambiarEstado">
                                                            <input type="hidden" name="id_bus" value="<%= b.getIdBus() %>">
                                                            <input type="hidden" name="nuevo_estado" value="<%= !b.isEstado() %>">
                                                            <button type="submit" class="btn btn-sm <%= b.isEstado() ? "btn-outline-danger" : "btn-outline-success" %>">
                                                                <%= b.isEstado() ? "Dar de baja" : "Reactivar" %>
                                                            </button>
                                                        </form>
                                                    </div>

                                                    <!-- Edición del Bus -->
                                                    <div class="modal fade" id="modalEditarBus<%= b.getIdBus() %>" tabindex="-1" aria-hidden="true">
                                                        <div class="modal-dialog">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title fw-bold">Editar Bus: <%= b.getPlaca() %></h5>
                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                                </div>
                                                                <form action="${pageContext.request.contextPath}/Gestionar_Buses" method="POST" enctype="multipart/form-data">
                                                                    <div class="modal-body">
                                                                        <input type="hidden" name="accion" value="editar">
                                                                        <input type="hidden" name="id_bus" value="<%= b.getIdBus() %>">
                                                                        <input type="hidden" name="foto_actual" value="<%= b.getFoto() %>">
                                                                        <input type="hidden" name="kilometraje_actual" value="<%= b.getKilometrajeActual() %>">
                                                                        <input type="hidden" name="estado_actual" value="<%= b.isEstado() %>">
                                                                        
                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">Actualizar Fotografía (Opcional)</label>
                                                                            <input type="file" class="form-control form-control-sm" name="foto" accept="image/png, image/jpeg">
                                                                        </div>
                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">Placa</label>
                                                                            <input type="text" class="form-control text-uppercase" name="placa" value="<%= b.getPlaca() %>" maxlength="7" pattern="[a-zA-Z0-9]+" required>
                                                                        </div>
                                                                        <div class="row">
                                                                            <div class="col-6 mb-3">
                                                                                <label class="form-label text-start d-block">Marca</label>
                                                                                <input type="text" class="form-control" name="marca" value="<%= b.getMarca() %>" required>
                                                                            </div>
                                                                            <div class="col-6 mb-3">
                                                                                <label class="form-label text-start d-block">Modelo</label>
                                                                                <input type="text" class="form-control" name="modelo" value="<%= b.getModelo() %>" required>
                                                                            </div>
                                                                        </div>
                                                                        <div class="row">
                                                                            <div class="col-6 mb-3">
                                                                                <label class="form-label text-start d-block">Año</label>
                                                                                <input type="number" class="form-control" name="anio_fabricacion" value="<%= b.getAnioFabricacion() %>" min="1990" max=""2026 required>
                                                                            </div>
                                                                            <div class="col-6 mb-3">
                                                                                <label class="form-label text-start d-block">Capacidad</label>
                                                                                <input type="number" class="form-control" name="capacidad" value="<%= b.getCapacidad() %>" min="10" required>
                                                                            </div>
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
                                                <td colspan="6" class="text-center text-muted py-4">No hay autobuses registrados en tu sucursal.</td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>