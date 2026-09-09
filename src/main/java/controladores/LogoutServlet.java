package controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // recuperar la sesión actual, el parámetro false evita crear una nueva si ya no existe
        HttpSession sesion = request.getSession(false);
        
        //si existe una sesión activa, se destruye por completo
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect("index.jsp");
    }
}
