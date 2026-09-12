/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controladores;

import dao.AdminSucursalDAO;
import dao.SucursalDAO;
import excepciones.BDException;
import modelos.Sucursal;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import modelos.Usuario;
import jakarta.servlet.http.HttpSession;
import modelos.Enums;

/**
 *
 * @author ACER
 */
@WebServlet(name = "GestionarSucursalesServlet", urlPatterns = {"/Gestionar_Sucursales"})
public class GestionarSucursalesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/LoginyRegistro/login.jsp");
            return;
        }

        SucursalDAO sucursalDAO = new SucursalDAO();
        AdminSucursalDAO adminDAO = new AdminSucursalDAO();

        try {
            List<Sucursal> listaSucursales = sucursalDAO.listarSucursales();
            List<Usuario> adminsDisponibles = adminDAO.listarAdminsDisponibles();

            request.setAttribute("listaSucursales", listaSucursales);
            request.setAttribute("adminsDisponibles", adminsDisponibles);
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/gestion_sucursales.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");

        try {
            if ("crear".equals(accion)) {
                procesarCreacion(request);
            } else if ("editar".equals(accion)) {
                procesarEdicion(request);
            }
        } catch (BDException e) {
            request.getSession().setAttribute("error", "Error en la base de datos: " + e.getMessage());
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Se detectó un error en los datos numéricos enviados.");
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Sucursales");
    }
    private void procesarCreacion(HttpServletRequest request) throws BDException {
        String nombre = request.getParameter("nombre");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        int idAdmin = Integer.parseInt(request.getParameter("id_admin"));

        if (telefono == null || !telefono.matches("\\d{8}")) {
            request.getSession().setAttribute("error", "El teléfono debe contener exactamente 8 números.");
            return;
        }

        SucursalDAO sucursalDAO = new SucursalDAO();

        if (sucursalDAO.existeTelefono(telefono)) {
            request.getSession().setAttribute("error", "No se pudo crear la sucursal. El número de teléfono ya se encuentra registrado.");
            return;
        }

        Sucursal nuevaSucursal = new Sucursal(0, nombre, direccion, telefono);
        boolean exito = sucursalDAO.agregarSucursal(nuevaSucursal, idAdmin);

        if (exito) {
            request.getSession().setAttribute("mensajeExito", "Sucursal creada y administrador asignado con éxito.");
        } else {
            request.getSession().setAttribute("error", "Error: No se pudo completar la creación de la sucursal.");
        }
    }

    private void procesarEdicion(HttpServletRequest request) throws BDException {
        int idSucursal = Integer.parseInt(request.getParameter("id_sucursal"));
        String nombre = request.getParameter("nombre");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String telefonoActual = request.getParameter("telefono_actual");

        if (telefono == null || !telefono.matches("\\d{8}")) {
            request.getSession().setAttribute("error", "El teléfono debe contener exactamente 8 números.");
            return;
        }

        SucursalDAO sucursalDAO = new SucursalDAO();

        if (!telefono.equals(telefonoActual) && sucursalDAO.existeTelefono(telefono)) {
            request.getSession().setAttribute("error", "El número de teléfono ya pertenece a otra sucursal.");
            return;
        }

        Sucursal sucursalModificada = new Sucursal(idSucursal, nombre, direccion, telefono);
        sucursalDAO.actualizarSucursal(sucursalModificada);
        request.getSession().setAttribute("mensajeExito", "Datos de la sucursal actualizados exitosamente.");
    }
}
