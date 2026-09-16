package controladoresReportesSucursal;

import dao.ReportesDAO;
import modelosReportesSucursal.ReporteIngresoAlquiler;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ReporteIngresoAlquilerServlet", urlPatterns = {"/Reporte_Ingreso_Alquiler_Sucursal"})
public class ReporteIngresoAlquilerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        procesarSolicitud(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        procesarSolicitud(request, response);
    }

    private void procesarSolicitud(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        int idMiSucursal = usuarioActivo.getIdSucursalAsignada();

        String fechaInicio = request.getParameter("fecha_inicio");
        String fechaFin = request.getParameter("fecha_fin");

        request.setAttribute("fechaInicio", fechaInicio != null ? fechaInicio : "");
        request.setAttribute("fechaFin", fechaFin != null ? fechaFin : "");

        try {
            List<ReporteIngresoAlquiler> reporte = new ReportesDAO().reporteIngresosAlquiler(idMiSucursal, fechaInicio, fechaFin);
            String nombreSucursal = new ReportesDAO().obtenerNombreSucursal(idMiSucursal);
            request.setAttribute("nombreSucursal", nombreSucursal);
            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, reporte, fechaInicio, fechaFin, nombreSucursal);
                return;
            }

            double granTotalAlquileres = 0;
            for (ReporteIngresoAlquiler r : reporte) {
                granTotalAlquileres += r.getPrecioTotal();
            }

            request.setAttribute("nombreSucursal", new ReportesDAO().obtenerNombreSucursal(idMiSucursal));
            request.setAttribute("reporte", reporte);
            request.setAttribute("granTotalAlquileres", granTotalAlquileres);

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar el reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/reporte_ingresos_alquiler.jsp").forward(request, response);
    }

    private void exportarHTML(HttpServletResponse response, List<ReporteIngresoAlquiler> reporte, String inicio, String fin, String nombreSucursal) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Ingresos_Alquiler.html\"");

        String periodo = (inicio == null || inicio.isEmpty()) ? "Historial Completo" : inicio + " al " + fin;

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Ingresos por Alquileres</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #fd7e14; border-bottom: 2px solid #fd7e14; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println("</style></head><body>");

            out.println("<h1>Reporte de Ingresos por Alquileres Privados</h1>");
            out.println("<p><strong>Sucursal:</strong> " + nombreSucursal + "</p>");
            out.println("<p><strong>Período:</strong> " + periodo + "</p>");

            out.println("<table><thead><tr><th>Cliente</th><th>Origen</th><th>Destino</th><th>Fecha de Salida</th><th>Placa Asignada</th><th>Precio (Q)</th></tr></thead><tbody>");

            double gTotal = 0;

            for (ReporteIngresoAlquiler r : reporte) {
                out.println("<tr>");
                out.println("<td><strong>" + r.getCliente() + "</strong></td>");
                out.println("<td>" + r.getOrigen() + "</td>");
                out.println("<td>" + r.getDestino() + "</td>");
                out.println("<td>" + r.getFechaSalida() + "</td>");
                out.println("<td>" + r.getPlacaBus() + "</td>");
                out.println("<td style='color: #fd7e14; font-weight: bold;'>" + String.format("%.2f", r.getPrecioTotal()) + "</td>");
                out.println("</tr>");
                gTotal += r.getPrecioTotal();
            }

            out.println("</tbody><tfoot><tr style='background-color: #f8f9fa; font-weight: bold;'>");
            out.println("<td colspan='5' style='text-align: right;'>TOTAL:</td>");
            out.println("<td style='color: #fd7e14; font-size: 1.1em;'>Q." + String.format("%.2f", gTotal) + "</td>");
            out.println("</tr></tfoot></table></body></html>");
        }
    }
}
