package controladores;

import dao.RutaDAO;
import dao.SucursalDAO; 
import excepciones.BDException;
import modelos.Ruta;
import modelos.Sucursal;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ACER
 */
@WebServlet(name = "GestionarRutasServlet", urlPatterns = {"/Gestionar_Rutas"})
public class GestionarRutasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        RutaDAO rutaDAO = new RutaDAO();
        SucursalDAO sucursalDAO = new SucursalDAO();

        try {
            int idMiSucursal = usuarioActivo.getIdSucursalAsignada();

            List<Ruta> listaRutas = rutaDAO.listarRutasPorSucursal(idMiSucursal);
            List<Sucursal> listaSucursales = sucursalDAO.listarSucursales(); // Para llenar el select de destinos
            
            //nombre exacto de la sucursal del administrador
            String nombreSucursal = "Mi Sucursal";
            if (listaSucursales != null) {
                for (Sucursal s : listaSucursales) {
                    if (s.getIdSucursal() == idMiSucursal) {
                        nombreSucursal = s.getNombre();
                        break;
                    }
                }
            }

            request.setAttribute("listaRutas", listaRutas);
            request.setAttribute("listaSucursales", listaSucursales);
            request.setAttribute("nombreSucursal", nombreSucursal);
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar los datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/rutas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String accion = request.getParameter("accion");
        RutaDAO rutaDAO = new RutaDAO();
        int idMiSucursal = usuarioActivo.getIdSucursalAsignada();

        try {
            if ("crear".equals(accion) || "editar".equals(accion)) {
                int idDestino = Integer.parseInt(request.getParameter("id_destino"));
                double distancia = Double.parseDouble(request.getParameter("distancia_km"));
                double precio = Double.parseDouble(request.getParameter("precio"));

                if (idMiSucursal == idDestino) {
                    request.getSession().setAttribute("error", "El origen y el destino no pueden ser la misma sucursal.");
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Rutas");
                    return;
                }

                Ruta ruta = new Ruta();
                ruta.setIdOrigen(idMiSucursal);
                ruta.setIdDestino(idDestino);
                ruta.setDistanciaKm(distancia);
                ruta.setPrecio(precio);

                if ("crear".equals(accion)) {
                    if (rutaDAO.existeRuta(idMiSucursal, idDestino)) {
                        request.getSession().setAttribute("error", "Denegado: Ya existe una ruta comercial hacia esa sucursal.");
                        response.sendRedirect(request.getContextPath() + "/Gestionar_Rutas");
                        return;
                    }
                    rutaDAO.agregarRuta(ruta);
                    request.getSession().setAttribute("mensajeExito", "Ruta registrada exitosamente.");
                } else {
                    ruta.setIdRuta(Integer.parseInt(request.getParameter("id_ruta")));
                    rutaDAO.actualizarRuta(ruta);
                    request.getSession().setAttribute("mensajeExito", "Ruta actualizada.");
                }
            } else if ("eliminar".equals(accion)) {
                int idRuta = Integer.parseInt(request.getParameter("id_ruta"));
                rutaDAO.eliminarRuta(idRuta);
                request.getSession().setAttribute("mensajeExito", "Ruta eliminada permanentemente.");
            }
        } catch (BDException | NumberFormatException e) {
            request.getSession().setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Rutas");
    }
}
