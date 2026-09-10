/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ACER
 */
@WebServlet(name = "AdminPersonalServlet", urlPatterns = {"/Registro_Administrador_Sucursal"})
public class AdminPersonalServlet extends HttpServlet {

@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/LoginyRegistro/login.jsp");
            return; 
        }

        request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if ("crearAdminSucursal".equals(accion)) {
            String dpi = request.getParameter("dpi");
            String nit = request.getParameter("nit");
            String telefono = request.getParameter("telefono");
            String nombre = request.getParameter("nombre");

            // Validaciones estrictas
            if (dpi == null || !dpi.matches("\\d{13}")) {
                request.setAttribute("error", "El DPI debe contener exactamente 13 números.");
                request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                return;
            }
            if (nit == null || !nit.matches("\\d{13}")) {
                request.setAttribute("error", "El NIT debe contener exactamente 13 números.");
                request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                return;
            }
            if (telefono == null || !telefono.matches("\\d{8}")) {
                request.setAttribute("error", "El teléfono debe contener exactamente 8 números.");
                request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                return;
            }
            if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
                request.setAttribute("error", "El nombre contiene caracteres inválidos. Solo letras y espacios.");
                request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                return;
            }
            
            try {
                if (usuarioDAO.existeDpi(dpi)) {
                    request.setAttribute("error", "El DPI ingresado ya se encuentra registrado en el sistema.");
                    request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                    return;
                }
                
                if (usuarioDAO.existeNit(nit)) {
                    request.setAttribute("error", "El NIT ingresado ya se encuentra registrado.");
                    request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                    return;
                }
                if (usuarioDAO.existeTelefono(telefono)) {
                    request.setAttribute("error", "El número de teléfono ya está asociado a otra cuenta.");
                    request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
                    return;
                }
                
                Usuario nuevoAdmin = new Usuario();
                nuevoAdmin.setDpi(dpi);
                nuevoAdmin.setNit(nit);
                nuevoAdmin.setPassword(request.getParameter("password"));
                nuevoAdmin.setNombre(nombre);
                nuevoAdmin.setTelefono(telefono);
                nuevoAdmin.setDireccion(request.getParameter("direccion"));
                nuevoAdmin.setEstado(true);
                nuevoAdmin.setRol(Enums.RolUsuario.ADMINISTRADOR_SUCURSAL);
                
                usuarioDAO.crearUsuario(nuevoAdmin);

                request.getSession().setAttribute("mensajeExito", "Administrador de sucursal creado exitosamente.");
                response.sendRedirect(request.getContextPath() + "/Registro_Administrador_Sucursal");

            } catch (BDException e) {
                request.setAttribute("error", "Hubo un error de conexión con la base de datos.");
                request.getRequestDispatcher("/AdminSistema/crear_admin.jsp").forward(request, response);
            }
        }
    }

}
