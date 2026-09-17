package controladores;

import dao.ViajeDAO;
import dao.ViajePrivadoDAO;
import excepciones.BDException;
import modelos.Viaje;
import modelos.ViajePrivado;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "HistorialViajesServlet", urlPatterns = {"/Historial_Viajes"})
public class HistorialViajesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        int idMiSucursal = usuarioActivo.getIdSucursalAsignada();

        try {

            List<Viaje> regularesFinalizados = new ViajeDAO().listarHistorialRegulares(idMiSucursal);

            List<ViajePrivado> privadosFinalizados = new ArrayList<>();
            for (ViajePrivado vp : new ViajePrivadoDAO().listarPorSucursal(idMiSucursal)) {
                if (vp.getEstado() == Enums.EstadoViaje.FINALIZADO) {
                    privadosFinalizados.add(vp);
                }
            }

            request.setAttribute("historialRegulares", regularesFinalizados);
            request.setAttribute("historialPrivados", privadosFinalizados);

        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar la bitácora: " + e.getMessage());
        }
        request.getRequestDispatcher("/AdminSucursal/historial_viajes.jsp").forward(request, response);
    }
}
