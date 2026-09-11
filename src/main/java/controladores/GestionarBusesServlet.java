/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controladores;

import dao.BusDAO;
import dao.ChoferDAO;
import dao.ViajeDAO;
import excepciones.BDException;
import modelos.Bus;
import modelos.Chofer;
import modelos.Usuario;
import modelos.Enums;
import java.io.IOException;
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

/**
 *
 * @author ACER
 */
@WebServlet(name = "GestionarBusesServlet", urlPatterns = {"/Gestionar_Buses"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class GestionarBusesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        BusDAO busDAO = new BusDAO();
        ChoferDAO choferDAO = new ChoferDAO();
        
        try {
            int idMiSucursal = usuarioActivo.getIdSucursalAsignada();
            
            // Se cargan los buses de la sucursal y los choferes activos (para el select de asignación)
            List<Bus> listaBuses = busDAO.listarBusesPorSucursal(idMiSucursal, false);
            List<Chofer> listaChoferesActivos = choferDAO.listarChoferesPorSucursal(idMiSucursal, true);
            
            request.setAttribute("listaBuses", listaBuses);
            request.setAttribute("listaChoferes", listaChoferesActivos);
        } catch (BDException e) {
            request.setAttribute("error", "Error al cargar los datos: " + e.getMessage());
        }

        request.getRequestDispatcher("/AdminSucursal/gestion_buses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuarioActivo = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;

        if (usuarioActivo == null || usuarioActivo.getRol() != Enums.RolUsuario.ADMINISTRADOR_SUCURSAL) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String accion = request.getParameter("accion");
        BusDAO busDAO = new BusDAO();
        int idMiSucursal = usuarioActivo.getIdSucursalAsignada();

        try {
            if ("crear".equals(accion) || "editar".equals(accion)) {
                String placa = request.getParameter("placa").toUpperCase();
                String marca = request.getParameter("marca");
                String modelo = request.getParameter("modelo");
                int anio = Integer.parseInt(request.getParameter("anio_fabricacion"));
                int capacidad = Integer.parseInt(request.getParameter("capacidad"));
                
                if (!placa.matches("^C\\d{3}[A-Z]{3}$")) {
                    request.getSession().setAttribute("error", "La placa debe iniciar con 'C', seguida de 3 números y 3 letras (Ej. C123ABC).");
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Buses");
                    return;
                }
                
                if (!placa.matches("^[A-Z0-9]{7}$")) {
                    request.getSession().setAttribute("error", "La placa debe contener 7 caracteres alfanuméricos.");
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Buses");
                    return;
                }

                String fotoBase64 = "editar".equals(accion) ? request.getParameter("foto_actual") : ""; 
                Part filePart = request.getPart("foto");
                
                if (filePart != null && filePart.getSize() > 0) {
                    byte[] imageBytes = filePart.getInputStream().readAllBytes();
                    fotoBase64 = Base64.getEncoder().encodeToString(imageBytes);
                }

                Bus bus = new Bus();
                bus.setIdSucursal(idMiSucursal);
                bus.setIdChofer(null);
                bus.setPlaca(placa);
                bus.setMarca(marca);
                bus.setModelo(modelo);
                bus.setAnioFabricacion(anio);
                bus.setCapacidad(capacidad);
                bus.setEstadoOperativo("DISPONIBLE");
                bus.setKilometrajeActual("crear".equals(accion) ? 0.0 : Double.parseDouble(request.getParameter("kilometraje_actual")));
                bus.setFoto(fotoBase64);
                bus.setEstado("crear".equals(accion) ? true : Boolean.parseBoolean(request.getParameter("estado_actual")));

                if ("crear".equals(accion)) {
                    busDAO.agregarBus(bus);
                    request.getSession().setAttribute("mensajeExito", "Bus registrado exitosamente.");
                } else {
                    bus.setIdBus(Integer.parseInt(request.getParameter("id_bus")));
                    busDAO.actualizarBus(bus);
                    request.getSession().setAttribute("mensajeExito", "Datos del bus actualizados.");
                }
            } 
            else if ("cambiarEstado".equals(accion)) {
                int idBus = Integer.parseInt(request.getParameter("id_bus"));
                boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("nuevo_estado"));
                
                ViajeDAO viaje = new ViajeDAO();
                if (!nuevoEstado && viaje.tieneViajesActivosPorBus(idBus)) {
                    request.getSession().setAttribute("error", "Denegado: El bus no puede ser dado de baja porque tiene viajes en curso o programados.");
                    response.sendRedirect(request.getContextPath() + "/Gestionar_Buses");
                    return;
                }
                
                busDAO.cambiarEstadoBus(idBus, nuevoEstado);
                request.getSession().setAttribute("mensajeExito", "Estado del vehículo actualizado.");
            }
        } catch (BDException | IllegalArgumentException e) {
            request.getSession().setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/Gestionar_Buses");
    }
}