package controladores;

import dao.ChoferDAO;
import excepciones.BDException;
import modelos.Chofer;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "GestionarChoferesServlet", urlPatterns = {"/Gestionar_Choferes"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class GestionarChoferesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        ChoferDAO choferDAO = new ChoferDAO();
        try {
            int idMiSucursal = usuarioActivo.getIdSucursalAsignada();
            List<Chofer> listaChoferes = choferDAO.listarChoferesPorSucursal(idMiSucursal, false);
            request.setAttribute("listaChoferes", listaChoferes);
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar los choferes: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/choferes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String accion = request.getParameter("accion");
        ChoferDAO choferDAO = new ChoferDAO();
        int idMiSucursal = usuarioActivo.getIdSucursalAsignada();

        try {
            if ("crear".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");
                String numLicencia = request.getParameter("num_licencia");

                String errorValidacion = validarDatosChofer(nombre, telefono, numLicencia);
                if (errorValidacion != null) {
                    request.getSession().setAttribute("error", errorValidacion);
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Choferes");
                    return;
                }

                Part filePart = request.getPart("foto");
                String fotoBase64 = "";
                if (filePart != null && filePart.getSize() > 0) {
                    byte[] imageBytes = filePart.getInputStream().readAllBytes();
                    fotoBase64 = Base64.getEncoder().encodeToString(imageBytes);
                }

                Chofer nuevoChofer = new Chofer();
                nuevoChofer.setIdSucursal(idMiSucursal);
                nuevoChofer.setNombre(nombre);
                nuevoChofer.setNumLicencia(numLicencia);
                nuevoChofer.setTipoLicencia(Enums.TipoLicencia.valueOf(request.getParameter("tipo_licencia")));
                nuevoChofer.setFechaVencimientoLicencia(LocalDate.parse(request.getParameter("fecha_vencimiento")));
                nuevoChofer.setTelefono(telefono);
                nuevoChofer.setSalarioBasePorViaje(Double.parseDouble(request.getParameter("salario_base")));
                nuevoChofer.setFoto(fotoBase64);
                nuevoChofer.setEstadoOperativo(Enums.EstadoOperativo.DISPONIBLE);
                nuevoChofer.setEstado(true);

                choferDAO.agregarChofer(nuevoChofer);
                request.getSession().setAttribute("mensajeExito", "Chofer registrado exitosamente.");

            } else if ("cambiarEstado".equals(accion)) {
                int idChofer = Integer.parseInt(request.getParameter("id_chofer"));
                boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("nuevo_estado"));

                // evitar dar de baja si está en un viaje
                if (!nuevoEstado) {
                    choferDAO.validarChoferLibre(idChofer);
                }

                choferDAO.cambiarEstadoChofer(idChofer, nuevoEstado);
                request.getSession().setAttribute("mensajeExito", "Estado actualizado.");

            } else if ("editar".equals(accion)) {
                int idChofer = Integer.parseInt(request.getParameter("id_chofer"));

                // evitar edición si está en un viaje
                choferDAO.validarChoferLibre(idChofer);

                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");
                String numLicencia = request.getParameter("num_licencia");
                String fotoBase64 = request.getParameter("foto_actual");

                String errorValidacion = validarDatosChofer(nombre, telefono, numLicencia);
                if (errorValidacion != null) {
                    request.getSession().setAttribute("error", errorValidacion);
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Choferes");
                    return;
                }

                Part filePart = request.getPart("foto");
                if (filePart != null && filePart.getSize() > 0) {
                    byte[] imageBytes = filePart.getInputStream().readAllBytes();
                    fotoBase64 = Base64.getEncoder().encodeToString(imageBytes);
                }

                Chofer choferModificado = new Chofer();
                choferModificado.setIdChofer(idChofer);
                choferModificado.setIdSucursal(idMiSucursal);
                choferModificado.setNombre(nombre);
                choferModificado.setNumLicencia(numLicencia);
                choferModificado.setTipoLicencia(Enums.TipoLicencia.valueOf(request.getParameter("tipo_licencia")));
                choferModificado.setFechaVencimientoLicencia(LocalDate.parse(request.getParameter("fecha_vencimiento")));
                choferModificado.setTelefono(telefono);
                choferModificado.setSalarioBasePorViaje(Double.parseDouble(request.getParameter("salario_base")));
                choferModificado.setFoto(fotoBase64);

                choferDAO.actualizarChofer(choferModificado);
                request.getSession().setAttribute("mensajeExito", "Datos del chofer actualizados.");
            }
        } catch (BDException e) {
            request.getSession().setAttribute("error", e.getMessage());
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Choferes");
    }

    private String validarDatosChofer(String nombre, String telefono, String numLicencia) {
        if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            return "El nombre solo debe contener letras y espacios.";
        }
        if (telefono == null || !telefono.matches("\\d{8}")) {
            return "El teléfono debe contener exactamente 8 dígitos numéricos.";
        }
        if (numLicencia == null || !numLicencia.matches("\\d{13}")) {
            return "La licencia debe contener exactamente 13 dígitos numéricos.";
        }
        return null;
    }
}
