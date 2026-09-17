package controladores;

import dao.AdminSucursalDAO;
import dao.UsuarioDAO;
import excepciones.BDException;
import modelos.Usuario;
import modelos.Enums;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/LoginyRegistro/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dpi = request.getParameter("dpi");
        String password = request.getParameter("password");

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorDpi(dpi);
            //validación
            if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
                Usuario usuario = usuarioOpt.get();

                // verificación de estado de cuenta
                if (!usuario.isEstado()) {
                    request.setAttribute("error", "Su cuenta se encuentra desactivada. Contacte al administrador.");
                    request.getRequestDispatcher("LoginyRegistro/login.jsp").forward(request, response);
                    return;
                }

                Enums.RolUsuario rol = usuario.getRol();
                
                if (rol == Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
                    AdminSucursalDAO adminDAO = new AdminSucursalDAO();
                    int idSucursal = adminDAO.obtenerSucursalDeAdmin(usuario.getIdUsuario());
                    if (idSucursal == 0) {
                        request.setAttribute("error", "Acceso denegado: No tienes ninguna sucursal asignada para operar.");
                        request.getRequestDispatcher("LoginyRegistro/login.jsp").forward(request, response);
                        return;
                    }
                    usuario.setIdSucursalAsignada(idSucursal);
                }
                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuarioLogueado", usuario);
                response.sendRedirect("index.jsp");

            } else {
                request.setAttribute("error", "Credenciales incorrectas. Verifique su DPI y contraseña.");
                request.getRequestDispatcher("LoginyRegistro/login.jsp").forward(request, response);
            }

        } catch (BDException e) {
            request.setAttribute("error", "Error de conexión: " + e.getMessage());
            request.getRequestDispatcher("LoginyRegistro/login.jsp").forward(request, response);
        }
    }
}
