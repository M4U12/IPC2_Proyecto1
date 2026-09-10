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
            // carga todos los admins y todas las sucursales para armar la pantalla
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
        int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
        int idSucursalActual = Integer.parseInt(request.getParameter("id_sucursal_actual"));

        AdminSucursalDAO adminDAO = new AdminSucursalDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try {
            if ("cambiarEstado".equals(accion)) {
                boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("nuevo_estado"));

                // valida solo si se va a desactivar a alguien que ya tiene sucursal
                if (!nuevoEstado && idSucursalActual > 0) {
                    List<Usuario> activos = adminDAO.listarAdminPorSucursalActivos(idSucursalActual);
                    if (activos.size() <= 1) {
                        request.getSession().setAttribute("error", "Denegado: La sucursal actual no puede quedarse sin administradores activos.");
                        response.sendRedirect(request.getContextPath() + "/Gestionar_Admin_Sucursal");
                        return;
                    }
                }

                usuarioDAO.cambiarEstadoUsuario(idUsuario, nuevoEstado);
                request.getSession().setAttribute("mensajeExito", "Estado actualizado correctamente.");
            }
            
            else if ("reasignar".equals(accion)) {
                int idNuevaSucursal = Integer.parseInt(request.getParameter("id_nueva_sucursal"));

                if (idSucursalActual == 0) {
                    // no tenía sucursal, se hace un insert normal
                    Sucursal sucTemp = new Sucursal();
                    sucTemp.setIdSucursal(idNuevaSucursal);
                    Usuario usuTemp = new Usuario();
                    usuTemp.setIdUsuario(idUsuario);
                    
                    adminDAO.agregarAdminASucursal(usuTemp, sucTemp);
                    request.getSession().setAttribute("mensajeExito", "Administrador asignado a la sucursal con éxito.");
                    
                } else if (idSucursalActual != idNuevaSucursal) {
                    // ya tenia sucursal, valida que su sede de origen no quede vacía antes del update
                    List<Usuario> activos = adminDAO.listarAdminPorSucursalActivos(idSucursalActual);
                    if (activos.size() <= 1) {
                        request.getSession().setAttribute("error", "Denegado: No puedes trasladar al último administrador activo de su sede actual.");
                        response.sendRedirect(request.getContextPath() + "/Gestionar_Admin_Sucursal");
                        return;
                    }
                    
                    adminDAO.actualizarSucursalDeAdmin(idUsuario, idNuevaSucursal);
                    request.getSession().setAttribute("mensajeExito", "Administrador trasladado con éxito.");
                }
            }

        } catch (BDException e) {
            request.getSession().setAttribute("error", "Error en el sistema: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Admin_Sucursal");
    }
}
