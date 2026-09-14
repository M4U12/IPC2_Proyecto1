function soloNumeros(e) {
    var charCode = (e.which) ? e.which : e.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        e.preventDefault();
    }
}
function soloLetras(e) {
    var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
    var regex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
    if (!regex.test(key)) {
        e.preventDefault();
        return false;
    }
}

function soloDecimales(e) {
    var charCode = (e.which) ? e.which : e.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode !== 46) {
        e.preventDefault();
    }
}

function calcularCotizacionEnVivo(idViaje, fechaSalidaStr, pasajeros, tarifaBase, tarifaPasajero) {
    let inputLlegada = document.getElementById('llegada_' + idViaje);
    let inputPrecio = document.getElementById('precio_' + idViaje);
    
    if (inputLlegada.value) {
        let fechaSalida = new Date(fechaSalidaStr);
        let fechaLlegada = new Date(inputLlegada.value);
        let diferenciaMilisegundos = fechaLlegada - fechaSalida;
        
        // Convertir a horas
        let horas = Math.floor(diferenciaMilisegundos / (1000 * 60 * 60));
        
        // mínimo de cobro de 1 hora
        if (horas <= 0) {
            horas = 1;
        }
        
        let precioEstimado = (pasajeros * tarifaPasajero) + (horas * tarifaBase);

        inputPrecio.value = precioEstimado.toFixed(2);
    }
}