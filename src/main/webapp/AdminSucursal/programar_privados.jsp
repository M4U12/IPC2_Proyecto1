<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.List"%>
<%@page import="modelos.ViajePrivado"%>
<%@page import="modelos.Bus"%>
<%@page import="modelos.Chofer"%>
<%@page import="modelos.Enums"%>
<%@page import="modelos.Sucursal"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Gestión de Privados</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">

                    <%
                        List<ViajePrivado> listaPrivados = (List<ViajePrivado>) request.getAttribute("listaPrivados");
                        List<Bus> listaBuses = (List<Bus>) request.getAttribute("listaBuses");
                        List<Chofer> listaChoferes = (List<Chofer>) request.getAttribute("listaChoferes");

                        // Extraemos la sucursal asignada para leer sus tarifas
                        Sucursal miSucursal = (Sucursal) request.getAttribute("miSucursal");

                        DateTimeFormatter formatoTabla = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
                        DateTimeFormatter formatoInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        String fechaActual = LocalDateTime.now().format(formatoInput);

                        String exitoMsg = (String) session.getAttribute("mensajeExito");
                        String errorMsg = (String) session.getAttribute("error");
                    %>

                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold text-primary"><i class="bi bi-star-fill me-2"></i> Viajes Privados y Cotizaciones</h2>
                        <p class="text-muted mb-0">Cotiza las solicitudes de los clientes y despacha las unidades alquiladas.</p>

                        <% if (exitoMsg != null) {%><div class="alert alert-success mt-3 mb-0"><%= exitoMsg%></div><% session.removeAttribute("mensajeExito");
                            } %>
                        <% if (errorMsg != null) {%><div class="alert alert-danger mt-3 mb-0"><%= errorMsg%></div><% session.removeAttribute("error");
                            }%>
                    </div>

                    <!-- Panel de Modificación de Tarifas de la Sucursal -->
                    <div class="card border-0 shadow-sm rounded-4 mb-4 bg-white">
                        <div class="card-body p-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
                            <div>
                                <h5 class="fw-bold text-primary mb-1"><i class="bi bi-sliders me-2"></i>Parámetros de Cotización</h5>
                                <p class="text-muted small mb-0">Define los precios base por hora y por pasajero que utiliza el sistema para calcular el costo estimado.</p>
                            </div>

                            <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST" class="d-flex gap-3 align-items-end">
                                <input type="hidden" name="accion" value="actualizar_tarifas">

                                <div>
                                    <label class="form-label fw-bold small text-muted mb-1">Tarifa Base por Hora</label>
                                    <div class="input-group input-group-sm" style="width: 170px;">
                                        <span class="input-group-text bg-light fw-bold text-dark">Q.</span>
                                        <input type="number" step="0.01" min="0" class="form-control fw-bold" name="tarifa_base" value="<%= miSucursal != null ? miSucursal.getTarifaBaseHora() : 0.0%>" required>
                                    </div>
                                </div>

                                <div>
                                    <label class="form-label fw-bold small text-muted mb-1">Tarifa por Pasajero</label>
                                    <div class="input-group input-group-sm" style="width: 170px;">
                                        <span class="input-group-text bg-light fw-bold text-dark">Q.</span>
                                        <input type="number" step="0.01" min="0" class="form-control fw-bold" name="tarifa_pasajero" value="<%= miSucursal != null ? miSucursal.getTarifaPasajero() : 0.0%>" required>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary btn-sm fw-bold px-3 py-2">
                                    <i class="bi bi-save me-1"></i> Guardar Tarifas
                                </button>
                            </form>
                        </div>
                    </div>

                    <div class="card border-0 shadow-sm rounded-4 p-4">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle border text-center">
                                <thead class="table-light">
                                    <tr>
                                        <th>Cliente</th>
                                        <th>Ruta Solicitada</th>
                                        <th>Salida Estimada</th>
                                        <th>Pasajeros</th>
                                        <th>Estado</th>
                                        <th>Acción Requerida</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (listaPrivados != null && !listaPrivados.isEmpty()) {
                                            for (ViajePrivado vp : listaPrivados) {
                                    %>
                                    <tr>
                                        <td class="fw-bold text-primary"><%= vp.getNombreCliente()%></td>
                                        <td>
                                            <span class="d-block small"><i class="bi bi-geo-alt-fill text-success"></i> <%= vp.getOrigen()%></span>
                                            <span class="d-block small"><i class="bi bi-flag-fill text-danger"></i> <%= vp.getDestino()%></span>
                                        </td>
                                        <td><%= vp.getFechaHoraSalidaEstimada().format(formatoTabla)%></td>
                                        <td><i class="bi bi-people-fill"></i> <%= vp.getCantidadPasajeros()%></td>
                                        <td>
                                            <%
                                                String badgeColor = "bg-secondary";
                                                if (vp.getEstado() == Enums.EstadoViaje.PENDIENTE)
                                                    badgeColor = "bg-warning text-dark";
                                                else if (vp.getEstado() == Enums.EstadoViaje.COTIZADA)
                                                    badgeColor = "bg-info text-dark";
                                                else if (vp.getEstado() == Enums.EstadoViaje.PAGADA)
                                                    badgeColor = "bg-primary";
                                                else if (vp.getEstado() == Enums.EstadoViaje.EN_CURSO)
                                                    badgeColor = "bg-dark";
                                                else if (vp.getEstado() == Enums.EstadoViaje.FINALIZADO)
                                                    badgeColor = "bg-success";
                                            %>
                                            <span class="badge <%= badgeColor%>"><%= vp.getEstado().name()%></span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2 justify-content-center">
                                                <% if (vp.getEstado() == Enums.EstadoViaje.PENDIENTE) {%>
                                                <button class="btn btn-sm btn-warning fw-bold" data-bs-toggle="modal" data-bs-target="#modalCotizar<%= vp.getIdViajePrivado()%>">Cotizar</button>
                                                <% } else if (vp.getEstado() == Enums.EstadoViaje.COTIZADA) { %>
                                                <span class="text-muted small fw-bold">Esperando Pago...</span>
                                                <% } else if (vp.getEstado() == Enums.EstadoViaje.PAGADA && vp.getIdBus() == null) {%>
                                                <button class="btn btn-sm btn-primary fw-bold" data-bs-toggle="modal" data-bs-target="#modalAsignar<%= vp.getIdViajePrivado()%>">Asignar Unidad</button>
                                                <% } else if (vp.getEstado() == Enums.EstadoViaje.PAGADA && vp.getIdBus() != null) {%>
                                                <button class="btn btn-sm btn-success fw-bold" data-bs-toggle="modal" data-bs-target="#modalIniciar<%= vp.getIdViajePrivado()%>"><i class="bi bi-play-fill"></i> Iniciar</button>
                                                <% } else if (vp.getEstado() == Enums.EstadoViaje.EN_CURSO) {%>
                                                <button class="btn btn-sm btn-dark fw-bold" data-bs-toggle="modal" data-bs-target="#modalFinalizar<%= vp.getIdViajePrivado()%>"><i class="bi bi-stop-fill"></i> Finalizar</button>
                                                <% } else { %>
                                                <i class="bi bi-check-circle-fill text-success fs-5"></i>
                                                <% } %>

                                                <% if (vp.getEstado() == Enums.EstadoViaje.PENDIENTE || vp.getEstado() == Enums.EstadoViaje.COTIZADA) {%>
                                                <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST" class="m-0" onsubmit="return confirm('¿Cancelar esta solicitud?');">
                                                    <input type="hidden" name="accion" value="cancelar">
                                                    <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">
                                                    <button type="submit" class="btn btn-sm btn-outline-warning" title="Cancelar Viaje"><i class="bi bi-x-octagon"></i></button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST" class="m-0" onsubmit="return confirm('¿Eliminar permanentemente?');">
                                                    <input type="hidden" name="accion" value="eliminar">
                                                    <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Eliminar"><i class="bi bi-trash"></i></button>
                                                </form>
                                                <% }%>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Modal Cotizar -->
                                <div class="modal fade text-start" id="modalCotizar<%= vp.getIdViajePrivado()%>" tabindex="-1">
                                    <div class="modal-dialog border-warning">
                                        <div class="modal-content">
                                            <div class="modal-header bg-warning">
                                                <h5 class="modal-title fw-bold">Cotizar Viaje de <%= vp.getNombreCliente()%></h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST">
                                                <div class="modal-body">
                                                    <input type="hidden" name="accion" value="cotizar">
                                                    <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">

                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Evaluación de Flota</label>
                                                        <%
                                                            boolean hayBusParaCotizar = false;
                                                            if (listaBuses != null) {
                                                                for (Bus b : listaBuses) {
                                                                    if (b.getCapacidad() >= vp.getCantidadPasajeros() && "DISPONIBLE".equals(String.valueOf(b.getEstadoOperativo()))) {
                                                                        hayBusParaCotizar = true;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            if (hayBusParaCotizar) {%>
                                                        <div class="alert alert-success py-2 small mb-0"><i class="bi bi-check-circle-fill"></i> Tienes unidades con capacidad para <%= vp.getCantidadPasajeros()%> pasajeros.</div>
                                                        <% } else {%>
                                                        <div class="alert alert-danger py-2 small mb-0"><i class="bi bi-x-circle-fill"></i> No tienes ningún bus disponible que soporte <%= vp.getCantidadPasajeros()%> pasajeros.</div>
                                                        <% }%>
                                                    </div>

                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Fecha/Hora Llegada Estimada (Destino)</label>
                                                        <input type="datetime-local" class="form-control" name="fecha_llegada" 
                                                               id="llegada_<%= vp.getIdViajePrivado()%>" 
                                                               min="<%= vp.getFechaHoraSalidaEstimada().format(formatoInput)%>" 
                                                               onchange="calcularCotizacionEnVivo(<%= vp.getIdViajePrivado()%>, '<%= vp.getFechaHoraSalidaEstimada().format(formatoInput)%>', <%= vp.getCantidadPasajeros()%>, <%= miSucursal != null ? miSucursal.getTarifaBaseHora() : 0%>, <%= miSucursal != null ? miSucursal.getTarifaPasajero() : 0%>)" 
                                                               required>
                                                    </div>

                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Precio Total a Cobrar (Q)</label>
                                                        <div class="input-group">
                                                            <span class="input-group-text fw-bold">Q.</span>
                                                            <input type="number" step="0.01" min="0" class="form-control fw-bold" name="precio" 
                                                                   id="precio_<%= vp.getIdViajePrivado()%>" 
                                                                   onkeypress="soloDecimales(event)" required>
                                                        </div>
                                                        <small class="text-muted"><i class="bi bi-magic"></i> El precio se calculará automáticamente al elegir la hora de llegada.</small>
                                                    </div>

                                                </div>
                                                <div class="modal-footer">
                                                    <button type="submit" class="btn btn-dark fw-bold" <% if (!hayBusParaCotizar) {
                                                            out.print("disabled");
                                                        } %>>Enviar Cotización</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>

                                <!-- Modal Asignar Unidad -->
                                <% if (vp.getEstado() == Enums.EstadoViaje.PAGADA && vp.getIdBus() == null) {%>
                                <div class="modal fade text-start" id="modalAsignar<%= vp.getIdViajePrivado()%>" tabindex="-1">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header bg-primary text-white">
                                                <h5 class="modal-title fw-bold">Asignar Recursos (Ya pagado)</h5>
                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST">
                                                <div class="modal-body">
                                                    <input type="hidden" name="accion" value="asignar">
                                                    <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">

                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Seleccionar Bus</label>
                                                        <%
                                                            boolean hayBusesAptos = false;
                                                            if (listaBuses != null) {
                                                                for (Bus b : listaBuses) {
                                                                    if (b.getCapacidad() >= vp.getCantidadPasajeros() && "DISPONIBLE".equals(String.valueOf(b.getEstadoOperativo()))) {
                                                                        hayBusesAptos = true;
                                                                        break;
                                                                    }
                                                                }
                                                            }

                                                            if (hayBusesAptos) { %>
                                                        <select class="form-select" name="id_bus" required>
                                                            <option value="" selected disabled>Selecciona bus apto...</option>
                                                            <% for (Bus b : listaBuses) {
                                                                    if (b.getCapacidad() >= vp.getCantidadPasajeros() && "DISPONIBLE".equals(String.valueOf(b.getEstadoOperativo()))) {%>
                                                            <option value="<%= b.getIdBus()%>">Placa: <%= b.getPlaca()%> (<%= b.getCapacidad()%> Asientos)</option>
                                                            <% }
                                                                } %>
                                                        </select>
                                                        <% } else {%>
                                                        <div class="alert alert-danger py-2 px-3 small fw-bold mb-0">
                                                            <i class="bi bi-x-circle-fill"></i> No hay buses con capacidad de <%= vp.getCantidadPasajeros()%> pasajeros.
                                                        </div>
                                                        <% } %>
                                                    </div>

                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Seleccionar Chofer</label>
                                                        <select class="form-select" name="id_chofer" required>
                                                            <option value="" selected disabled>Selecciona el chofer...</option>
                                                            <% if (listaChoferes != null) {
                                                                    for (Chofer c : listaChoferes) {
                                                                        if ("DISPONIBLE".equals(String.valueOf(c.getEstadoOperativo()))) {%>
                                                            <option value="<%= c.getIdChofer()%>"><%= c.getNombre()%></option>
                                                            <%      }
                                                                    }
                                                                } %>
                                                        </select>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="submit" class="btn btn-primary fw-bold" <% if (!hayBusesAptos) {
                                                            out.print("disabled");
                                                        } %>>Asignar y Preparar Salida</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <% } %>

                                <!-- Modal Iniciar -->
                                <% if (vp.getEstado() == Enums.EstadoViaje.PAGADA && vp.getIdBus() != null) {
                                        double kmActualBus = 0;
                                        if (listaBuses != null) {
                                            for (Bus b : listaBuses) {
                                                if (b.getIdBus() == vp.getIdBus()) {
                                                    kmActualBus = b.getKilometrajeActual();
                                                    break;
                                                }
                                            }
                                        }
                                %>
                                <div class="modal fade text-start" id="modalIniciar<%= vp.getIdViajePrivado()%>" tabindex="-1">
                                    <div class="modal-dialog">
                                        <div class="modal-content border-success">
                                            <div class="modal-header bg-success text-white">
                                                <h5 class="modal-title fw-bold">Iniciar Viaje Privado</h5>
                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST">
                                                <div class="modal-body">
                                                    <input type="hidden" name="accion" value="iniciar">
                                                    <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">
                                                    <input type="hidden" name="km_actual_bus" value="<%= kmActualBus%>">
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Kilometraje de Salida</label>
                                                        <input type="number" step="0.1" class="form-control border-success" name="kilometraje_salida" min="<%= kmActualBus%>" value="<%= kmActualBus%>" onkeypress="soloDecimales(event)" required>
                                                        <div class="form-text text-success"><i class="bi bi-info-circle"></i> Último registro en sistema: <%= kmActualBus%> km.</div>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Hora Real de Salida</label>
                                                        <input type="datetime-local" class="form-control border-success" name="fecha_hora_salida_real" value="<%= fechaActual%>" required>
                                                    </div>
                                                </div>
                                                <div class="modal-footer"><button type="submit" class="btn btn-success fw-bold">Confirmar Salida</button></div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <% } %>

                                <!-- Modal Finalizar -->
                                <% if (vp.getEstado() == Enums.EstadoViaje.EN_CURSO) {
                                        double kmSeguroSalida = (vp.getKilometrajeSalida() != null) ? vp.getKilometrajeSalida() : 0.0;
                                %>
                                <div class="modal fade text-start" id="modalFinalizar<%= vp.getIdViajePrivado()%>" tabindex="-1">
                                    <div class="modal-dialog">
                                        <div class="modal-content border-dark">
                                            <div class="modal-header bg-dark text-white">
                                                <h5 class="modal-title fw-bold">Finalizar Viaje Privado</h5>
                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/Gestionar_Privados" method="POST">
                                                <div class="modal-body">
                                                    <input type="hidden" name="accion" value="finalizar">
                                                    <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">

                                                    <input type="hidden" name="id_bus" value="<%= vp.getIdBus()%>">
                                                    <input type="hidden" name="id_chofer" value="<%= vp.getIdChofer()%>">
                                                    <input type="hidden" name="km_salida" value="<%= kmSeguroSalida%>">

                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Kilometraje Final</label>
                                                        <input type="number" step="0.1" min="<%= kmSeguroSalida%>" value="<%= kmSeguroSalida%>" class="form-control" name="kilometraje_llegada" required>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Gasto Combustible (Q)</label>
                                                        <input type="number" step="0.01" min="0" class="form-control" name="gasto_combustible" required>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Hora Real de Llegada</label>
                                                        <input type="datetime-local" class="form-control" name="fecha_hora_llegada_real" value="<%= fechaActual%>" required>
                                                    </div>
                                                </div>
                                                <div class="modal-footer"><button type="submit" class="btn btn-dark fw-bold">Registrar Llegada</button></div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <% } %>

                                <% }
                                } else { %>
                                <tr><td colspan="6" class="text-center py-4 text-muted">No hay solicitudes privadas en esta sucursal.</td></tr>
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