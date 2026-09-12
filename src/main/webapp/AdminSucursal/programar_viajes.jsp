<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="modelos.Viaje"%>
<%@page import="modelos.Ruta"%>
<%@page import="modelos.Bus"%>
<%@page import="modelos.Chofer"%>
<%@page import="modelos.Sucursal"%>
<%@page import="modelos.Enums"%>
<%@page import="modelos.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Programación de Viajes - Code 'n Buses</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body>
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5">

                    <%
                        List<Viaje> listaViajes = (List<Viaje>) request.getAttribute("listaViajes");
                        List<Ruta> listaRutas = (List<Ruta>) request.getAttribute("listaRutas");
                        List<Bus> listaBuses = (List<Bus>) request.getAttribute("listaBuses");
                        List<Chofer> listaChoferes = (List<Chofer>) request.getAttribute("listaChoferes");
                        List<Sucursal> listaSucursales = (List<Sucursal>) request.getAttribute("listaSucursales");

                        DateTimeFormatter formatoFechaTabla = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        DateTimeFormatter formatoFechaInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                        // nombre de la sucursal de origen
                        int miSucursalId = ((Usuario) session.getAttribute("usuarioLogueado")).getIdSucursalAsignada();
                        String nombreOrigen = "Mi Sucursal";

                        if (listaSucursales != null) {
                            for (Sucursal s : listaSucursales) {
                                if (s.getIdSucursal() == miSucursalId) {
                                    nombreOrigen = s.getNombre();
                                    break;
                                }
                            }
                        }

                        // para la disponibilidad
                        List<Integer> busesOcupados = new ArrayList<>();
                        List<Integer> choferesOcupados = new ArrayList<>();
                        if (listaViajes != null) {
                            for (Viaje v : listaViajes) {
                                if (v.getEstadoViaje() == Enums.EstadoViaje.PROGRAMADO || v.getEstadoViaje() == Enums.EstadoViaje.EN_CURSO) {
                                    busesOcupados.add(v.getIdBus());
                                    choferesOcupados.add(v.getIdChofer());
                                }
                            }
                        }
                    %>

                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4">
                        <h2 class="fw-bold text-primary">
                            <i class="bi bi-building me-2"></i> 
                            <%= nombreOrigen%> <span class="text-muted fs-4">(Sucursal #<%= miSucursalId%>)</span>
                        </h2>
                        <p class="text-muted mb-0">Programa salidas, asigna unidades.</p>

                        <% if (session.getAttribute("mensajeExito") != null) {%>
                        <div class="alert alert-success mt-3 mb-0"><%= session.getAttribute("mensajeExito")%></div>
                        <% session.removeAttribute("mensajeExito"); %>
                        <% } %>
                        <% if (session.getAttribute("error") != null) {%>
                        <div class="alert alert-danger mt-3 mb-0"><%= session.getAttribute("error")%></div>
                        <% session.removeAttribute("error"); %>
                        <% } %>
                    </div>

                    <div class="row g-4">
                        <!-- Formulario izquierda -->
                        <div class="col-md-4">
                            <div class="p-4 bg-white rounded-4 shadow-sm">
                                <h5 class="fw-bold mb-3">Programar Salida</h5>
                                <form action="${pageContext.request.contextPath}/Gestionar_Viajes" method="POST">
                                    <input type="hidden" name="accion" value="programar">

                                    <div class="mb-3">
                                        <label class="form-label text-muted small fw-bold">Ruta Comercial</label>
                                        <select class="form-select" name="id_ruta" required>
                                            <option value="">Selecciona la ruta...</option>
                                            <% if (listaRutas != null) {
                                                    for (Ruta r : listaRutas) {
                                                        String dest = "Desconocido";
                                                        if (listaSucursales != null) {
                                                            for (Sucursal s : listaSucursales) {
                                                                if (s.getIdSucursal() == r.getIdDestino()) {
                                                                    dest = s.getNombre();
                                                                    break;
                                                                }
                                                            }
                                                        }
                                            %>
                                            <option value="<%= r.getIdRuta()%>"><%= nombreOrigen%> ➔ <%= dest%> (Q.<%= r.getPrecio()%>)</option>
                                            <% }
                                                } %>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label text-muted small fw-bold">Bus Asignado</label>
                                        <select class="form-select" name="id_bus" required>
                                            <option value="">Selecciona el bus...</option>
                                            <% if (listaBuses != null) {
                                                    for (Bus b : listaBuses) {
                                                        if (!busesOcupados.contains(b.getIdBus())) {
                                            %>
                                            <option value="<%= b.getIdBus()%>">Placa: <%= b.getPlaca()%> (<%= b.getCapacidad()%> Asientos)</option>
                                            <% }
                                                    }
                                                } %>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label text-muted small fw-bold">Chofer Designado</label>
                                        <select class="form-select" name="id_chofer" required>
                                            <option value="">Selecciona el chofer...</option>
                                            <% if (listaChoferes != null) {
                                                    for (Chofer c : listaChoferes) {
                                                        if (!choferesOcupados.contains(c.getIdChofer())) {
                                            %>
                                            <option value="<%= c.getIdChofer()%>"><%= c.getNombre()%></option>
                                            <% }
                                                    }
                                                } %>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label text-muted small fw-bold">Salida Programada</label>
                                        <input type="datetime-local" class="form-control" name="fecha_salida" required>
                                    </div>

                                    <div class="mb-4">
                                        <label class="form-label text-muted small fw-bold">Llegada Estimada</label>
                                        <input type="datetime-local" class="form-control" name="fecha_llegada" required>
                                    </div>

                                    <button type="submit" class="btn btn-primary w-100 fw-bold">Publicar Viaje</button>
                                </form>
                            </div>
                        </div>

                        <!-- Tabla derecha -->
                        <div class="col-md-8">
                            <div class="p-4 bg-white rounded-4 shadow-sm h-100">
                                <h5 class="fw-bold mb-3">Control de Operaciones</h5>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle border text-center">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Salida Estimada</th>
                                                <th>Ruta (Destino)</th>
                                                <th>Bus</th>
                                                <th>Chofer</th>
                                                <th>Estado</th>
                                                <th>Acciones Operativas</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <!-- 
                                            1. Identifica la ruta específica asociada al viaje actual
                                            2. Extrae el id de la sucursal de destino configurada en esa ruta.
                                            3. Busca dicho id en el catálogo de sucursales para obtener el nombre real.-->
                                            <% if (listaViajes != null && !listaViajes.isEmpty()) {
                                                    for (Viaje v : listaViajes) {

                                                        String nombreDestino = "Desconocido";
                                                        if (listaRutas != null) {
                                                            for (Ruta r : listaRutas) {
                                                                if (r.getIdRuta() == v.getIdRuta()) {
                                                                    if (listaSucursales != null) {
                                                                        for (Sucursal s : listaSucursales) {
                                                                            if (s.getIdSucursal() == r.getIdDestino()) {
                                                                                nombreDestino = s.getNombre();
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                            %>
                                            <tr>
                                                <td class="fw-bold text-primary">
                                                    <%= v.getFechaHoraSalidaEstimada().format(formatoFechaTabla)%>
                                                </td>
                                                <td><i class="bi bi-signpost-split-fill text-secondary me-1"></i> <%= nombreDestino%></td>
                                                <td>#<%= v.getIdBus()%></td>
                                                <td>#<%= v.getIdChofer()%></td>
                                                <td>
                                                    <%
                                                        String colorBadge = "bg-secondary";
                                                        if (v.getEstadoViaje() == Enums.EstadoViaje.PROGRAMADO)
                                                            colorBadge = "bg-primary";
                                                        else if (v.getEstadoViaje() == Enums.EstadoViaje.EN_CURSO)
                                                            colorBadge = "bg-warning text-dark";
                                                        else if (v.getEstadoViaje() == Enums.EstadoViaje.FINALIZADO)
                                                            colorBadge = "bg-success";
                                                        else if (v.getEstadoViaje() == Enums.EstadoViaje.CANCELADO)
                                                            colorBadge = "bg-danger";
                                                    %>
                                                    <span class="badge <%= colorBadge%>"><%= v.getEstadoViaje().name()%></span>
                                                </td>
                                                <td>
                                                    <div class="d-flex gap-2 justify-content-center">
                                                        <% if (v.getEstadoViaje() == Enums.EstadoViaje.PROGRAMADO) {%>
                                                        <!-- Controles Pre-Viaje -->
                                                        <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEditar<%= v.getIdViaje()%>" title="Editar Viaje">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <button type="button" class="btn btn-sm btn-success" data-bs-toggle="modal" data-bs-target="#modalIniciar<%= v.getIdViaje()%>" title="Registrar Salida Real">
                                                            <i class="bi bi-play-fill"></i> Salida
                                                        </button>
                                                        <form action="${pageContext.request.contextPath}/Gestionar_Viajes" method="POST" class="m-0" onsubmit="return confirm('¿Seguro que deseas CANCELAR este viaje? (Esta acción no se puede deshacer)');">
                                                            <input type="hidden" name="accion" value="cancelar">
                                                            <input type="hidden" name="id_viaje" value="<%= v.getIdViaje()%>">
                                                            <button type="submit" class="btn btn-sm btn-outline-warning" title="Cancelar Viaje"><i class="bi bi-x-octagon"></i></button>
                                                        </form>
                                                        <form action="${pageContext.request.contextPath}/Gestionar_Viajes" method="POST" class="m-0" onsubmit="return confirm('¿Eliminar viaje permanentemente? Solo se permite si no hay boletos vendidos.');">
                                                            <input type="hidden" name="accion" value="eliminar">
                                                            <input type="hidden" name="id_viaje" value="<%= v.getIdViaje()%>">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Eliminar"><i class="bi bi-trash"></i></button>
                                                        </form>

                                                        <% } else if (v.getEstadoViaje() == Enums.EstadoViaje.EN_CURSO) {%>
                                                        <!-- Control Post-Viaje -->
                                                        <button type="button" class="btn btn-sm btn-dark" data-bs-toggle="modal" data-bs-target="#modalFinalizar<%= v.getIdViaje()%>">
                                                            <i class="bi bi-stop-fill"></i> Registrar Llegada
                                                        </button>

                                                        <% } else { %>
                                                        <span class="text-muted small fw-bold"><i class="bi bi-lock-fill"></i> Bloqueado</span>
                                                        <% } %>
                                                    </div>
                                                </td>
                                            </tr>

                                            <!-- MODALES -->

                                            <!-- Modal Editar -->
                                            <% if (v.getEstadoViaje() == Enums.EstadoViaje.PROGRAMADO) {%>
                                        <div class="modal fade text-start" id="modalEditar<%= v.getIdViaje()%>" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title fw-bold">Editar Viaje hacia <%= nombreDestino%></h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                    </div>
                                                    <form action="${pageContext.request.contextPath}/Gestionar_Viajes" method="POST">
                                                        <div class="modal-body">
                                                            <input type="hidden" name="accion" value="editar">
                                                            <input type="hidden" name="id_viaje" value="<%= v.getIdViaje()%>">

                                                            <div class="mb-3">
                                                                <label class="form-label text-muted small fw-bold">Ruta Comercial</label>
                                                                <select class="form-select" name="id_ruta" required>
                                                                    <% if (listaRutas != null) {
                                                                            for (Ruta r : listaRutas) {
                                                                                String destEdit = "Desconocido";
                                                                                if (listaSucursales != null) {
                                                                                    for (Sucursal s : listaSucursales) {
                                                                                        if (s.getIdSucursal() == r.getIdDestino()) {
                                                                                            destEdit = s.getNombre();
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                }
                                                                    %>
                                                                    <option value="<%= r.getIdRuta()%>" <%= r.getIdRuta() == v.getIdRuta() ? "selected" : ""%>>
                                                                        <%= nombreOrigen%> ➔ <%= destEdit%>
                                                                    </option>
                                                                    <% }
                                                                        } %>
                                                                </select>
                                                            </div>

                                                            <div class="mb-3">
                                                                <label class="form-label text-muted small fw-bold">Bus Asignado</label>
                                                                <select class="form-select" name="id_bus" required>
                                                                    <% if (listaBuses != null) {
                                                                            for (Bus b : listaBuses) {
                                                                                if (!busesOcupados.contains(b.getIdBus()) || b.getIdBus() == v.getIdBus()) {
                                                                    %>
                                                                    <option value="<%= b.getIdBus()%>" <%= b.getIdBus() == v.getIdBus() ? "selected" : ""%>>
                                                                        Placa: <%= b.getPlaca()%> (<%= b.getCapacidad()%> Asientos)
                                                                    </option>
                                                                    <% }
                                                                            }
                                                                        } %>
                                                                </select>
                                                            </div>

                                                            <div class="mb-3">
                                                                <label class="form-label text-muted small fw-bold">Chofer Designado</label>
                                                                <select class="form-select" name="id_chofer" required>
                                                                    <% if (listaChoferes != null) {
                                                                            for (Chofer c : listaChoferes) {
                                                                                if (!choferesOcupados.contains(c.getIdChofer()) || c.getIdChofer() == v.getIdChofer()) {
                                                                    %>
                                                                    <option value="<%= c.getIdChofer()%>" <%= c.getIdChofer() == v.getIdChofer() ? "selected" : ""%>>
                                                                        <%= c.getNombre()%>
                                                                    </option>
                                                                    <% }
                                                                            }
                                                                        }%>
                                                                </select>
                                                            </div>

                                                            <div class="mb-3">
                                                                <label class="form-label text-muted small fw-bold">Salida Programada</label>
                                                                <input type="datetime-local" class="form-control" name="fecha_salida" value="<%= v.getFechaHoraSalidaEstimada().format(formatoFechaInput)%>" required>
                                                            </div>

                                                            <div class="mb-3">
                                                                <label class="form-label text-muted small fw-bold">Llegada Estimada</label>
                                                                <input type="datetime-local" class="form-control" name="fecha_llegada" value="<%= v.getFechaHoraLlegadaEstimada().format(formatoFechaInput)%>" required>
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

                                        <!-- Modal Iniciar Salida -->
                                        <%
                                            // busqueda del nombre del chofer y placa del bus para la alerta del modal
                                            String nombreChoferSalida = "Desconocido";
                                            String placaBusSalida = "Desconocida";

                                            if (listaChoferes != null) {
                                                for (Chofer c : listaChoferes) {
                                                    if (c.getIdChofer() == v.getIdChofer()) {
                                                        nombreChoferSalida = c.getNombre();
                                                        break;
                                                    }
                                                }
                                            }

                                            if (listaBuses != null) {
                                                for (Bus b : listaBuses) {
                                                    if (b.getIdBus() == v.getIdBus()) {
                                                        placaBusSalida = b.getPlaca();
                                                        break;
                                                    }
                                                }
                                            }
                                        %>
                                        <div class="modal fade text-start" id="modalIniciar<%= v.getIdViaje()%>" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content border-success">
                                                    <div class="modal-header bg-success text-white">
                                                        <h5 class="modal-title fw-bold">Autorizar Salida</h5>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                    </div>
                                                    <form action="${pageContext.request.contextPath}/Gestionar_Viajes" method="POST" onsubmit="return confirm('ATENCIÓN: El registro de salida es inmutable y cerrará la venta de boletos. ¿Confirmar datos?');">
                                                        <div class="modal-body">
                                                            <input type="hidden" name="accion" value="iniciar_viaje">
                                                            <input type="hidden" name="id_viaje" value="<%= v.getIdViaje()%>">

                                                            <div class="alert succes-warning small">
                                                                Chofer encargado: <b><%= nombreChoferSalida%></b> <br> Bus con placas <b><%= placaBusSalida%></b>.
                                                            </div>

                                                            <div class="mb-3">
                                                                <label class="form-label fw-bold">Hora Exacta de Salida</label>
                                                                <input type="datetime-local" class="form-control border-success" name="fecha_hora_salida_real" required>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label class="form-label fw-bold">Kilometraje al Arrancar</label>
                                                                <input type="number" step="0.1" min="0" class="form-control border-success" name="kilometraje_salida" required>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                                            <button type="submit" class="btn btn-success fw-bold">Confirmar Salida</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                        <% } %>

                                        <!-- Modal Finalizar Llegada -->
                                        <% if (v.getEstadoViaje() == Enums.EstadoViaje.EN_CURSO) {%>
                                        <div class="modal fade text-start" id="modalFinalizar<%= v.getIdViaje()%>" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content border-dark">
                                                    <div class="modal-header bg-dark text-white">
                                                        <h5 class="modal-title fw-bold">Reporte de Llegada</h5>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                    </div>
                                                    <form action="${pageContext.request.contextPath}/Gestionar_Viajes" method="POST" onsubmit="return confirm('ATENCIÓN: El registro de llegada es inmutable y el cálculo de depreciación será definitivo. ¿Confirmar?');">
                                                        <div class="modal-body">
                                                            <input type="hidden" name="accion" value="finalizar_viaje">
                                                            <input type="hidden" name="id_viaje" value="<%= v.getIdViaje()%>">
                                                            <input type="hidden" name="id_bus" value="<%= v.getIdBus()%>">

                                                            <div class="alert alert-info small">
                                                                El kilometraje registrado sumará desgaste al Bus #<%= v.getIdBus()%> para el cálculo de depreciación.
                                                            </div>

                                                            <div class="mb-3">
                                                                <label class="form-label fw-bold">Hora Exacta de Llegada</label>
                                                                <input type="datetime-local" class="form-control" name="fecha_hora_llegada_real" required>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label class="form-label fw-bold">Kilometraje Final del Bus</label>
                                                                <input type="number" step="0.1" min="0" class="form-control" name="kilometraje_llegada" required>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label class="form-label fw-bold">Gasto en Combustible (Q)</label>
                                                                <input type="number" step="0.01" min="0" class="form-control" name="gasto_combustible" required>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer bg-light">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                                            <button type="submit" class="btn btn-dark fw-bold">Finalizar Operación</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                        <% } %>

                                        <%  }
                                        } else { %>
                                        <tr>
                                            <td colspan="6" class="text-center text-muted py-4">
                                                <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>
                                                Aún no hay viajes regulares programados en tu sucursal.
                                            </td>
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