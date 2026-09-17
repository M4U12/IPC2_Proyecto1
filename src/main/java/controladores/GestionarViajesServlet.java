package controladores;

import dao.BusDAO;
import dao.ChoferDAO;
import dao.RutaDAO;
import dao.SucursalDAO;
import dao.ViajeDAO;
import excepciones.BDException;
import modelos.Bus;
import modelos.Chofer;
import modelos.Ruta;
import modelos.Viaje;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
import java.time.LocalDateTime;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GestionarViajesServlet", urlPatterns = {"/Gestionar_Viajes"})
public class GestionarViajesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        ViajeDAO viajeDAO = new ViajeDAO();
        RutaDAO rutaDAO = new RutaDAO();
        BusDAO busDAO = new BusDAO();
        ChoferDAO choferDAO = new ChoferDAO();
        SucursalDAO sucursalDAO = new SucursalDAO();

        try {
            int miSucursal = usuarioActivo.getIdSucursalAsignada();
            List<Viaje> todosLosViajes = viajeDAO.listarViajesRegularesPorSucursal(miSucursal);
            List<Viaje> viajesActivos = new ArrayList<>();
            for (Viaje v : todosLosViajes) {
                if (v.getEstadoViaje() != Enums.EstadoViaje.FINALIZADO) {
                    viajesActivos.add(v);
                }
            }
            
            request.setAttribute("listaBuses", busDAO.listarBusesPorSucursal(miSucursal, true));
            request.setAttribute("listaChoferes", choferDAO.listarChoferesPorSucursal(miSucursal, true));
            request.setAttribute("listaSucursales", sucursalDAO.listarSucursales());
            request.setAttribute("listaViajes", viajesActivos);
            request.setAttribute("listaRutas", rutaDAO.listarRutasPorSucursal(miSucursal));

            request.getRequestDispatcher("/AdminSucursal/programar_viajes.jsp").forward(request, response);

        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
            request.getRequestDispatcher("/AdminSucursal/programar_viajes.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");

        try {
            if ("programar".equals(accion)) {
                procesarProgramacion(request);
            } else if ("editar".equals(accion)) {
                procesarEdicion(request);
            } else if ("eliminar".equals(accion) || "cancelar".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                int idBus = Integer.parseInt(request.getParameter("id_bus"));
                int idChofer = Integer.parseInt(request.getParameter("id_chofer"));

                new ViajeDAO().procesarLiberacionDeRecursos(idViaje, idBus, idChofer, accion);

                request.getSession().setAttribute("mensajeExito", "Acción procesada y recursos liberados de forma segura.");
            } else if ("iniciar_viaje".equals(accion)) {
                procesarInicio(request);
            } else if ("finalizar_viaje".equals(accion)) {
                procesarFinalizacion(request);
            }
        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Viajes");
    }

    private void procesarProgramacion(HttpServletRequest request) throws BDException {
        LocalDateTime fechaSalida = LocalDateTime.parse(request.getParameter("fecha_salida"));
        LocalDateTime fechaLlegada = LocalDateTime.parse(request.getParameter("fecha_llegada"));

        if (fechaSalida.isAfter(fechaLlegada) || fechaSalida.isEqual(fechaLlegada)) {
            throw new BDException("La fecha de llegada debe ser posterior a la salida.");
        }

        int idBus = Integer.parseInt(request.getParameter("id_bus"));
        int idChofer = Integer.parseInt(request.getParameter("id_chofer"));

        Viaje nuevoViaje = new Viaje();
        nuevoViaje.setEstadoViaje(Enums.EstadoViaje.PROGRAMADO);
        nuevoViaje.setIdRuta(Integer.parseInt(request.getParameter("id_ruta")));
        nuevoViaje.setIdBus(idBus);
        nuevoViaje.setIdChofer(idChofer);
        nuevoViaje.setFechaHoraSalidaEstimada(fechaSalida);
        nuevoViaje.setFechaHoraLlegadaEstimada(fechaLlegada);

        new ViajeDAO().procesarProgramacionCompleta(nuevoViaje, idBus, idChofer);

        request.getSession().setAttribute("mensajeExito", "Viaje programado con éxito.");
    }

    private void procesarEdicion(HttpServletRequest request) throws BDException {
        LocalDateTime fechaSalida = LocalDateTime.parse(request.getParameter("fecha_salida"));
        LocalDateTime fechaLlegada = LocalDateTime.parse(request.getParameter("fecha_llegada"));

        if (fechaSalida.isAfter(fechaLlegada) || fechaSalida.isEqual(fechaLlegada)) {
            throw new BDException("La fecha de llegada debe ser posterior a la salida.");
        }

        int idBusNuevo = Integer.parseInt(request.getParameter("id_bus"));
        int idBusAntiguo = Integer.parseInt(request.getParameter("id_bus_antiguo"));
        int idChoferNuevo = Integer.parseInt(request.getParameter("id_chofer"));
        int idChoferAntiguo = Integer.parseInt(request.getParameter("id_chofer_antiguo"));

        Viaje viajeEditado = new Viaje();
        viajeEditado.setIdViaje(Integer.parseInt(request.getParameter("id_viaje")));
        viajeEditado.setIdRuta(Integer.parseInt(request.getParameter("id_ruta")));
        viajeEditado.setIdBus(idBusNuevo);
        viajeEditado.setIdChofer(idChoferNuevo);
        viajeEditado.setFechaHoraSalidaEstimada(fechaSalida);
        viajeEditado.setFechaHoraLlegadaEstimada(fechaLlegada);

        new ViajeDAO().procesarEdicionCompleta(viajeEditado, idBusAntiguo, idChoferAntiguo);

        request.getSession().setAttribute("mensajeExito", "Datos del viaje actualizados correctamente.");
    }

    private void procesarInicio(HttpServletRequest request) throws BDException {
        int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
        double kilometrajeSalida = Double.parseDouble(request.getParameter("kilometraje_salida"));
        LocalDateTime fechaHoraReal = LocalDateTime.parse(request.getParameter("fecha_hora_salida_real"));

        new ViajeDAO().iniciarViaje(idViaje, kilometrajeSalida, fechaHoraReal);
        request.getSession().setAttribute("mensajeExito", "¡Buen viaje! Salida registrada oficialmente.");
    }

    private void procesarFinalizacion(HttpServletRequest request) throws Exception {
        int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
        int idBus = Integer.parseInt(request.getParameter("id_bus"));
        int idChofer = Integer.parseInt(request.getParameter("id_chofer"));

        double kilometrajeFinal = Double.parseDouble(request.getParameter("kilometraje_llegada"));
        double kmSalida = Double.parseDouble(request.getParameter("km_salida"));
        if (kilometrajeFinal < kmSalida) {
            throw new Exception("Error: El kilometraje final (" + kilometrajeFinal + ") no puede ser menor al de salida (" + kmSalida + ").");
        }
        double gastoCombustible = Double.parseDouble(request.getParameter("gasto_combustible"));
        LocalDateTime fechaHoraReal = LocalDateTime.parse(request.getParameter("fecha_hora_llegada_real"));

        new ViajeDAO().procesarFinalizacionCompleta(idViaje, idBus, idChofer, kilometrajeFinal, gastoCombustible, fechaHoraReal);

        request.getSession().setAttribute("mensajeExito", "Llegada registrada. Viaje finalizado con éxito.");
    }
}
