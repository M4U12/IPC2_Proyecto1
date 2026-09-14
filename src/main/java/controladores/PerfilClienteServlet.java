package controladores;

import dao.CarteraDAO;
import dao.UsuarioDAO;
import dao.ViajePrivadoDAO;
import excepciones.BDException;
import modelos.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import modelos.Cartera;
import modelos.Enums;

@WebServlet(name = "PerfilClienteServlet", urlPatterns = {"/Mi_Perfil"})
public class PerfilClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            // Datos de Cartera y Privados
            CarteraDAO carteraDAO = new CarteraDAO();
            Optional<Cartera> optCartera = carteraDAO.obtenerCarteraPorUsuario(usuarioActivo.getIdUsuario());
            if (optCartera.isPresent()) {
                request.setAttribute("miCartera", optCartera.get());
            }
            request.setAttribute("listaPrivadosCliente", new ViajePrivadoDAO().listarPorCliente(usuarioActivo.getIdUsuario()));

            // --- para los regulares ---
            request.setAttribute("listaBoletos", new dao.BoletoDAO().listarTodosBoletosPorCliente(usuarioActivo.getIdUsuario()));
            request.setAttribute("listaViajesCliente", new dao.ViajeDAO().listarViajesPorUsuario(usuarioActivo.getIdUsuario()));
            request.setAttribute("listaRutas", new dao.RutaDAO().listarTodasLasRutas());
            request.setAttribute("listaSucursales", new dao.SucursalDAO().listarSucursales());

        } catch (excepciones.BDException e) {
            request.setAttribute("error", "Error al cargar tu perfil: " + e.getMessage());
        }

        request.getRequestDispatcher("/PaginasUsuarios/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();
        Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuarioLogueado");

        if (usuarioActivo != null) {
            try {
                if ("actualizar_perfil".equals(accion)) {
                    usuarioActivo.setNombre(request.getParameter("nombre"));
                    usuarioActivo.setNit(request.getParameter("nit"));
                    usuarioActivo.setTelefono(request.getParameter("telefono"));
                    usuarioActivo.setDireccion(request.getParameter("direccion"));

                    String nuevaPassword = request.getParameter("password");
                    if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
                        usuarioActivo.setPassword(nuevaPassword);
                    }

                    new UsuarioDAO().actualizarUsuario(usuarioActivo);
                    sesion.setAttribute("usuarioLogueado", usuarioActivo);
                    sesion.setAttribute("mensajeExito", "Tus datos personales han sido actualizados.");

                } else if ("pagar_cotizacion".equals(accion)) {
                    int idViajePrivado = Integer.parseInt(request.getParameter("id_viaje_privado"));
                    double montoCobrar = Double.parseDouble(request.getParameter("monto"));

                    CarteraDAO carteraDAO = new CarteraDAO();
                    Optional<Cartera> cartera = carteraDAO.obtenerCarteraPorUsuario(usuarioActivo.getIdUsuario());

                    if (cartera.isPresent()) {
                        carteraDAO.descontarFondosYRegistrar(usuarioActivo.getIdUsuario(), cartera.get().getIdCartera(), montoCobrar, Enums.TipoTransacciones.PAGO_ALQUILER, "Pago Alquiler #" + idViajePrivado);
                        new ViajePrivadoDAO().pagarCotizacion(idViajePrivado);

                        sesion.setAttribute("mensajeExito", "Pago procesado con éxito! El administrador te asignará una unidad muy pronto.");
                    } else {
                        sesion.setAttribute("error", "Error: No se encontró tu billetera.");
                    }
                } else if ("cancelar_solicitud".equals(accion)) {
                    int idViajePrivado = Integer.parseInt(request.getParameter("id_viaje_privado"));
                    new ViajePrivadoDAO().cancelarViajePrivado(idViajePrivado);
                    sesion.setAttribute("mensajeExito", "Has cancelado la solicitud de cotización exitosamente.");
                }
            } catch (BDException e) {
                sesion.setAttribute("error", e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/Mi_Perfil");
    }
}
