package controladoresReportesSucursal;

import dao.ReportesDAO;
import modelosReportesSucursal.*;
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

@WebServlet(name = "ReporteChoferesSucursalServlet", urlPatterns = {"/Reporte_Choferes_Sucursal"})
public class ReporteChoferesSucursalServlet extends HttpServlet {

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
            List<ReporteChofer> reporte = new ReportesDAO().reporteChoferes(idMiSucursal);
            String nombreSucursal = new ReportesDAO().obtenerNombreSucursal(idMiSucursal);
            request.setAttribute("nombreSucursal", nombreSucursal);
            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, reporte, nombreSucursal);
                return;
            }
            request.setAttribute("nombreSucursal", new ReportesDAO().obtenerNombreSucursal(idMiSucursal));
            request.setAttribute("reporte", reporte);
        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar el reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/reporte_choferes.jsp").forward(request, response);
    }

    private void exportarHTML(HttpServletResponse response, List<ReporteChofer> reporte, String nombreSucursal) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Choferes_Sucursal.html\"");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Choferes</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #0d6efd; border-bottom: 2px solid #0d6efd; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println(".activo { color: #198754; font-weight: bold; }");
            out.println(".baja { color: #dc3545; font-weight: bold; }");
            out.println("</style></head><body>");

            out.println("<h1>Listado General de Choferes</h1>");
            out.println("<p><strong>Sucursal:</strong> " + nombreSucursal + "</p>");

            out.println("<table><thead><tr><th>No. Licencia</th><th>Nombre Completo</th><th>Tipo Licencia</th><th>Vencimiento</th><th>Estado</th><th>Total Viajes</th></tr></thead><tbody>");

            for (ReporteChofer r : reporte) {
                String claseEstado = r.getEstado().equals("Activo") ? "activo" : "baja";
                out.println("<tr>");
                out.println("<td><strong>" + r.getLicencia() + "</strong></td>");
                out.println("<td>" + r.getNombre() + "</td>");
                out.println("<td>" + r.getTipoLicencia() + "</td>");
                out.println("<td>" + r.getFechaVencimiento() + "</td>");
                out.println("<td class='" + claseEstado + "'>" + r.getEstado() + "</td>");
                out.println("<td style='font-size: 1.1em; font-weight: bold;'>" + r.getTotalViajes() + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table></body></html>");
        }
    }
}
