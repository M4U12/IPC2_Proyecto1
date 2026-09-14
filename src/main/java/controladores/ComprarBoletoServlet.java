package controladores;

import dao.BoletoDAO;
import dao.BusDAO;
import dao.CarteraDAO;
import dao.RutaDAO;
import dao.ViajeDAO;
import excepciones.BDException;
import modelos.Boleto;
import modelos.Bus;
import modelos.Cartera;
import modelos.Ruta;
import modelos.Usuario;
import modelos.Viaje;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelos.Enums;

@WebServlet(name = "ComprarBoletoServlet", urlPatterns = {"/Comprar_Boleto"})
public class ComprarBoletoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario usuarioActivo = (Usuario) request.getSession().getAttribute("usuarioLogueado");
        if (usuarioActivo == null) {
            request.getSession().setAttribute("error", "Debes iniciar sesión para comprar boletos.");
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String idViajeParam = request.getParameter("id_viaje");
        if (idViajeParam == null) {
            response.sendRedirect(request.getContextPath() + "/Viajes_Disponibles");
            return;
        }

        int idViaje = Integer.parseInt(idViajeParam);

        try {
            // obtener viaje buscandolo en la lista de programados
            Viaje viajeActual = null;
            for (Viaje v : new ViajeDAO().listarViajesDisponibles()) {
                if (v.getIdViaje() == idViaje) {
                    viajeActual = v;
                    break;
                }
            }
            if (viajeActual == null) {
                throw new Exception("Viaje no encontrado o no disponible.");
            }

            // obtener precio de ruta
            double precio = 0;
            for (Ruta r : new RutaDAO().listarTodasLasRutas()) {
                if (r.getIdRuta() == viajeActual.getIdRuta()) {
                    precio = r.getPrecio();
                    break;
                }
            }

            // obtener capacidad y asientos ocupados
            Optional<Bus> optBus = new BusDAO().obtenerBusPorId(viajeActual.getIdBus());
            if (optBus.isEmpty()) {
                throw new Exception("El bus asignado a este viaje ya no existe en el sistema.");
            }
            Bus bus = optBus.get();

            List<Integer> asientosOcupados = new BoletoDAO().obtenerAsientosOcupados(idViaje);

            // obtener cartera
            Optional<Cartera> optCartera = new CarteraDAO().obtenerCarteraPorUsuario(usuarioActivo.getIdUsuario());

            request.setAttribute("viaje", viajeActual);
            request.setAttribute("precio", precio);
            request.setAttribute("capacidadBus", bus.getCapacidad());
            request.setAttribute("asientosOcupados", asientosOcupados);
            request.setAttribute("cartera", optCartera.orElse(null));

            request.getRequestDispatcher("/PaginasUsuarios/comprar_boletos.jsp").forward(request, response);

        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error cargando la compra: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Viajes_Disponibles");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario usuarioActivo = (Usuario) request.getSession().getAttribute("usuarioLogueado");
        int idViaje = Integer.parseInt(request.getParameter("id_viaje"));
        int asiento = Integer.parseInt(request.getParameter("numero_asiento"));
        double precio = Double.parseDouble(request.getParameter("precio"));
        int idCartera = Integer.parseInt(request.getParameter("id_cartera"));

        try {
            Boleto nuevoBoleto = new Boleto();
            nuevoBoleto.setIdUsuario(usuarioActivo.getIdUsuario());
            nuevoBoleto.setIdViaje(idViaje);
            nuevoBoleto.setNumeroAsiento(asiento);
            nuevoBoleto.setPrecioPagado(precio);
            nuevoBoleto.setFechaPago(LocalDateTime.now());
            new BoletoDAO().comprarBoletoTransaccional(nuevoBoleto, idCartera, precio, Enums.TipoTransacciones.PAGO_BOLETO.name(), "Compra Asiento #" + asiento + " Viaje #" + idViaje);

            request.getSession().setAttribute("mensajeExito", "Boleto comprado exitosamente. Asiento #" + asiento);
            response.sendRedirect(request.getContextPath() + "/Viajes_Disponibles");

        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Comprar_Boleto?id_viaje=" + idViaje);
        }
    }
}
