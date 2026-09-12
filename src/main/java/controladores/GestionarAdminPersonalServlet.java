/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controladores;

import dao.AdminSucursalDAO;
import dao.SucursalDAO;
import dao.UsuarioDAO;
import excepciones.BDException;
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
import java.util.Optional;
import modelos.Sucursal;

/**
 *
 * @author ACER
 */
@WebServlet(name = "GestionarAdminPersonalServlet", urlPatterns = {"/Gestionar_Admin_Sucursal"})
public class GestionarAdminPersonalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        AdminSucursalDAO adminDAO = new AdminSucursalDAO();
        SucursalDAO sucursalDAO = new SucursalDAO();

        try {
            request.setAttribute("listaAdmins", adminDAO.listarTodosAdmins("Todos"));
            request.setAttribute("listaSucursales", sucursalDAO.listarSucursales());
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar los datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/gestion_admins.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");

        try {
            if ("cambiarEstado".equals(accion)) {
                procesarCambioEstado(request);
            } else if ("reasignar".equals(accion)) {
                procesarReasignacion(request);
            } else if ("editar".equals(accion)) {
                procesarEdicion(request);
            }
        } catch (BDException e) {
            request.getSession().setAttribute("error", "Error en el sistema: " + e.getMessage());
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Se detectó un error en los datos numéricos enviados.");
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Admin_Sucursal");
    }

    private void procesarCambioEstado(HttpServletRequest request) throws BDException {
        int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
        int idSucursalActual = Integer.parseInt(request.getParameter("id_sucursal_actual"));
        boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("nuevo_estado"));

        AdminSucursalDAO adminDAO = new AdminSucursalDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // valida que la sucursal actual no se quede sin administradores activos al dar de baja
        if (!nuevoEstado && idSucursalActual > 0) {
            List<Usuario> activos = adminDAO.listarAdminPorSucursalActivos(idSucursalActual);
            if (activos.size() <= 1) {
                request.getSession().setAttribute("error", "Denegado: La sucursal no puede quedarse sin administradores activos.");
                return; 
            }
        }

        usuarioDAO.cambiarEstadoUsuario(idUsuario, nuevoEstado);
        request.getSession().setAttribute("mensajeExito", "Estado actualizado correctamente.");
    }

    private void procesarReasignacion(HttpServletRequest request) throws BDException {
        int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
        int idSucursalActual = Integer.parseInt(request.getParameter("id_sucursal_actual"));
        int idNuevaSucursal = Integer.parseInt(request.getParameter("id_nueva_sucursal"));

        AdminSucursalDAO adminDAO = new AdminSucursalDAO();

        if (idSucursalActual == 0) {
            // asignación inicial
            Sucursal sucTemp = new Sucursal();
            sucTemp.setIdSucursal(idNuevaSucursal);
            Usuario usuTemp = new Usuario();
            usuTemp.setIdUsuario(idUsuario);

            adminDAO.agregarAdminASucursal(usuTemp, sucTemp);
            request.getSession().setAttribute("mensajeExito", "Administrador asignado a la sucursal con éxito.");

        } else if (idSucursalActual != idNuevaSucursal) {
            List<Usuario> activos = adminDAO.listarAdminPorSucursalActivos(idSucursalActual);
            if (activos.size() <= 1) {
                request.getSession().setAttribute("error", "Denegado: No puedes trasladar al último administrador activo de su sede actual.");
                return;
            }

            adminDAO.actualizarSucursalDeAdmin(idUsuario, idNuevaSucursal);
            request.getSession().setAttribute("mensajeExito", "Administrador trasladado con éxito.");
        }
    }

    private void procesarEdicion(HttpServletRequest request) throws BDException {
        String nombre = request.getParameter("nombre");
        String dpi = request.getParameter("dpi");
        String nit = request.getParameter("nit");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");

        String dpiActual = request.getParameter("dpi_actual");
        String nitActual = request.getParameter("nit_actual");
        String telefonoActual = request.getParameter("telefono_actual");

        String errorFormato = validarFormatos(nombre, dpi, nit, telefono);
        if (errorFormato != null) {
            request.getSession().setAttribute("error", errorFormato);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if (!dpi.equals(dpiActual) && usuarioDAO.existeDpi(dpi)) {
            request.getSession().setAttribute("error", "El DPI ya está en uso.");
            return;
        }
        if (!nit.equals(nitActual) && usuarioDAO.existeNit(nit)) {
            request.getSession().setAttribute("error", "El NIT ya está en uso.");
            return;
        }
        if (!telefono.equals(telefonoActual) && usuarioDAO.existeTelefono(telefono)) {
            request.getSession().setAttribute("error", "El teléfono ya está en uso.");
            return;
        }

        Optional<Usuario> adminOpt = usuarioDAO.buscarPorDpi(dpiActual);
        if (adminOpt.isPresent()) {
            Usuario admin = adminOpt.get();
            admin.setNombre(nombre);
            admin.setDpi(dpi);
            admin.setNit(nit);
            admin.setTelefono(telefono);
            admin.setDireccion(direccion);

            usuarioDAO.actualizarUsuario(admin);
            request.getSession().setAttribute("mensajeExito", "Datos del administrador actualizados.");
        }
    }

    private String validarFormatos(String nombre, String dpi, String nit, String telefono) {
        if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            return "El nombre contiene caracteres inválidos.";
        }
        if (dpi == null || !dpi.matches("\\d{13}")) {
            return "El DPI debe contener 13 números.";
        }
        if (nit == null || !nit.matches("\\d{13}")) {
            return "El NIT debe contener 13 números.";
        }
        if (telefono == null || !telefono.matches("\\d{8}")) {
            return "El teléfono debe contener 8 números.";
        }
        return null;
    }
}
