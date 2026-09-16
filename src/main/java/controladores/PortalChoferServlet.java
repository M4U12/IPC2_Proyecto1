package controladores;

import dao.BusDAO;
import dao.ChoferDAO;
import dao.ViajeDAO;
import dao.ViajePrivadoDAO;
import excepciones.BDException;
import modelos.Bus;
import modelos.Chofer;
import modelos.Viaje;
import modelos.ViajePrivado;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PortalChoferServlet", urlPatterns = {"/Portal_Chofer"})
public class PortalChoferServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        Chofer choferActivo = (Chofer) sesion.getAttribute("choferLogueado");

        // si inició sesion se busca si tiene viajes asignados
        if (choferActivo != null) {
            try {
                ViajeDAO vDAO = new ViajeDAO();
                ViajePrivadoDAO vpDAO = new ViajePrivadoDAO();
                BusDAO busDAO = new BusDAO();

                Optional<Viaje> viajeRegular = vDAO.obtenerViajeActivoPorChofer(choferActivo.getIdChofer());
                Optional<ViajePrivado> viajePrivado = vpDAO.obtenerViajePrivadoActivoPorChofer(choferActivo.getIdChofer());

                if (viajeRegular.isPresent()) {
                    request.setAttribute("viajeRegular", viajeRegular.get());
                    Optional<Bus> bus = busDAO.obtenerBusPorId(viajeRegular.get().getIdBus());
                    
                    if (bus.isPresent()) {
                        request.setAttribute("kmActualBus", bus.get().getKilometrajeActual());
                        request.setAttribute("busAsignado", bus.get());
                    }
                }

                if (viajePrivado.isPresent()) {
                    request.setAttribute("viajePrivado", viajePrivado.get());
                    Optional<Bus> bus = busDAO.obtenerBusPorId(viajePrivado.get().getIdBus());
                    if (bus.isPresent()) {
                        request.setAttribute("kmActualBus", bus.get().getKilometrajeActual());
                        request.setAttribute("busAsignado", bus.get());
                    }
                }

            } catch (BDException e) {
                request.setAttribute("error", "Error al cargar tu itinerario: " + e.getMessage());
            }
        }
        request.getRequestDispatcher("/Chofer/portal_chofer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();

        try {
            if ("login".equals(accion)) {
                String licencia = request.getParameter("num_licencia");
                Optional<Chofer> choferOpt = new ChoferDAO().autenticarPorLicencia(licencia);

                if (choferOpt.isPresent()) {
                    sesion.setAttribute("choferLogueado", choferOpt.get());
                } else {
                    sesion.setAttribute("error", "Número de licencia incorrecto o chofer inactivo.");
                }
            } 
            else if ("logout".equals(accion)) {
                sesion.removeAttribute("choferLogueado");
            } 
            else if ("iniciar_regular".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                double kmSalida = Double.parseDouble(request.getParameter("km_salida"));
                LocalDateTime salidaReal = LocalDateTime.parse(request.getParameter("fecha_hora"));
                new ViajeDAO().iniciarViaje(idViaje, kmSalida, salidaReal);
                sesion.setAttribute("mensajeExito", "¡Buen viaje! Salida registrada.");
            } 
            else if ("finalizar_regular".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                int idBus = Integer.parseInt(request.getParameter("id_bus"));
                int idChofer = Integer.parseInt(request.getParameter("id_chofer"));
                double kmLlegada = Double.parseDouble(request.getParameter("km_llegada"));
                double gasto = Double.parseDouble(request.getParameter("gasto_combustible"));
                LocalDateTime llegadaReal = LocalDateTime.parse(request.getParameter("fecha_hora"));
                new ViajeDAO().procesarFinalizacionCompleta(idViaje, idBus, idChofer, kmLlegada, gasto, llegadaReal);
                sesion.setAttribute("mensajeExito", "Ruta finalizada. Buen trabajo.");
            }
            else if ("iniciar_privado".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                double kmSalida = Double.parseDouble(request.getParameter("km_salida"));
                LocalDateTime salidaReal = LocalDateTime.parse(request.getParameter("fecha_hora"));
                new ViajePrivadoDAO().iniciarViaje(idViaje, kmSalida, salidaReal);
                sesion.setAttribute("mensajeExito", "¡Buen viaje privado! Salida registrada.");
            }
            else if ("finalizar_privado".equals(accion)) {
                int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
                int idBus = Integer.parseInt(request.getParameter("id_bus"));
                int idChofer = Integer.parseInt(request.getParameter("id_chofer"));
                double kmLlegada = Double.parseDouble(request.getParameter("km_llegada"));
                double gasto = Double.parseDouble(request.getParameter("gasto_combustible"));
                LocalDateTime llegadaReal = LocalDateTime.parse(request.getParameter("fecha_hora"));
                new ViajePrivadoDAO().procesarFinalizacionCompleta(idViaje, idBus, idChofer, kmLlegada, gasto, llegadaReal);
                sesion.setAttribute("mensajeExito", "Viaje privado finalizado. Buen trabajo.");
            }
        } catch (Exception e) {
            sesion.setAttribute("error", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Portal_Chofer");
    }
}