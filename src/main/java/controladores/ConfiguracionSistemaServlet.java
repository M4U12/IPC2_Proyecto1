/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controladores;

import dao.ConfiguracionSistemaDAO;
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
@WebServlet(name = "ConfiguracionSistemaServlet", urlPatterns = {"/Parametros"})
public class ConfiguracionSistemaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        ConfiguracionSistemaDAO configDAO = new ConfiguracionSistemaDAO();
        try {
            double depreciacionActual = configDAO.obtenerDepreciacionActual();
            request.setAttribute("depreciacionActual", depreciacionActual);
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/parametros.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            double nuevaDepreciacion = Double.parseDouble(request.getParameter("depreciacion"));

            if (nuevaDepreciacion < 0) {
                request.getSession().setAttribute("error", "La depreciación no puede ser un valor negativo.");
                response.sendRedirect(request.getContextPath() + "/Parametros");
                return;
            }

            ConfiguracionSistemaDAO configDAO = new ConfiguracionSistemaDAO();
            configDAO.guardarConfiguracion(nuevaDepreciacion);

            request.getSession().setAttribute("mensajeExito", "Valor de depreciación actualizado correctamente.");

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Formato de número inválido.");
        } catch (BDException e) {
            request.getSession().setAttribute("error", "Error al guardar: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Parametros");
    }
}
