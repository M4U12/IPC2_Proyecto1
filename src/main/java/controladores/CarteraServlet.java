package controladores;

import dao.CarteraDAO;
import dao.TransaccionDAO;
import excepciones.BDException;
import modelos.Cartera;
import modelos.Transaccion;
import modelos.Usuario;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CarteraServlet", urlPatterns = {"/Mi_Cartera"})
public class CarteraServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            CarteraDAO carteraDAO = new CarteraDAO();
            TransaccionDAO transaccionDAO = new TransaccionDAO();

            Optional<Cartera> optCartera = carteraDAO.obtenerCarteraPorUsuario(usuarioActivo.getIdUsuario());
            if (!optCartera.isPresent()) {
                throw new BDException("No se encontró la billetera del usuario.");
            }
            Cartera miCartera = optCartera.get();

            String fechaInicioStr = request.getParameter("fechaInicio");
            String fechaFinStr = request.getParameter("fechaFin");

            LocalDate fechaInicio = (fechaInicioStr != null && !fechaInicioStr.isEmpty()) ? LocalDate.parse(fechaInicioStr) : null;
            LocalDate fechaFin = (fechaFinStr != null && !fechaFinStr.isEmpty()) ? LocalDate.parse(fechaFinStr) : null;

            List<Transaccion> miHistorial = transaccionDAO.listarTransacciones(miCartera.getIdCartera(), fechaInicio, fechaFin);

            request.setAttribute("miCartera", miCartera);
            request.setAttribute("miHistorial", miHistorial);

        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar la cartera: " + e.getMessage());
        } catch (Exception e) {
            request.setAttribute("error", "Formato de fecha inválido.");
        }

        request.getRequestDispatcher("/PaginasUsuarios/cartera.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();
        Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuarioLogueado");

        if ("recargar".equals(accion)) {
            try {
                double monto = Double.parseDouble(request.getParameter("monto"));
                if (monto <= 0) {
                    sesion.setAttribute("error", "El monto de recarga debe ser mayor a Q.0.00");
                } else {
                    CarteraDAO carteraDAO = new CarteraDAO();
                    Optional<Cartera> cartera = carteraDAO.obtenerCarteraPorUsuario(usuarioActivo.getIdUsuario());

                    if (cartera.isPresent()) {
                        carteraDAO.agregarFondosYRegistrar(usuarioActivo.getIdUsuario(), cartera.get().getIdCartera(), monto, "Recarga de fondos desde plataforma");
                        sesion.setAttribute("mensajeExito", "Has recargado Q." + String.format("%.2f", monto) + " a tu billetera!");
                    } else {
                        sesion.setAttribute("error", "Cartera no encontrada.");
                    }
                }
            } catch (NumberFormatException e) {
                sesion.setAttribute("error", "Formato de monto inválido.");
            } catch (BDException e) {
                sesion.setAttribute("error", "Error al procesar el pago: " + e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/Mi_Cartera");
    }
}
