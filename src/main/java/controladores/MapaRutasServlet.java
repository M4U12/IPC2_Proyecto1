package controladores;

import dao.RutaDAO;
import dao.SucursalDAO;
import excepciones.BDException;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "MapaRutasServlet", urlPatterns = {"/Mapa_Rutas"})
public class MapaRutasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            request.setAttribute("listaSucursales", new SucursalDAO().listarSucursales());
            request.setAttribute("listaRutas", new RutaDAO().listarTodasLasRutas());
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar la red de rutas: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/mapa_rutas.jsp").forward(request, response);
    }
}