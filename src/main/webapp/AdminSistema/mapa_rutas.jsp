<%@page import="java.util.List"%>
<%@page import="modelos.Sucursal"%>
<%@page import="modelos.Ruta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mapa de Rutas</title>
        <jsp:include page="/Componentes/recursos.jsp" />
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    </head>
    <body class="bg-light">
        <div class="container-fluid p-0">
            <div class="row g-0">
                <jsp:include page="/Componentes/sidebar.jsp" />

                <div class="col-md-9 col-lg-10 p-5 min-vh-100">
                    <div class="p-4 bg-white rounded-4 shadow-sm mb-4 d-flex justify-content-between align-items-center">
                        <div>
                            <h2 class="fw-bold mb-0"><i class="bi bi-geo-alt text-primary me-2"></i>Mapa de Rutas</h2>
                            <p class="text-muted mb-0">Visualiza los destinos operativos filtrados por sucursal de origen.</p>
                        </div>

                        <div style="width: 350px;">
                            <label class="form-label fw-bold small text-muted">Filtrar por Sucursal de Origen:</label>
                            <select class="form-select border-primary fw-bold shadow-sm" id="filtroOrigen">
                                <option value="">Selecciona una Sucursal...</option>
                                <%
                                    List<Sucursal> sucursales = (List<Sucursal>) request.getAttribute("listaSucursales");
                                    if (sucursales != null) {
                                        for (Sucursal s : sucursales) {
                                %>
                                <option value="<%= s.getIdSucursal()%>"><%= s.getNombre()%></option>
                                <%      }
                                    }
                                %>
                            </select>
                        </div>
                    </div>

                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                        <!-- Contenedor del Mapa -->
                        <div id="mapa-red" style="height: 65vh; width: 100%; z-index: 1;"></div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            // Transformar listas de java a formato JSON para js
            const datosSucursales = [
            <%
                    if (sucursales != null) {
                        for (int i = 0; i < sucursales.size(); i++) {
                            Sucursal s = sucursales.get(i);
            %>
            { id: <%= s.getIdSucursal()%>, nombre: "<%= s.getNombre()%>", lat: <%= s.getLatitud()%>, lng: <%= s.getLongitud()%> }<%= (i < sucursales.size() - 1) ? "," : ""%>
            <%      }
                    }
            %>
            ];

            const datosRutas = [
            <%
                    List<Ruta> rutas = (List<Ruta>) request.getAttribute("listaRutas");
                    if (rutas != null) {
                        for (int i = 0; i < rutas.size(); i++) {
                            Ruta r = rutas.get(i);
            %>
            { origen: <%= r.getIdOrigen()%>, destino: <%= r.getIdDestino()%>, precio: <%= r.getPrecio()%> }<%= (i < rutas.size() - 1) ? "," : ""%>
            <%      }
                    }
            %>
            ];

            // inicialización del mapa
            const mapRed = L.map('mapa-red').setView([14.83472, -91.51805], 8);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: '&copy; OpenStreetMap'}).addTo(mapRed); //descarga las calles y mapas para pegarlas en el fondo

            // capa dinamica para agrupar y borrar marcadores/líneas fácilmente
            const layerRutas = L.layerGroup().addTo(mapRed);

            // iconos personalizados
            const iconRed = new L.Icon({iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png', shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png', iconSize: [25, 41], iconAnchor: [12, 41]});
            const iconBlue = new L.Icon({iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png', shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png', iconSize: [25, 41], iconAnchor: [12, 41]});

            // evento principal, cuando el usuario cambia el filtro
            document.getElementById('filtroOrigen').addEventListener('change', function () {
                layerRutas.clearLayers(); // borra rutas del filtro anterior

                let idOrigen = parseInt(this.value);
                if (!idOrigen) {
                    mapRed.setView([14.83472, -91.51805], 8);
                    return;
                }

                // para encontrar la sucursal de origen y colocar marcador rojo
                let sucOrigen = datosSucursales.find(s => s.id === idOrigen);
                if (!sucOrigen)
                    return;

                L.marker([sucOrigen.lat, sucOrigen.lng], {icon: iconRed})
                        .addTo(layerRutas)
                        .bindPopup(`<div class="text-center"><b>ORIGEN</b><br><span class="text-danger fw-bold">${sucOrigen.nombre}</span></div>`)
                        .openPopup();

                mapRed.setView([sucOrigen.lat, sucOrigen.lng], 9);

                // filtrar las rutas que inician en esta sucursal
                let rutasActivas = datosRutas.filter(r => r.origen === idOrigen);

                // dibujar los destinos y trazar las líneas
                for (let ruta of rutasActivas) {
                    let sucDestino = datosSucursales.find(s => s.id === ruta.destino);
                    if (sucDestino) {
                        L.marker([sucDestino.lat, sucDestino.lng], {icon: iconBlue})
                                .addTo(layerRutas)
                                .bindPopup(`<b>DESTINO: ${sucDestino.nombre}</b><br>Precio: Q.${ruta.precio.toFixed(2)}`);

                        L.polyline([[sucOrigen.lat, sucOrigen.lng], [sucDestino.lat, sucDestino.lng]], {
                            color: '#0d6efd',
                            weight: 4,
                            opacity: 0.7,
                            dashArray: '10, 10'
                        }).addTo(layerRutas);
                    }
                }
            });
        </script>
    </body>
</html>