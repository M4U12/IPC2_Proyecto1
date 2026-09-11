<%@page import="java.util.List"%>
<%@page import="modelos.Chofer"%>
<%@page import="modelos.Enums"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Gestión de Choferes - Code 'n Buses</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold">Gestión de Choferes Locales</h2>
                        <p class="text-muted">Administra el personal operativo asignado a tu sucursal.</p>

                        <% if (session.getAttribute("mensajeExito") != null) {%>
                        <div class="alert alert-success"><%= session.getAttribute("mensajeExito")%></div>
                        <% session.removeAttribute("mensajeExito"); %>
                        <% } %>
                        <% if (session.getAttribute("error") != null) {%>
                        <div class="alert alert-danger"><%= session.getAttribute("error")%></div>
                        <% session.removeAttribute("error"); %>
                        <% } %>
                    </div>

                    <div class="row g-4">
                        <!-- Formulario de Registro -->
                        <div class="col-md-4">
                            <div class="p-4 bg-white rounded-4 shadow-sm">
                                <h5 class="fw-bold mb-3">Registrar Chofer</h5>
                                <form action="${pageContext.request.contextPath}/Gestionar_Choferes" method="POST" enctype="multipart/form-data">
                                    <input type="hidden" name="accion" value="crear">

                                    <div class="mb-3 text-center">
                                        <label class="form-label fw-bold d-block">Fotografía del Chofer</label>
                                        <input type="file" class="form-control form-control-sm" name="foto" accept="image/png, image/jpeg" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Nombre Completo</label>
                                        <input type="text" class="form-control" name="nombre" pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+" title="El nombre solo debe contener letras y espacios" onkeypress="soloLetras(event)" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Teléfono</label>
                                        <input type="text" class="form-control" name="telefono" maxlength="8" pattern="\d{8}" title="Debe contener exactamente 8 números" onkeypress="soloNumeros(event)" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">No. Licencia</label>
                                        <input type="text" class="form-control" name="num_licencia" maxlength="13" pattern="\d{13}" title="Debe contener exactamente 13 números" onkeypress="soloNumeros(event)" required>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Tipo</label>
                                            <select class="form-select" name="tipo_licencia" required>
                                                <option value="TIPO_A">Tipo A</option>
                                                <option value="TIPO_B">Tipo B</option>
                                                <option value="TIPO_C">Tipo C</option>
                                                <option value="TIPO_D">Tipo D</option>
                                                <option value="TIPO_E">Tipo E</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Vencimiento</label>
                                            <input type="date" class="form-control" name="fecha_vencimiento" required>
                                        </div>
                                    </div>
                                    <div class="mb-4">
                                        <label class="form-label">Salario Base por Viaje (Q)</label>
                                        <input type="number" step="0.01" min="0" class="form-control" name="salario_base" required>
                                    </div>

                                    <button type="submit" class="btn btn-primary w-100 fw-bold">Registrar Chofer</button>
                                </form>
                            </div>
                        </div>

                        <!-- Tabla de Choferes -->
                        <div class="col-md-8">
                            <div class="p-4 bg-white rounded-4 shadow-sm h-100">
                                <h5 class="fw-bold mb-3">Personal Activo</h5>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle border">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Foto</th>
                                                <th>Nombre</th>
                                                <th>Licencia</th>
                                                <th>Teléfono</th>
                                                <th>Salario</th>
                                                <th>Estado</th>
                                                <th>Acción</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<Chofer> listaChoferes = (List<Chofer>) request.getAttribute("listaChoferes");
                                                if (listaChoferes != null && !listaChoferes.isEmpty()) {
                                                    for (Chofer c : listaChoferes) { %>
                                            <tr>
                                                <td>
                                                    <% if (c.getFoto() == null || c.getFoto().isEmpty()) { %>
                                                    <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 45px; height: 45px;">
                                                        N/A
                                                    </div>
                                                    <% } else {%>
                                                    <img src="data:image/jpeg;base64,<%= c.getFoto()%>" 
                                                         alt="Foto" width="45" height="45" class="rounded-circle" style="object-fit: cover;">
                                                    <% }%>
                                                </td>
                                                <td class="fw-bold"><%= c.getNombre()%></td>
                                                <td>
                                                    <span class="fw-bold"><%= c.getNumLicencia()%></span> <br>
                                                    <span class="badge bg-dark mb-1"><%= c.getTipoLicencia()%></span><br>
                                                    <small class="text-muted fw-semibold">Vence: <%= c.getFechaVencimientoLicencia()%></small>
                                                </td>
                                                <td><%= c.getTelefono()%></td>
                                                <td>Q. <%= c.getSalarioBasePorViaje()%></td>
                                                <td>
                                                    <span class="badge <%= c.isEstado() ? "bg-success" : "bg-danger"%>">
                                                        <%= c.isEstado() ? "Disponible" : "De Baja"%>
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="d-flex gap-2">
                                                        <!-- boton para editar -->
                                                        <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEditar<%= c.getIdChofer()%>">
                                                            Editar
                                                        </button>

                                                        <!-- Formulario original de Estado -->
                                                        <form action="${pageContext.request.contextPath}/Gestionar_Choferes" method="POST" class="m-0">
                                                            <input type="hidden" name="accion" value="cambiarEstado">
                                                            <input type="hidden" name="id_chofer" value="<%= c.getIdChofer()%>">
                                                            <input type="hidden" name="nuevo_estado" value="<%= !c.isEstado()%>">
                                                            <button type="submit" class="btn btn-sm <%= c.isEstado() ? "btn-outline-danger" : "btn-outline-success"%>">
                                                                <%= c.isEstado() ? "Dar de baja" : "Reactivar"%>
                                                            </button>
                                                        </form>
                                                    </div>

                                                    <!-- modal para editar -->
                                                    <div class="modal fade" id="modalEditar<%= c.getIdChofer()%>" aria-hidden="true">
                                                        <div class="modal-dialog">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title fw-bold">Editar Datos: <%= c.getNombre()%></h5>
                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                                </div>
                                                                <form action="${pageContext.request.contextPath}/Gestionar_Choferes" method="POST" enctype="multipart/form-data">
                                                                    <div class="modal-body">
                                                                        <input type="hidden" name="accion" value="editar">
                                                                        <input type="hidden" name="id_chofer" value="<%= c.getIdChofer()%>">
                                                                        <input type="hidden" name="foto_actual" value="<%= c.getFoto()%>">

                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">Actualizar Fotografía (Opcional)</label>
                                                                            <input type="file" class="form-control form-control-sm" name="foto" accept="image/png, image/jpeg">
                                                                            <small class="text-muted">Si no seleccionas nada, se conservará la foto actual.</small>
                                                                        </div>
                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">Nombre Completo</label>
                                                                            <input type="text" class="form-control" name="nombre" value="<%= c.getNombre()%>" pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+" title="Solo letras y espacios permitidos" onkeypress="soloLetras(event)" required>
                                                                        </div>
                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">Teléfono</label>
                                                                            <input type="text" class="form-control" name="telefono" value="<%= c.getTelefono()%>" maxlength="8" pattern="\d{8}" title="Debe contener exactamente 8 números" onkeypress="soloNumeros(event)" required>
                                                                        </div>
                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">No. Licencia</label>
                                                                            <input type="text" class="form-control" name="num_licencia" value="<%= c.getNumLicencia()%>" maxlength="13" pattern="\d{13}" title="Debe contener exactamente 13 números" onkeypress="soloNumeros(event)" required>
                                                                        </div>
                                                                        <div class="row">
                                                                            <div class="col-md-6 mb-3">
                                                                                <label class="form-label text-start d-block">Tipo</label>
                                                                                <select class="form-select" name="tipo_licencia" required>
                                                                                    <option value="TIPO_A" <%= c.getTipoLicencia() == Enums.TipoLicencia.TIPO_A ? "selected" : ""%>>Tipo A</option>
                                                                                    <option value="TIPO_B" <%= c.getTipoLicencia() == Enums.TipoLicencia.TIPO_B ? "selected" : ""%>>Tipo B</option>
                                                                                    <option value="TIPO_C" <%= c.getTipoLicencia() == Enums.TipoLicencia.TIPO_C ? "selected" : ""%>>Tipo C</option>
                                                                                    <option value="TIPO_D" <%= c.getTipoLicencia() == Enums.TipoLicencia.TIPO_D ? "selected" : ""%>>Tipo D</option>
                                                                                    <option value="TIPO_E" <%= c.getTipoLicencia() == Enums.TipoLicencia.TIPO_E ? "selected" : ""%>>Tipo E</option>
                                                                                </select>
                                                                            </div>
                                                                            <div class="col-md-6 mb-3">
                                                                                <label class="form-label text-start d-block">Vencimiento</label>
                                                                                <input type="date" class="form-control" name="fecha_vencimiento" value="<%= c.getFechaVencimientoLicencia()%>" required>
                                                                            </div>
                                                                        </div>
                                                                        <div class="mb-3">
                                                                            <label class="form-label text-start d-block">Salario Base por Viaje (Q)</label>
                                                                            <input type="number" step="0.01" min="0" class="form-control" name="salario_base" value="<%= c.getSalarioBasePorViaje()%>" required>
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
                                                <td colspan="7" class="text-center text-muted py-4">No hay choferes registrados en tu sucursal.</td>
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
        </div>
    </body>
</html>