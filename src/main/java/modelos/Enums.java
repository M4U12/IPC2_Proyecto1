package modelos;

public class Enums {

    public enum EstadoViaje {
        PROGRAMADO,
        EN_CURSO,
        FINALIZADO,
        CANCELADO,
        PENDIENTE,
        COTIZADA,
        PAGADA,
    }

    public enum TipoViaje {
        REGULAR,
        PRIVADO
    }

    public enum RolUsuario {
        ADMINISTRADOR_SISTEMA,
        ADMINISTRADOR_SUCURSAL,
        CLIENTE,
    }

    public enum TipoLicencia {
        TIPO_A,
        TIPO_B,
        TIPO_C,
        TIPO_D,
        TIPO_E
    }

    public enum EstadoOperativo {
        DISPONIBLE,
        EN_RUTA,
        EN_MANTENIMIENTO,
        INACTIVO,
    }

    public enum TipoTransacciones {
        RECARGA,
        PAGO_BOLETO,
        PAGO_ALQUILER
    }

    public enum EstadoSolicitud {
        PENDIENTE,
        COTIZADA,
        PAGADA,
        RECHAZADA
    }
}
