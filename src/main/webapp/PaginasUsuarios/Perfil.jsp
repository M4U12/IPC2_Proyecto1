<%@page import="modelos.Usuario"%>
<%@page import="modelos.Cartera"%>
<%@page import="modelos.ViajePrivado"%>
<%@page import="modelos.Enums"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="modelos.Boleto"%>
<%@page import="modelos.Viaje"%>
<%@page import="modelos.Ruta"%>
<%@page import="modelos.Sucursal"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mi Perfil - Code 'n Buses</title>
        <jsp:include page="/Componentes/recursos.jsp" />
    </head>
    <body class="bg-light">

        <div class="container-fluid p-0">
            <div class="row g-0">

                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">
                    <%
                        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
                        Cartera cartera = (Cartera) request.getAttribute("miCartera");
                        List<ViajePrivado> listaPrivados = (List<ViajePrivado>) request.getAttribute("listaPrivadosCliente");

                        //listas para regulares
                        List<Boleto> listaBoletos = (List<Boleto>) request.getAttribute("listaBoletos");
                        List<Viaje> listaViajesCliente = (List<Viaje>) request.getAttribute("listaViajesCliente");
                        List<Ruta> listaRutas = (List<Ruta>) request.getAttribute("listaRutas");
                        List<Sucursal> listaSucursales = (List<Sucursal>) request.getAttribute("listaSucursales");

                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        String exitoMsg = (String) request.getAttribute("mensajeExito");
                        if (exitoMsg == null) {
                            exitoMsg = (String) session.getAttribute("mensajeExito");
                        }

                        String errorMsg = (String) request.getAttribute("error");
                        if (errorMsg == null)
                            errorMsg = (String) session.getAttribute("error");
                    %>

                    <% if (exitoMsg != null) {%>
                    <div class="alert alert-success fw-bold"><%= exitoMsg%></div>
                    <% session.removeAttribute("mensajeExito");
                        } %>

                    <% if (errorMsg != null) {%>
                    <div class="alert alert-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> <%= errorMsg%></div>
                    <% session.removeAttribute("error");
                        }%>

                    <!-- Encabezado Principal del Perfil -->
                    <div class="card shadow-sm border-0 rounded-4 mb-4">
                        <div class="card-body p-4 d-flex justify-content-between align-items-center">
                            <div class="d-flex align-items-center">
                                <i class="bi bi-person-circle display-4 text-primary me-3"></i>
                                <div>
                                    <div class="d-flex align-items-center gap-3 mb-1">
                                        <h2 class="fw-bold mb-0"><%= usuario.getNombre()%></h2>
                                        <button type="button" class="btn btn-sm btn-outline-secondary fw-bold" data-bs-toggle="modal" data-bs-target="#modalEditarPerfil">
                                            <i class="bi bi-pencil-square"></i> Editar
                                        </button>
                                    </div>
                                    <p class="text-muted mb-0">DPI: <%= usuario.getDpi()%> | NIT: <%= usuario.getNit()%></p>
                                </div>
                            </div>
                            <div class="text-end bg-light px-4 py-2 rounded-3 shadow-sm border border-white">
                                <span class="text-muted small fw-bold">Saldo en Cartera</span><br>
                                <span class="fs-3 fw-bold text-success">
                                    Q.<%= cartera != null ? String.format("%.2f", cartera.getCantidadDinero()) : "0.00"%>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Pestañas de Historial y Cotizaciones -->
                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                        <div class="card-header bg-white border-bottom p-0">
                            <ul class="nav nav-tabs nav-fill border-0" id="historialTabs" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link active fw-bold py-3 text-dark border-0" data-bs-toggle="tab" data-bs-target="#tab-regulares">
                                        <i class="bi bi-bus-front"></i> Viajes Regulares
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link fw-bold py-3 text-dark border-0" data-bs-toggle="tab" data-bs-target="#tab-privados">
                                        <i class="bi bi-star"></i> Viajes Privados Activos
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link fw-bold py-3 text-dark border-0" data-bs-toggle="tab" data-bs-target="#tab-cotizaciones">
                                        <i class="bi bi-envelope-paper"></i> Mis Cotizaciones
                                    </button>
                                </li>
                            </ul>
                        </div>

                        <div class="card-body p-4 tab-content">

                            <!-- Pestaña 1: Viajes Regulares -->
                            <div class="tab-pane fade show active" id="tab-regulares">
                                <% if (listaBoletos != null && !listaBoletos.isEmpty()) { %>
                                <div class="list-group list-group-flush">
                                    <% for (Boleto b : listaBoletos) {
                                            Viaje vAsociado = null;
                                            if (listaViajesCliente != null) {
                                                for (Viaje v : listaViajesCliente) {
                                                    if (v.getIdViaje() == b.getIdViaje()) {
                                                        vAsociado = v;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (vAsociado != null) {
                                                String origen = "Desconocido";
                                                String destino = "Desconocido";
                                                if (listaRutas != null && listaSucursales != null) {
                                                    for (Ruta r : listaRutas) {
                                                        if (r.getIdRuta() == vAsociado.getIdRuta()) {
                                                            for (Sucursal s : listaSucursales) {
                                                                if (s.getIdSucursal() == r.getIdOrigen()) {
                                                                    origen = s.getNombre();
                                                                }
                                                                if (s.getIdSucursal() == r.getIdDestino()) {
                                                                    destino = s.getNombre();
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                    %>
                                    <div class="list-group-item px-0 py-3 d-flex justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1 fw-bold text-dark">
                                                <i class="bi bi-geo-alt-fill text-success"></i> <%= origen%> <br>
                                                <i class="bi bi-flag-fill text-danger"></i> <%= destino%>
                                            </h6>
                                            <small class="text-muted">
                                                <i class="bi bi-calendar-event"></i> Salida: <%= vAsociado.getFechaHoraSalidaEstimada().format(formatoFecha)%>
                                                | <i class="bi bi-heptagon"></i> Asiento: <b>#<%= b.getNumeroAsiento()%></b>
                                            </small>
                                        </div>
                                        <div class="text-end">
                                            <span class="d-block fw-bold text-dark mb-1">Q.<%= String.format("%.2f", b.getPrecioPagado())%></span>

                                            <% if (vAsociado.getEstadoViaje() == Enums.EstadoViaje.PROGRAMADO) { %>
                                            <span class="badge bg-primary"><i class="bi bi-ticket-detailed"></i> Boleto Comprado</span>
                                            <% } else if (vAsociado.getEstadoViaje() == Enums.EstadoViaje.EN_CURSO) { %>
                                            <span class="badge bg-warning text-dark"><i class="bi bi-bus-front-fill"></i> Viaje en Curso</span>
                                            <% } else if (vAsociado.getEstadoViaje() == Enums.EstadoViaje.FINALIZADO) { %>
                                            <span class="badge bg-success"><i class="bi bi-check-circle-fill"></i> Finalizado</span>
                                            <% } else if (vAsociado.getEstadoViaje() == Enums.EstadoViaje.CANCELADO) { %>
                                            <span class="badge bg-danger"><i class="bi bi-x-circle-fill"></i> Cancelado</span>
                                            <% } %>
                                        </div>
                                    </div>
                                    <%      }
                                            } %>
                                </div>
                                <% } else { %>
                                <div class="alert alert-light text-center py-5">
                                    <i class="bi bi-ticket-perforated display-4 text-muted d-block mb-3"></i>
                                    <p class="text-muted">Aún no has comprado boletos para nuestras rutas regulares.</p>
                                    <a href="${pageContext.request.contextPath}/Viajes_Disponibles" class="btn btn-outline-primary mt-2">Ver Rutas Disponibles</a>
                                </div>
                                <% } %>
                            </div>

                            <!-- Pestaña 2: Viajes Privados (PAGADOS, EN CURSO o FINALIZADOS) -->
                            <div class="tab-pane fade" id="tab-privados">
                                <% boolean hayPrivados = false;
                                    if (listaPrivados != null) {
                                        for (ViajePrivado vp : listaPrivados) {
                                            if (vp.getEstado() == Enums.EstadoViaje.PAGADA || vp.getEstado() == Enums.EstadoViaje.EN_CURSO || vp.getEstado() == Enums.EstadoViaje.FINALIZADO) {
                                                hayPrivados = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (hayPrivados) { %>
                                <div class="list-group list-group-flush">
                                    <% for (ViajePrivado vp : listaPrivados) {
                                            if (vp.getEstado() == Enums.EstadoViaje.PAGADA || vp.getEstado() == Enums.EstadoViaje.EN_CURSO || vp.getEstado() == Enums.EstadoViaje.FINALIZADO) {%>
                                    <div class="list-group-item px-0 py-3 d-flex justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1 fw-bold text-dark">
                                                <i class="bi bi-geo-alt-fill text-success"></i> <%= vp.getOrigen()%> <br>
                                                <i class="bi bi-flag-fill text-danger"></i> <%= vp.getDestino()%>
                                            </h6>
                                            <small class="text-muted"><i class="bi bi-calendar-event"></i> Salida Estimada: <%= vp.getFechaHoraSalidaEstimada().format(formatoFecha)%></small>
                                        </div>
                                        <div class="text-end">
                                            <% if (vp.getEstado() == Enums.EstadoViaje.PAGADA) {
                                                    if (vp.getIdBus() == null) { %>
                                            <span class="badge bg-primary">Esperando Asignación de Bus</span>
                                            <% } else { %>
                                            <span class="badge bg-info text-dark fw-bold">
                                                <i class="bi bi-bus-front-fill"></i> Unidad Asignada
                                            </span>
                                            <% }
                                            } else if (vp.getEstado() == Enums.EstadoViaje.EN_CURSO) { %>
                                            <span class="badge bg-dark">Viaje en Curso</span>
                                            <% } else { %>
                                            <span class="badge bg-success">Completado</span>
                                            <% } %>
                                        </div>
                                    </div>
                                    <% }
                                        } %>
                                </div>
                                <% } else { %>
                                <div class="alert alert-light text-center py-5">
                                    <i class="bi bi-journal-check display-4 text-muted d-block mb-3"></i>
                                    <p class="text-muted">Aún no tienes viajes privados activos o completados.</p>
                                </div>
                                <% } %>
                            </div>

                            <!-- Pestaña 3: Cotizaciones (PENDIENTE o COTIZADA) -->
                            <div class="tab-pane fade" id="tab-cotizaciones">
                                <% boolean hayCotizaciones = false;
                                    if (listaPrivados != null) {
                                        for (ViajePrivado vp : listaPrivados) {
                                            if (vp.getEstado() == Enums.EstadoViaje.PENDIENTE || vp.getEstado() == Enums.EstadoViaje.COTIZADA) {
                                                hayCotizaciones = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (hayCotizaciones) { %>
                                <div class="list-group list-group-flush">
                                    <% for (ViajePrivado vp : listaPrivados) {
                                            if (vp.getEstado() == Enums.EstadoViaje.PENDIENTE || vp.getEstado() == Enums.EstadoViaje.COTIZADA) {%>
                                    <div class="list-group-item px-0 py-3 d-flex justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1 fw-bold text-dark">
                                                <i class="bi bi-geo-alt-fill text-success"></i> <%= vp.getOrigen()%> <br>
                                                <i class="bi bi-flag-fill text-danger"></i> <%= vp.getDestino()%>
                                            </h6>
                                            <small class="text-muted"><i class="bi bi-calendar-event"></i> Solicitado para: <%= vp.getFechaHoraSalidaEstimada().format(formatoFecha)%> | Pasajeros: <%= vp.getCantidadPasajeros()%></small>
                                        </div>

                                        <div class="text-end">
                                            <% if (vp.getEstado() == Enums.EstadoViaje.PENDIENTE) { %>
                                            <span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split"></i> Evaluando Presupuesto...</span>
                                            <% } else if (vp.getEstado() == Enums.EstadoViaje.COTIZADA) {%>
                                            <span class="d-block fw-bold text-success mb-2 fs-5">Q.<%= String.format("%.2f", vp.getPrecio())%></span>
                                            <form action="${pageContext.request.contextPath}/Mi_Perfil" method="POST" onsubmit="return confirm('¿Confirmas el pago de Q.<%= String.format("%.2f", vp.getPrecio())%> con el saldo de tu Billetera?');">
                                                <input type="hidden" name="accion" value="pagar_cotizacion">
                                                <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">
                                                <input type="hidden" name="monto" value="<%= vp.getPrecio()%>">
                                                <button type="submit" class="btn btn-success fw-bold"><i class="bi bi-wallet2"></i> Pagar y Programar</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/Mi_Perfil" method="POST" class="mt-2" onsubmit="return confirm('¿Seguro que deseas cancelar esta cotización?');">
                                                <input type="hidden" name="accion" value="cancelar_solicitud">
                                                <input type="hidden" name="id_viaje_privado" value="<%= vp.getIdViajePrivado()%>">
                                                <button type="submit" class="btn btn-outline-danger btn-sm fw-bold w-100">Cancelar Solicitud</button>
                                            </form>
                                            <% } %>
                                        </div>
                                    </div>
                                    <% }
                                        } %>
                                </div>
                                <% } else { %>
                                <div class="alert alert-light text-center py-5">
                                    <i class="bi bi-ui-checks display-4 text-muted d-block mb-3"></i>
                                    <p class="text-muted">No tienes cotizaciones pendientes de revisión o pago.</p>
                                    <a href="${pageContext.request.contextPath}/Solicitar_Privado" class="btn btn-outline-primary mt-2">Cotizar Nuevo Viaje</a>
                                </div>
                                <% }%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Perfil -->
        <div class="modal fade" id="modalEditarPerfil" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <div class="modal-header border-bottom-0 pb-0">
                        <h5 class="modal-title fw-bold"><i class="bi bi-person-gear"></i> Editar Información Personal</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/Mi_Perfil" method="POST">
                        <div class="modal-body">
                            <!-- ACCIÓN EXCLUSIVA PARA EL PERFIL -->
                            <input type="hidden" name="accion" value="actualizar_perfil">

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold small text-muted">DPI (No modificable)</label>
                                    <input type="text" class="form-control bg-light" value="<%= usuario.getDpi()%>" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold small">Nombre Completo</label>
                                    <input type="text" class="form-control" name="nombre" value="<%= usuario.getNombre()%>" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold small">NIT</label>
                                    <input type="text" class="form-control" name="nit" value="<%= usuario.getNit()%>" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold small">Teléfono</label>
                                    <input type="text" class="form-control" name="telefono" value="<%= usuario.getTelefono()%>" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label fw-bold small">Dirección</label>
                                    <input type="text" class="form-control" name="direccion" value="<%= usuario.getDireccion()%>" required>
                                </div>
                                <div class="col-12 mt-4">
                                    <label class="form-label fw-bold small">Cambiar Contraseña</label>
                                    <input type="password" class="form-control" name="password" placeholder="Ingresa una nueva contraseña si deseas cambiarla">
                                    <div class="form-text text-muted">Déjalo en blanco para conservar tu contraseña actual.</div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer border-top-0 pt-0">
                            <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-primary fw-bold px-4">Guardar Cambios</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>