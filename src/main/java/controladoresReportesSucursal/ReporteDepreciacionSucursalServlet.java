package controladoresReportesSucursal;

import dao.ReportesDAO;
import modelosReportesSucursal.ReporteDepreciacion;
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

@WebServlet(name = "ReporteDepreciacionSucursalServlet", urlPatterns = {"/Reporte_Depreciacion_Sucursal"})
public class ReporteDepreciacionSucursalServlet extends HttpServlet {

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

        try {
            List<ReporteDepreciacion> reporte = new ReportesDAO().reporteDepreciacion(idMiSucursal);
            String nombreSucursal = new ReportesDAO().obtenerNombreSucursal(idMiSucursal);
            request.setAttribute("nombreSucursal", nombreSucursal);
            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, reporte, nombreSucursal);
                return;
            }

            double granTotalDepreciacion = 0;
            for (ReporteDepreciacion r : reporte) {
                granTotalDepreciacion += r.getDepreciacionTotal();
            }

            request.setAttribute("nombreSucursal", new ReportesDAO().obtenerNombreSucursal(idMiSucursal));
            request.setAttribute("reporte", reporte);
            request.setAttribute("granTotalDepreciacion", granTotalDepreciacion);

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar el reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/reporte_depreciacion.jsp").forward(request, response);
    }

    private void exportarHTML(HttpServletResponse response, List<ReporteDepreciacion> reporte, String nombreSucursal) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Depreciacion_Buses.html\"");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Depreciación</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #dc3545; border-bottom: 2px solid #dc3545; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 12px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println(".text-danger { color: #dc3545; font-weight: bold; }");
            out.println("</style></head><body>");

            out.println("<h1>Reporte de Depreciación por Bus</h1>");
            out.println("<p><strong>Sucursal:</strong> " + nombreSucursal + "</p>");

            out.println("<table><thead><tr><th>No. de Placa</th><th>Kilómetros Recorridos</th><th>Tarifa por Km (Q)</th><th>Depreciación Acumulada (Q)</th></tr></thead><tbody>");

            double totalGlobal = 0;
            for (ReporteDepreciacion r : reporte) {
                out.println("<tr>");
                out.println("<td><strong>" + r.getPlaca() + "</strong></td>");
                out.println("<td>" + String.format("%.2f", r.getKilometrosRecorridos()) + " km</td>");
                out.println("<td>" + String.format("%.2f", r.getDepreciacionPorKm()) + "</td>");
                out.println("<td class='text-danger'>" + String.format("%.2f", r.getDepreciacionTotal()) + "</td>");
                out.println("</tr>");
                totalGlobal += r.getDepreciacionTotal();
            }

            out.println("</tbody><tfoot><tr style='background-color: #f8f9fa; font-size: 1.1em;'>");
            out.println("<td colspan='3' style='text-align: right; font-weight: bold;'>TOTAL:</td>");
            out.println("<td class='text-danger'>Q." + String.format("%.2f", totalGlobal) + "</td>");
            out.println("</tr></tfoot></table></body></html>");
        }
    }
}
