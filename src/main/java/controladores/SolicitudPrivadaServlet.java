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
            solicitud.setIdSucursal(Integer.parseInt(request.getParameter("id_sucursal")));
            
            solicitud.setOrigen(request.getParameter("origen"));
            solicitud.setDestino(request.getParameter("destino"));
            
            solicitud.setCantidadPasajeros(Integer.parseInt(request.getParameter("pasajeros")));

            LocalDateTime fechaSalida = LocalDateTime.parse(request.getParameter("fecha_salida"));
            solicitud.setFechaHoraSalidaEstimada(fechaSalida);

            ViajePrivadoDAO dao = new ViajePrivadoDAO();
            dao.registrarSolicitud(solicitud);

            sesion.setAttribute("mensajeExito", "¡Tu solicitud ha sido enviada! Un administrador la revisará pronto para asignarle un precio. Revisa la pestaña de Cotizaciones en tu Perfil.");
            response.sendRedirect(request.getContextPath() + "/Mi_Perfil");

        } catch (Exception e) {
            sesion.setAttribute("error", "Error al enviar la solicitud: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Solicitar_Privado");
        }
    }
}