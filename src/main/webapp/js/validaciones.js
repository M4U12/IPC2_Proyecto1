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