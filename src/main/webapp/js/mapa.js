document.addEventListener('DOMContentLoaded', function () {
    var mapContainer = document.getElementById('mapaPrivado');

    //solo ejecuta si el contenedor existe
    if (mapContainer) {
        var map = L.map('mapaPrivado').setView([14.83472, -91.51805], 14);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        var greenIcon = new L.Icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
        });

        var redIcon = new L.Icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
        });

        var markerOrigen = L.marker([14.836, -91.527], {draggable: true, icon: greenIcon}).addTo(map).bindPopup("<b>Origen</b><br>Arrástrame al punto de salida.").openPopup();
        var markerDestino = L.marker([14.845, -91.515], {draggable: true, icon: redIcon}).addTo(map).bindPopup("<b>Destino</b><br>Arrástrame a donde quieres ir.");

        function actualizarDireccion(marker, inputId) {
            var pos = marker.getLatLng();
            document.getElementById(inputId).value = "Calculando ubicación...";

            fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${pos.lat}&lon=${pos.lng}&zoom=18&addressdetails=1`)
                    .then(response => response.json())
                    .then(data => {
                        if (data && data.address) {
                            var addr = data.address;

                            // extracción de datos específicos
                            var lugar = addr.amenity || addr.building || addr.shop || "";
                            var calle = addr.road || addr.pedestrian || "";

                            // busqueda de la zona a todas las etiquetas usadas en Guatemala
                            var zona = addr.city_district || addr.suburb || addr.neighbourhood || addr.quarter || "";

                            var ciudad = addr.city || addr.town || addr.village || addr.county || "";
                            var departamento = addr.state || "";

                            // construcción de la dirección limpia
                            var direccionLimpia = [lugar, calle, zona, ciudad, departamento]
                                    .filter(item => item !== "" && item !== undefined) // Filtra nulos o vacíos
                                    .filter((item, index, arreglo) => arreglo.indexOf(item) === index) // evita repetir "Quetzaltenango" si es ciudad y depto a la vez
                                    .join(", ");

                            document.getElementById(inputId).value = direccionLimpia || data.display_name;
                        } else {
                            document.getElementById(inputId).value = "Lat: " + pos.lat.toFixed(4) + ", Lng: " + pos.lng.toFixed(4);
                        }
                    })
                    .catch(error => {
                        document.getElementById(inputId).value = "Lat: " + pos.lat.toFixed(4) + ", Lng: " + pos.lng.toFixed(4);
                    });
        }

        markerOrigen.on('dragend', function () {
            actualizarDireccion(markerOrigen, 'inputOrigen');
        });
        markerDestino.on('dragend', function () {
            actualizarDireccion(markerDestino, 'inputDestino');
        });

        actualizarDireccion(markerOrigen, 'inputOrigen');
        actualizarDireccion(markerDestino, 'inputDestino');
    }
});