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

        if ("crear".equals(accion)) {
            String nombre = request.getParameter("nombre");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");
            int idAdmin = Integer.parseInt(request.getParameter("id_admin"));

            if (telefono == null || !telefono.matches("\\d{8}")) {
                request.setAttribute("error", "El teléfono debe contener exactamente 8 números.");
                request.getRequestDispatcher("/AdminSistema/gestion_sucursales.jsp").forward(request, response);
                return;
            }

            SucursalDAO sucursalDAO = new SucursalDAO();

            try {
                // numeros duplicados
                if (sucursalDAO.existeTelefono(telefono)) {
                    request.getSession().setAttribute("error", "No se pudo crear la sucursal. El número de teléfono ya se encuentra registrado.");
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Sucursales");
                    return;
                }

                Sucursal nuevaSucursal = new Sucursal(0, nombre, direccion, telefono);
                boolean exito = sucursalDAO.agregarSucursal(nuevaSucursal, idAdmin);

                if (exito) {
                    request.getSession().setAttribute("mensajeExito", "Sucursal creada y administrador asignado con éxito.");
                } else {
                    request.getSession().setAttribute("error", "Error: No se pudo completar la creación de la sucursal.");
                }
            } catch (BDException e) {
                request.getSession().setAttribute("error", "Error: Hubo un fallo en la base de datos (" + e.getMessage() + ").");
            }
            response.sendRedirect(request.getContextPath() + "/Gestionar_Sucursales");
        }
    }

}
