package controladores;

import dao.SucursalDAO;
import dao.ViajePrivadoDAO;
import excepciones.BDException;
import modelos.ViajePrivado;
import modelos.Usuario;
import java.io.IOException;
import java.time.LocalDateTime;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Optional;
import modelos.Sucursal;

@WebServlet(name = "SolicitudPrivadaServlet", urlPatterns = {"/Solicitar_Privado"})
public class SolicitudPrivadaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            SucursalDAO sucursalDAO = new SucursalDAO();
            request.setAttribute("listaSucursales", sucursalDAO.listarSucursales());
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar sucursales: " + e.getMessage());
        }

        request.getRequestDispatcher("/PaginasUsuarios/solicitar_privado.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuarioLogueado");

        if (usuarioActivo == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            ViajePrivado solicitud = new ViajePrivado();
            solicitud.setIdCliente(usuarioActivo.getIdUsuario());
            int idSucursal = Integer.parseInt(request.getParameter("id_sucursal"));
            solicitud.setIdSucursal(idSucursal);
            solicitud.setOrigen(request.getParameter("origen"));
            String destino = request.getParameter("destino");

            if ("ida_vuelta".equals(request.getParameter("tipo_viaje"))) {
                destino += " (Ida y Vuelta)";
            }
            solicitud.setDestino(destino);

            int pasajeros = Integer.parseInt(request.getParameter("pasajeros"));
            solicitud.setCantidadPasajeros(pasajeros);

    
            LocalDateTime fechaSalida = LocalDateTime.parse(request.getParameter("fecha_salida"));
            LocalDateTime fechaLlegada = LocalDateTime.parse(request.getParameter("fecha_llegada"));

            double tarifaHora = 0;
            double tarifaPasajero = 0;
            Optional<Sucursal> sucOpt = new SucursalDAO().buscarSucursalPorId(idSucursal);
            if (sucOpt.isPresent()) {
                tarifaHora = sucOpt.get().getTarifaBaseHora();
                tarifaPasajero = sucOpt.get().getTarifaPasajero();
            }

            // calculo de las horas redondeando hacia arriba, mínimo 1 hora
            long minutos = Duration.between(fechaSalida, fechaLlegada).toMinutes();
            double horas = Math.ceil(minutos / 60.0);
            if (horas < 1) {
                horas = 1;
            }

            double precioCalculado = (horas * tarifaHora) + (pasajeros * tarifaPasajero);

            solicitud.setFechaHoraSalidaEstimada(fechaSalida);
            solicitud.setFechaHoraLlegadaEstimada(fechaLlegada);
            solicitud.setPrecio(precioCalculado);

            new ViajePrivadoDAO().registrarSolicitud(solicitud);

            sesion.setAttribute("mensajeExito", "¡Tu solicitud ha sido enviada! Un administrador revisará la cotización sugerida pronto. Revisa la pestaña de Cotizaciones en tu Perfil.");
            response.sendRedirect(request.getContextPath() + "/Mi_Perfil");

        } catch (Exception e) {
            sesion.setAttribute("error", "Error al enviar la solicitud: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Solicitar_Privado");
        }
    }
}
