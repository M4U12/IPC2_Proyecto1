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

@WebServlet(name = "ReporteCostosServlet", urlPatterns = {"/Reporte_Costos"})
public class ReporteCostosServlet extends HttpServlet {

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
        request.setAttribute("idSucursalSeleccionada", 0);

        try {
            List<Sucursal> listaSucursales = new SucursalDAO().listarSucursales();
            List<ReporteGanancias> reporte = new ReportesDAO().generarReporteFinanciero("2000-01-01", "2100-12-31", 0, false);

            calcularTotalesYMandar(request, reporte, listaSucursales);
        } catch (Exception e) {
            request.setAttribute("error", "Error al generar reporte: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/reporte_costos.jsp").forward(request, response);
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

            List<ReporteGanancias> reporte = new ReportesDAO().generarReporteFinanciero(inicio, fin, idSucursal, false);

            String accion = request.getParameter("accion");
            if ("exportar_html".equals(accion)) {
                exportarHTML(response, inicio, fin, reporte);
                return;
            }

            calcularTotalesYMandar(request, reporte, listaSucursales);

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSistema/reporte_costos.jsp").forward(request, response);
    }


    private void calcularTotalesYMandar(HttpServletRequest request, List<ReporteGanancias> reporte, List<Sucursal> listaSucursales) {
        double combustible = 0, manoObra = 0, repuestos = 0, depreciacion = 0, total = 0;

        for (ReporteGanancias rep : reporte) {
            combustible += rep.getCostoCombustible();
            manoObra += rep.getCostoManoObraTaller();
            repuestos += rep.getCostoRepuestosTaller();
            depreciacion += rep.getCostoDepreciacion();
            total += rep.getTotalCostos();
        }

        request.setAttribute("listaSucursales", listaSucursales);
        request.setAttribute("reporte", reporte);
        request.setAttribute("granCombustible", combustible);
        request.setAttribute("granManoObra", manoObra);
        request.setAttribute("granRepuestos", repuestos);
        request.setAttribute("granDepreciacion", depreciacion);
        request.setAttribute("granTotal", total);
    }

    private void exportarHTML(HttpServletResponse response, String inicio, String fin, List<ReporteGanancias> reporte) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Costos_Operativos.html\"");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Costos Operativos</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; color: #333; }");
            out.println("h1 { color: #dc3545; border-bottom: 2px solid #dc3545; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #212529; color: white; }");
            out.println(".text-danger { color: #dc3545; font-weight: bold; }");
            out.println("</style></head><body>");

            out.println("<h1>Reporte de Costos Operativos</h1>");
            out.println("<p><strong>Período:</strong> " + (inicio.equals("2000-01-01") ? "Historial Completo" : inicio + " al " + fin) + "</p>");

            out.println("<table><thead><tr><th>Sucursal</th><th>Combustible (Q)</th><th>Mano de Obra (Q)</th><th>Repuestos (Q)</th><th>Depreciación (Q)</th><th>Costo Total (Q)</th></tr></thead><tbody>");

            double gComb = 0, gMano = 0, gRep = 0, gDep = 0, gTot = 0;

            for (ReporteGanancias r : reporte) {
                out.println("<tr>");
                out.println("<td><strong>" + r.getNombreSucursal() + "</strong></td>");
                out.println("<td>" + String.format("%.2f", r.getCostoCombustible()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getCostoManoObraTaller()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getCostoRepuestosTaller()) + "</td>");
                out.println("<td>" + String.format("%.2f", r.getCostoDepreciacion()) + "</td>");
                out.println("<td class='text-danger'>" + String.format("%.2f", r.getTotalCostos()) + "</td>");
                out.println("</tr>");

                gComb += r.getCostoCombustible();
                gMano += r.getCostoManoObraTaller();
                gRep += r.getCostoRepuestosTaller();
                gDep += r.getCostoDepreciacion();
                gTot += r.getTotalCostos();
            }

            out.println("</tbody><tfoot><tr style='background-color: #f8f9fa; font-weight: bold;'>");
            out.println("<td>TOTAL</td>");
            out.println("<td>Q." + String.format("%.2f", gComb) + "</td>");
            out.println("<td>Q." + String.format("%.2f", gMano) + "</td>");
            out.println("<td>Q." + String.format("%.2f", gRep) + "</td>");
            out.println("<td>Q." + String.format("%.2f", gDep) + "</td>");
            out.println("<td class='text-danger'>Q." + String.format("%.2f", gTot) + "</td>");
            out.println("</tr></tfoot></table></body></html>");
        }
    }
}
