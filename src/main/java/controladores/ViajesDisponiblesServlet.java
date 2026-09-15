package controladores;

import dao.ViajeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ViajesDisponiblesServlet", urlPatterns = {"/Viajes_Disponibles"})
public class ViajesDisponiblesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("listaViajes", new ViajeDAO().listarViajesDisponiblesConDetalle());
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo cargar la cartelera de viajes en este momento.");
        }     
        request.getRequestDispatcher("/PaginasUsuarios/viajes_disponibles.jsp").forward(request, response);
    }
}