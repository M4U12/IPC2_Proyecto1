package controladoresReportesSucursal;

import dao.ReportesDAO;
import dao.BusDAO;
import dao.RutaDAO;
import excepciones.BDException;
import modelosReportesSucursal.ReporteIngresoBoleto;
import modelos.Bus;
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

@WebServlet(name = "ReporteIngresoBoletoServlet", urlPatterns = {"/Reporte_Ingreso_Boletos_Sucursal"})
public class ReporteIngresoBoletoServlet extends HttpServlet {

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
        String rutaParam = request.getParameter("id_ruta");
        String busParam = request.getParameter("id_bus");

        Integer idRuta = (rutaParam != null && !rutaParam.isEmpty()) ? Integer.parseInt(rutaParam) : null;
        Integer idBus = (busParam != null && !busParam.isEmpty()) ? Integer.parseInt(busParam) : null;

        request.setAttribute("fechaInicio", fechaInicio != null ? fechaInicio : "");
        request.setAttribute("fechaFin", fechaFin != null ? fechaFin : "");
        request.setAttribute("idRutaSeleccionada", idRuta);
        request.setAttribute("idBusSeleccionado", idBus);

        try {
            List<Bus> listaBuses = new BusDAO().listarBusesPorSucursal(idMiSucursal, false);
            request.setAttribute("listaBuses", listaBuses);
            request.setAttribute("listaRutas", new RutaDAO().listarRutasPorSucursal(idMiSucursal));
            List<ReporteIngresoBoleto> reporte = new ReportesDAO().reporteIngresosBoletos(idMiSucursal, fechaInicio, fechaFin, idRuta, idBus);
            String nombreSucursal = new ReportesDAO().obtenerNombreSucursal(idMiSucursal);
            request.setAttribute("nombreSucursal", nombreSucursal);
            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, reporte, fechaInicio, fechaFin, nombreSucursal);
                return;
            }

            double totalIngresos = 0;
            int totalBoletos = 0;
            for (ReporteIngresoBoleto r : reporte) {
                totalIngresos += r.getIngresoTotal();
                totalBoletos += r.getCantidadBoletos();
            }

            request.setAttribute("nombreSucursal", new ReportesDAO().obtenerNombreSucursal(idMiSucursal));
            request.setAttribute("reporte", reporte);
            request.setAttribute("granTotalIngresos", totalIngresos);
            request.setAttribute("totalBoletos", totalBoletos);
            
        } catch (BDException | IOException e) {
            request.setAttribute("error", "Error al procesar el reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/reporte_ingresos_boletos.jsp").forward(request, response);
    }

    private void exportarHTML(HttpServletResponse response, List<ReporteIngresoBoleto> reporte, String inicio, String fin, String nombreSucursal) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Ingresos_Boletos.html\"");

        String periodo = (inicio == null || inicio.isEmpty()) ? "Historial Completo" : inicio + " al " + fin;

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Ingresos por Boletos</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #198754; border-bottom: 2px solid #198754; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println("</style></head><body>");

            out.println("<h1>Reporte de Ingresos por Boletos</h1>");
            out.println("<p><strong>Sucursal:</strong> " + nombreSucursal + "</p>");
            out.println("<p><strong>Período:</strong> " + periodo + "</p>");

            out.println("<table><thead><tr><th>ID Viaje</th><th>Ruta</th><th>Fecha de Salida</th><th>Boletos Vendidos</th><th>Ingreso Generado (Q)</th></tr></thead><tbody>");

            double gTotal = 0;
            int gBoletos = 0;

            for (ReporteIngresoBoleto r : reporte) {
                out.println("<tr>");
                out.println("<td><strong>" + r.getIdViaje() + "</strong></td>");
                out.println("<td>" + r.getRuta() + "</td>");
                out.println("<td>" + r.getFechaViaje() + "</td>");
                out.println("<td>" + r.getCantidadBoletos() + "</td>");
                out.println("<td style='color: #198754; font-weight: bold;'>" + String.format("%.2f", r.getIngresoTotal()) + "</td>");
                out.println("</tr>");
                gTotal += r.getIngresoTotal();
                gBoletos += r.getCantidadBoletos();
            }
            
            out.println("</tbody><tfoot><tr style='background-color: #f8f9fa; font-weight: bold;'>");
            out.println("<td colspan='3' style='text-align: right;'>TOTALES:</td>");
            out.println("<td>" + gBoletos + " boletos</td>");
            out.println("<td style='color: #198754;'>Q." + String.format("%.2f", gTotal) + "</td>");
            out.println("</tr></tfoot></table></body></html>");
        }
    }
}