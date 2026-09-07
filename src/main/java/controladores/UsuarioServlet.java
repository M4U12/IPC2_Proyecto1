package controladores;

import dao.UsuarioDAO;
import excepciones.BDException;
import modelos.Usuario;
import modelos.Enums;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("registro.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String accion = request.getParameter("accion");
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if ("crearCliente".equals(accion)) {

            String dpi = request.getParameter("dpi");
            String nit = request.getParameter("nit");
            String telefono = request.getParameter("telefono");

            // validaciones estrictas con regex solo numeros, sin signos, longitud exacta)
            if (dpi == null || !dpi.matches("\\d{13}")) {
                request.setAttribute("error", "El DPI debe contener exactamente 13 números (sin guiones ni signos).");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }
            if (nit == null || !nit.matches("\\d{13}")) {
                request.setAttribute("error", "El NIT debe contener exactamente 13 números.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }
            if (telefono == null || !telefono.matches("\\d{8}")) {
                request.setAttribute("error", "El teléfono debe contener exactamente 8 números.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            String nombre = request.getParameter("nombre");

            // validacion estricta para el nombre
            if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                request.setAttribute("error", "El nombre contiene caracteres inválidos. Solo se permiten letras y espacios.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            try {

                if (usuarioDAO.existeDpi(dpi)) {
                    request.setAttribute("error", "El DPI ingresado ya está registrado en otra cuenta.");
                    request.getRequestDispatcher("registro.jsp").forward(request, response);
                    return;
                }
                if (usuarioDAO.existeNit(nit)) {
                    request.setAttribute("error", "El NIT ingresado ya se encuentra registrado.");
                    request.getRequestDispatcher("registro.jsp").forward(request, response);
                    return;
                }
                if (usuarioDAO.existeTelefono(telefono)) {
                    request.setAttribute("error", "El número de teléfono ya está asociado a otra cuenta.");
                    request.getRequestDispatcher("registro.jsp").forward(request, response);
                    return;
                }

                Usuario nuevoCliente = new Usuario();
                nuevoCliente.setDpi(dpi);
                nuevoCliente.setNit(nit);
                nuevoCliente.setPassword(request.getParameter("password"));
                nuevoCliente.setNombre(request.getParameter("nombre"));
                nuevoCliente.setTelefono(request.getParameter("telefono"));
                nuevoCliente.setDireccion(request.getParameter("direccion"));
                nuevoCliente.setEstado(true);
                nuevoCliente.setRol(Enums.RolUsuario.CLIENTE);
                //mandar a la bd
                usuarioDAO.crearUsuario(nuevoCliente);

                request.setAttribute("mensaje", "Cuenta creada exitosamente. Ya puedes iniciar sesión.");
                request.getRequestDispatcher("index.jsp").forward(request, response);

            } catch (BDException e) {
                request.setAttribute("error", "Error al registrar: Ya existe un usuario con ese DPI/NIT/Numero de teléfono");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
            }
        }
    }
}
