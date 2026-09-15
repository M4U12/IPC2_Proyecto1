package controladores;

import dao.ReportesDAO;
import modelos.ReporteRutasDemanda;
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

@WebServlet(name = "ReporteRutasServlet", urlPatterns = {"/Reporte_Rutas"})
public class ReporteRutasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        request.setAttribute("fechaInicio", "");
        request.setAttribute("fechaFin", "");

        try {
            List<ReporteRutasDemanda> reporte = new ReportesDAO().generarReporteRutasDemandadas("2000-01-01", "2100-12-31");
            request.setAttribute("reporte", reporte);
        } catch (Exception e) {
            request.setAttribute("error", "Error al generar reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/reporte_rutas.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String fechaInicio = request.getParameter("fecha_inicio");
        String fechaFin = request.getParameter("fecha_fin");

        if (fechaInicio == null) {
            fechaInicio = "";
        }
        if (fechaFin == null) {
            fechaFin = "";
        }

        String inicio = fechaInicio.isEmpty() ? "2000-01-01" : fechaInicio;
        String fin = fechaFin.isEmpty() ? "2100-12-31" : fechaFin;

        request.setAttribute("fechaInicio", fechaInicio);
        request.setAttribute("fechaFin", fechaFin);

        try {
            List<ReporteRutasDemanda> reporte = new ReportesDAO().generarReporteRutasDemandadas(inicio, fin);
            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, inicio, fin, reporte);
                return; 
            }

            request.setAttribute("reporte", reporte);

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/reporte_rutas.jsp").forward(request, response);
    }

    private void exportarHTML(HttpServletResponse response, String inicio, String fin, List<ReporteRutasDemanda> reporte) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Rutas_Demandadas.html\"");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Rutas Más Demandadas</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #0d6efd; border-bottom: 2px solid #0d6efd; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println("</style></head><body>");

            out.println("<h1>Reporte de Rutas Más Demandadas</h1>");
            out.println("<p><strong>Período:</strong> " + (inicio.equals("2000-01-01") ? "Historial Completo" : inicio + " al " + fin) + "</p>");

            out.println("<table><thead><tr><th>Posición</th><th>Sucursal Origen</th><th>Sucursal Destino</th><th>Tarifa (Q)</th><th>Boletos Vendidos</th></tr></thead><tbody>");

            int posicion = 1;
            for (ReporteRutasDemanda r : reporte) {
                out.println("<tr>");
                out.println("<td><strong>#" + posicion + "</strong></td>");
                out.println("<td>" + r.getOrigen() + "</td>");
                out.println("<td>" + r.getDestino() + "</td>");
                out.println("<td>Q." + String.format("%.2f", r.getPrecio()) + "</td>");
                out.println("<td style='font-size: 1.2em; font-weight: bold; color: #198754;'>" + r.getTotalBoletosVendidos() + "</td>");
                out.println("</tr>");
                posicion++;
            }
            out.println("</tbody></table></body></html>");
        }
    }
}
