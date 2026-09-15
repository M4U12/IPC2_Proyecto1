package controladores;

import dao.ReportesDAO;
import dao.SucursalDAO;
import modelos.ReporteGanancias;
import modelos.Sucursal;
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

@WebServlet(name = "ReporteGananciasServlet", urlPatterns = {"/Reporte_Ganancias"})
public class ReporteGananciasServlet extends HttpServlet {

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

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SISTEMA) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String fechaInicio = request.getParameter("fecha_inicio");
        String fechaFin = request.getParameter("fecha_fin");
        String idSucursalStr = request.getParameter("id_sucursal");

        if (fechaInicio == null) {
            fechaInicio = "";
        }
        if (fechaFin == null) {
            fechaFin = "";
        }
        String inicio = fechaInicio.isEmpty() ? "2000-01-01" : fechaInicio;
        String fin = fechaFin.isEmpty() ? "2100-12-31" : fechaFin;

        int idSucursal = 0;
        if (idSucursalStr != null && !idSucursalStr.isEmpty()) {
            idSucursal = Integer.parseInt(idSucursalStr);
        }

        request.setAttribute("fechaInicio", fechaInicio);
        request.setAttribute("fechaFin", fechaFin);
        request.setAttribute("idSucursalSeleccionada", idSucursal);

        try {
            List<Sucursal> listaSucursales = new SucursalDAO().listarSucursales();
            List<ReporteGanancias> reporte = new ReportesDAO().generarReporteFinanciero(inicio, fin, idSucursal, true);

            double totalIngresos = 0;
            double totalCostos = 0;
            double gananciaNeta = 0;

            for (ReporteGanancias rep : reporte) {
                totalIngresos += rep.getTotalIngresos();
                totalCostos += rep.getTotalCostos();
                gananciaNeta += rep.getGananciaNeta();
            }

            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, inicio, fin, reporte, totalIngresos, totalCostos, gananciaNeta);
                return; // para no recargar la pagina
            }

            request.setAttribute("listaSucursales", listaSucursales);
            request.setAttribute("reporte", reporte);
            request.setAttribute("granTotalIngresos", totalIngresos);
            request.setAttribute("granTotalCostos", totalCostos);
            request.setAttribute("granGananciaNeta", gananciaNeta);

        } catch (Exception e) {
            request.setAttribute("error", "Ocurrió un error al generar el reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/reporte_ganancias.jsp").forward(request, response);
    }


    private void exportarHTML(HttpServletResponse response, String inicio, String fin, List<ReporteGanancias> reporte, double totalIngresos, double totalCostos, double gananciaNeta) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Ganancias.html\"");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
            out.println("<title>Reporte de Ganancias</title>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #0d6efd; border-bottom: 2px solid #0d6efd; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 14px; }");
            out.println("th, td { border: 1px solid #dee2e6; padding: 12px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println(".sub-th { background-color: #f8f9fa; color: #212529; font-weight: bold; }");
            out.println(".text-success { color: #198754; font-weight: bold; }");
            out.println(".text-danger { color: #dc3545; font-weight: bold; }");
            out.println(".text-primary { color: #0d6efd; font-weight: bold; }");
            out.println("</style></head><body>");

            out.println("<h1>Reporte de Ganancias - Code 'n Buses</h1>");
            out.println("<p><strong>Período Evaluado:</strong> " + (inicio.equals("2000-01-01") ? "Historial Completo" : inicio + " al " + fin) + "</p>");

            out.println("<table><thead>");
            out.println("<tr>");
            out.println("<th rowspan='2'>Sucursal</th>");
            out.println("<th colspan='3' style='background-color: #198754;'>Ingresos</th>");
            out.println("<th colspan='5' style='background-color: #dc3545;'>Costos Operativos</th>");
            out.println("<th rowspan='2' style='background-color: #0d6efd;'>Ganancia Neta</th>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<th class='sub-th'>Boletos (Q)</th><th class='sub-th'>Privados (Q)</th><th class='sub-th'>Subtotal (Q)</th>");
            out.println("<th class='sub-th'>Combustible (Q)</th><th class='sub-th'>Mano Obra (Q)</th><th class='sub-th'>Repuestos (Q)</th><th class='sub-th'>Depreciación (Q)</th><th class='sub-th'>Subtotal (Q)</th>");
            out.println("</tr></thead><tbody>");

            for (ReporteGanancias r : reporte) {
                out.println("<tr>");
                out.println("<td style='text-align: left; font-weight: bold;'>" + r.getNombreSucursal() + "</td>");

                out.println("<td>" + String.format("%.2f", r.getIngresosBoletos()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getIngresosPrivados()) + "</td>");
                out.println("<td class='text-success'>" + String.format("%.2f", r.getTotalIngresos()) + "</td>");

                out.println("<td>" + String.format("%.2f", r.getCostoCombustible()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getCostoManoObraTaller()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getCostoRepuestosTaller()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getCostoDepreciacion()) + "</td>");
                out.println("<td class='text-danger'>" + String.format("%.2f", r.getTotalCostos()) + "</td>");

                String colorGanancia = r.getGananciaNeta() >= 0 ? "text-primary" : "text-danger";
                out.println("<td class='" + colorGanancia + "'>" + String.format("%.2f", r.getGananciaNeta()) + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");

            out.println("<h2 style='margin-top: 40px; border-bottom: 1px solid #ccc; padding-bottom: 10px;'>Resumen Global</h2>");
            out.println("<p><strong>Gran Total Ingresos:</strong> Q." + String.format("%.2f", totalIngresos) + "</p>");
            out.println("<p><strong>Gran Total Costos:</strong> Q." + String.format("%.2f", totalCostos) + "</p>");

            String colorTotal = gananciaNeta >= 0 ? "#0d6efd" : "#dc3545";
            out.println("<h3 style='color: " + colorTotal + ";'>Gran Ganancia Neta: Q." + String.format("%.2f", gananciaNeta) + "</h3>");

            out.println("</body></html>");
        }
    }
}
