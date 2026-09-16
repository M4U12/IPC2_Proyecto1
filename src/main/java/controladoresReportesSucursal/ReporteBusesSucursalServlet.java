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

@WebServlet(name = "ReporteBusesSucursalServlet", urlPatterns = {"/Reporte_Buses_Sucursal"})
public class ReporteBusesSucursalServlet extends HttpServlet {

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
        String estadoParam = request.getParameter("estado_filtro");
        Boolean estadoFiltro = null;

        if (estadoParam != null && !estadoParam.isEmpty()) {
            estadoFiltro = Boolean.parseBoolean(estadoParam);
        }

        request.setAttribute("estadoFiltro", estadoParam);

        try {
            List<ReporteBus> reporte = new ReportesDAO().reporteBuses(idMiSucursal, estadoFiltro);
            String nombreSucursal = new ReportesDAO().obtenerNombreSucursal(idMiSucursal);
            request.setAttribute("nombreSucursal", nombreSucursal);
            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, reporte, estadoFiltro, nombreSucursal);
                return;
            }
            request.setAttribute("nombreSucursal", new ReportesDAO().obtenerNombreSucursal(idMiSucursal));
            request.setAttribute("reporte", reporte);

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar el reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/reporte_buses.jsp").forward(request, response);
    }

    private void exportarHTML(HttpServletResponse response, List<ReporteBus> reporte, Boolean estadoFiltro, String nombreSucursal) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Buses_Sucursal.html\"");

        String estadoTexto = (estadoFiltro == null) ? "Todos" : (estadoFiltro ? "Activos" : "De Baja");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Reporte de Buses</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #0d6efd; border-bottom: 2px solid #0d6efd; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println("</style></head><body>");

            out.println("<h1>Listado General de Buses</h1>");
            out.println("<p><strong>Sucursal:</strong> " + nombreSucursal + "</p>");
            out.println("<p><strong>Filtro de Estado:</strong> " + estadoTexto + "</p>");

            out.println("<table><thead><tr><th>Placa</th><th>Marca</th><th>Modelo</th><th>Capacidad</th><th>Estado Operativo</th><th>Chofer Asignado</th><th>Kilometraje Actual</th><th>Total Viajes</th></tr></thead><tbody>");

            for (ReporteBus r : reporte) {
                out.println("<tr>");
                out.println("<td><strong>" + r.getPlaca() + "</strong></td>");
                out.println("<td>" + r.getMarca() + "</td>");
                out.println("<td>" + r.getModelo() + "</td>");
                out.println("<td>" + r.getCapacidad() + " pasajeros</td>");
                out.println("<td>" + r.getEstadoOperativo() + "</td>");
                out.println("<td>" + r.getNombreChofer() + "</td>");
                out.println("<td>" + r.getKilometrajeActual() + " km</td>");
                out.println("<td style='font-weight: bold;'>" + r.getTotalViajes() + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table></body></html>");
        }
    }
}
