document.addEventListener('DOMContentLoaded', function () {

    function obtenerDireccionLimpia(lat, lng, inputElement) {
        inputElement.value = "Calculando ubicación...";
        fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`)
                .then(response => response.json())
                .then(data => {
                    if (data && data.address) {
                        var addr = data.address;
                        var lugar = addr.amenity || addr.building || addr.shop || "";
                        var calle = addr.road || addr.pedestrian || "";
                        var zona = addr.city_district || addr.suburb || addr.neighbourhood || addr.quarter || "";
                        var ciudad = addr.city || addr.town || addr.village || addr.county || "";
                        var departamento = addr.state || "";

                        var direccionLimpia = [lugar, calle, zona, ciudad, departamento]
                                .filter(item => item !== "" && item !== undefined)
                                .filter((item, index, arreglo) => arreglo.indexOf(item) === index)
                                .join(", ");

                        inputElement.value = direccionLimpia || data.display_name;
                    } else {
                        inputElement.value = "Lat: " + lat.toFixed(4) + ", Lng: " + lng.toFixed(4);
                    }
                })
                .catch(error => {
                    inputElement.value = "Lat: " + lat.toFixed(4) + ", Lng: " + lng.toFixed(4);
                });
    }

    //  MAPA DE VIAJES PRIVADOS
    var mapContainerPrivado = document.getElementById('mapaPrivado');
    if (mapContainerPrivado) {
        var mapPrivado = L.map('mapaPrivado').setView([14.83472, -91.51805], 14);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: '&copy; OpenStreetMap'}).addTo(mapPrivado);

        var greenIcon = new L.Icon({iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png', shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png', iconSize: [25, 41], iconAnchor: [12, 41]});
        var redIcon = new L.Icon({iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png', shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png', iconSize: [25, 41], iconAnchor: [12, 41]});

        var markerOrigen = L.marker([14.836, -91.527], {draggable: true, icon: greenIcon}).addTo(mapPrivado).bindPopup("<b>Origen</b>").openPopup();
        var markerDestino = L.marker([14.845, -91.515], {draggable: true, icon: redIcon}).addTo(mapPrivado).bindPopup("<b>Destino</b>");

        var inOrig = document.getElementById('inputOrigen');
        var inDest = document.getElementById('inputDestino');

        markerOrigen.on('dragend', function () {
            obtenerDireccionLimpia(markerOrigen.getLatLng().lat, markerOrigen.getLatLng().lng, inOrig);
        });
        markerDestino.on('dragend', function () {
            obtenerDireccionLimpia(markerDestino.getLatLng().lat, markerDestino.getLatLng().lng, inDest);
        });

        obtenerDireccionLimpia(14.836, -91.527, inOrig);
        obtenerDireccionLimpia(14.845, -91.515, inDest);
    }


    // ÍCONO AZUL PARA SUCURSALES
    var blueIcon = new L.Icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
        iconSize: [25, 41], iconAnchor: [12, 41]
    });

    // MAPA DE CREACIÓN DE SUCURSAL
    var mapContainerSucursal = document.getElementById('mapa-sucursal');
    if (mapContainerSucursal) {
        var mapSucursal = L.map('mapa-sucursal').setView([14.83472, -91.51805], 14);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: '&copy; OpenStreetMap'}).addTo(mapSucursal);

        var markerSucursal = L.marker([14.83472, -91.51805], {draggable: true, icon: blueIcon}).addTo(mapSucursal).bindPopup("<b>Ubicación</b>").openPopup();

        var inputCrear = document.getElementById('direccion-input');
        var inputLat = document.getElementById('lat-input');
        var inputLng = document.getElementById('lng-input');

        markerSucursal.on('dragend', function () {
            var pos = markerSucursal.getLatLng();
            obtenerDireccionLimpia(pos.lat, pos.lng, inputCrear);
            inputLat.value = pos.lat;
            inputLng.value = pos.lng;
        });

        mapSucursal.on('click', function (e) {
            markerSucursal.setLatLng(e.latlng);
            obtenerDireccionLimpia(e.latlng.lat, e.latlng.lng, inputCrear);
            inputLat.value = e.latlng.lat;
            inputLng.value = e.latlng.lng;
        });
    }

    // MAPAS EN MODALES DE EDICIÓN
    var modalesEditar = document.querySelectorAll('.modal');
    modalesEditar.forEach(function (modal) {
        modal.addEventListener('shown.bs.modal', function () {
            var mapContainer = modal.querySelector('.mapa-editar-sucursal');

            if (mapContainer) {
                if (mapContainer.miMapaLeaflet) {
                    mapContainer.miMapaLeaflet.invalidateSize();
                    return;
                }

                // lee las coordenadas de los atributos data para centrar el mapa donde ya estaba
                var latGuardada = parseFloat(mapContainer.getAttribute('data-lat')) || 14.83472;
                var lngGuardada = parseFloat(mapContainer.getAttribute('data-lng')) || -91.51805;

                var mapEdit = L.map(mapContainer).setView([latGuardada, lngGuardada], 14);
                mapContainer.miMapaLeaflet = mapEdit;

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: '&copy; OpenStreetMap'}).addTo(mapEdit);

                var markerEdit = L.marker([latGuardada, lngGuardada], {draggable: true, icon: blueIcon}).addTo(mapEdit).bindPopup("<b>Actualizar Ubicación</b><br>Arrastra o haz clic.").openPopup();

                var inputEdit = modal.querySelector('.direccion-editar-input');
                var inputLatEdit = modal.querySelector('.lat-editar-input');
                var inputLngEdit = modal.querySelector('.lng-editar-input');

                markerEdit.on('dragend', function () {
                    var pos = markerEdit.getLatLng();
                    obtenerDireccionLimpia(pos.lat, pos.lng, inputEdit);
                    if (inputLatEdit)
                        inputLatEdit.value = pos.lat;
                    if (inputLngEdit)
                        inputLngEdit.value = pos.lng;
                });

                mapEdit.on('click', function (e) {
                    markerEdit.setLatLng(e.latlng);
                    obtenerDireccionLimpia(e.latlng.lat, e.latlng.lng, inputEdit);
                    if (inputLatEdit)
                        inputLatEdit.value = e.latlng.lat;
                    if (inputLngEdit)
                        inputLngEdit.value = e.latlng.lng;
                });

                setTimeout(() => {
                    mapEdit.invalidateSize();
                }, 150);
            }
        });
    });
});