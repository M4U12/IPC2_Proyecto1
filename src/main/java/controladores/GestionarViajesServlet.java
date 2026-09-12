/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
import java.time.format.DateTimeParseException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

            request.setAttribute("listaViajes", viajeDAO.listarViajesRegularesPorSucursal(miSucursal));
            request.setAttribute("listaRutas", rutaDAO.listarRutasPorSucursal(miSucursal));
            request.setAttribute("listaBuses", busDAO.listarBusesPorSucursal(miSucursal, true));
            request.setAttribute("listaChoferes", choferDAO.listarChoferesPorSucursal(miSucursal, true));
            request.setAttribute("listaSucursales", sucursalDAO.listarSucursales());

        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/programar_viajes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");

        try {
            if ("programar".equals(accion)) {
                procesarProgramacion(request);
            } else if ("editar".equals(accion)) {
                procesarEdicion(request);
            } else if ("eliminar".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                new ViajeDAO().eliminarViaje(idViaje);
                request.getSession().setAttribute("mensajeExito", "Viaje eliminado permanentemente.");
            } else if ("cancelar".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                new ViajeDAO().cancelarViaje(idViaje);
                request.getSession().setAttribute("mensajeExito", "Viaje cancelado (los boletos pueden ser reembolsados).");
            } else if ("iniciar_viaje".equals(accion)) {
                procesarInicio(request);
            } else if ("finalizar_viaje".equals(accion)) {
                procesarFinalizacion(request);
            }
        } catch (BDException e) {
            request.getSession().setAttribute("error", e.getMessage());
        } catch (NumberFormatException | DateTimeParseException e) {
            request.getSession().setAttribute("error", "Error en el formato de los datos ingresados.");
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Viajes");
    }

    private void procesarProgramacion(HttpServletRequest request) throws BDException {
        LocalDateTime fechaSalida = LocalDateTime.parse(request.getParameter("fecha_salida"));
        LocalDateTime fechaLlegada = LocalDateTime.parse(request.getParameter("fecha_llegada"));

        if (fechaSalida.isAfter(fechaLlegada) || fechaSalida.isEqual(fechaLlegada)) {
            request.getSession().setAttribute("error", "La fecha de llegada debe ser posterior a la salida.");
            return;
        }

        Viaje nuevoViaje = new Viaje();
        nuevoViaje.setTipoViaje(Enums.TipoViaje.REGULAR);
        nuevoViaje.setEstadoViaje(Enums.EstadoViaje.PROGRAMADO);
        nuevoViaje.setIdRuta(Integer.parseInt(request.getParameter("id_ruta")));
        nuevoViaje.setIdBus(Integer.parseInt(request.getParameter("id_bus")));
        nuevoViaje.setIdChofer(Integer.parseInt(request.getParameter("id_chofer")));
        nuevoViaje.setFechaHoraSalidaEstimada(fechaSalida);
        nuevoViaje.setFechaHoraLlegadaEstimada(fechaLlegada);

        new ViajeDAO().registrarViaje(nuevoViaje);
        request.getSession().setAttribute("mensajeExito", "Viaje programado con éxito.");
    }

    private void procesarEdicion(HttpServletRequest request) throws BDException {
        LocalDateTime fechaSalida = LocalDateTime.parse(request.getParameter("fecha_salida"));
        LocalDateTime fechaLlegada = LocalDateTime.parse(request.getParameter("fecha_llegada"));

        if (fechaSalida.isAfter(fechaLlegada) || fechaSalida.isEqual(fechaLlegada)) {
            request.getSession().setAttribute("error", "La fecha de llegada debe ser posterior a la salida.");
            return;
        }

        Viaje viajeEditado = new Viaje();
        viajeEditado.setIdViaje(Integer.parseInt(request.getParameter("id_viaje")));
        viajeEditado.setIdRuta(Integer.parseInt(request.getParameter("id_ruta")));
        viajeEditado.setIdBus(Integer.parseInt(request.getParameter("id_bus")));
        viajeEditado.setIdChofer(Integer.parseInt(request.getParameter("id_chofer")));
        viajeEditado.setFechaHoraSalidaEstimada(fechaSalida);
        viajeEditado.setFechaHoraLlegadaEstimada(fechaLlegada);

        new ViajeDAO().actualizarViajeRegular(viajeEditado);
        request.getSession().setAttribute("mensajeExito", "Datos del viaje actualizados.");
    }

    private void procesarInicio(HttpServletRequest request) throws BDException {
        int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
        double kilometrajeSalida = Double.parseDouble(request.getParameter("kilometraje_salida"));
        LocalDateTime fechaHoraReal = LocalDateTime.parse(request.getParameter("fecha_hora_salida_real"));

        new ViajeDAO().iniciarViaje(idViaje, kilometrajeSalida, fechaHoraReal);
        request.getSession().setAttribute("mensajeExito", "¡Buen viaje! Salida registrada oficialmente.");
    }

    private void procesarFinalizacion(HttpServletRequest request) throws BDException {
        int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
        int idBus = Integer.parseInt(request.getParameter("id_bus"));
        double kilometrajeFinal = Double.parseDouble(request.getParameter("kilometraje_llegada"));
        double gastoCombustible = Double.parseDouble(request.getParameter("gasto_combustible"));
        LocalDateTime fechaHoraReal = LocalDateTime.parse(request.getParameter("fecha_hora_llegada_real"));

        new ViajeDAO().finalizarViaje(idViaje, kilometrajeFinal, gastoCombustible, fechaHoraReal);

        // sumar kilometraje a la unidad para el control de desgaste
        new BusDAO().actualizarEstadoOperativoYKilometraje(idBus, "Activo", kilometrajeFinal);

        request.getSession().setAttribute("mensajeExito", "Llegada registrada. Viaje finalizado con éxito.");
    }
}
