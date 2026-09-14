package controladores;

import dao.BusDAO;
import dao.ChoferDAO;
import dao.ViajePrivadoDAO;
import excepciones.BDException;
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

@WebServlet(name = "GestionarPrivadosServlet", urlPatterns = {"/Gestionar_Privados"})
public class GestionarPrivadosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            int miSucursal = usuarioActivo.getIdSucursalAsignada();
            ViajePrivadoDAO vpDAO = new ViajePrivadoDAO();
            BusDAO busDAO = new BusDAO();
            ChoferDAO choferDAO = new ChoferDAO();

            request.setAttribute("listaPrivados", vpDAO.listarPorSucursal(miSucursal));
            request.setAttribute("listaBuses", busDAO.listarBusesPorSucursal(miSucursal, true));
            request.setAttribute("listaChoferes", choferDAO.listarChoferesPorSucursal(miSucursal, true));

        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/programar_privados.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();

        try {
            ViajePrivadoDAO vpDAO = new ViajePrivadoDAO();
            int idViaje = Integer.parseInt(request.getParameter("id_viaje_privado"));

            if ("cotizar".equals(accion)) {
                double precio = Double.parseDouble(request.getParameter("precio"));
                if (precio < 0) {
                    throw new Exception("El precio de la cotización no puede ser menor a 0.");
                }
                LocalDateTime llegadaEst = LocalDateTime.parse(request.getParameter("fecha_llegada"));
                vpDAO.cotizarViaje(idViaje, precio, llegadaEst);
                sesion.setAttribute("mensajeExito", "Cotización enviada al cliente.");

            }else if ("cancelar".equals(accion)) {
                vpDAO.cancelarViajePrivado(idViaje);
                sesion.setAttribute("mensajeExito", "Solicitud cancelada.");
                
            } else if ("eliminar".equals(accion)) {
                vpDAO.eliminarViajePrivado(idViaje);
                sesion.setAttribute("mensajeExito", "Solicitud eliminada permanentemente del sistema.");
                
            } else if ("asignar".equals(accion)) {
                int idBus = Integer.parseInt(request.getParameter("id_bus"));
                int idChofer = Integer.parseInt(request.getParameter("id_chofer"));
                vpDAO.asignarRecursos(idViaje, idBus, idChofer);
                new dao.BusDAO().actualizarEstadoOperativo(idBus, Enums.EstadoOperativo.EN_RUTA);
                sesion.setAttribute("mensajeExito", "Unidad y chofer asignados. Listo para salir.");

            } else if ("iniciar".equals(accion)) {
                double kmSalida = Double.parseDouble(request.getParameter("kilometraje_salida"));
                LocalDateTime salidaReal = LocalDateTime.parse(request.getParameter("fecha_hora_salida_real"));
                vpDAO.iniciarViaje(idViaje, kmSalida, salidaReal);
                sesion.setAttribute("mensajeExito", "Viaje privado iniciado oficialmente.");

            } else if ("finalizar".equals(accion)) {
                double kmLlegada = Double.parseDouble(request.getParameter("kilometraje_llegada"));
                double kmSalida = Double.parseDouble(request.getParameter("km_salida"));
                double gasto = Double.parseDouble(request.getParameter("gasto_combustible"));
                LocalDateTime llegadaReal = LocalDateTime.parse(request.getParameter("fecha_hora_llegada_real"));
                if (kmLlegada < kmSalida) {
                    throw new Exception("El kilometraje final (" + kmLlegada + ") no puede ser menor al kilometraje de salida (" + kmSalida + ").");
                }
                vpDAO.finalizarViaje(idViaje, kmLlegada, gasto, llegadaReal);

                int idBus = Integer.parseInt(request.getParameter("id_bus"));
                new BusDAO().actualizarEstadoOperativoYKilometraje(idBus, Enums.EstadoOperativo.DISPONIBLE, kmLlegada);

                sesion.setAttribute("mensajeExito", "Viaje privado finalizado con éxito.");
            }
        } catch (Exception e) {
            sesion.setAttribute("error", "Error en la operación: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Privados");
    }
}
