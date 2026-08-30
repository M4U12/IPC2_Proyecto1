package modelos;

public class Enums {

    public enum EstadoViaje {
        PROGRAMADO,
        EN_CURSO,
        FINALIZADO,
        CANCELADO
    }

    public enum TipoViaje {
        ESTANDAR,
        PRIVADO
    }

    public enum RolUsuario {
        ADMINISTRADOR_GLOBAL,
        ADMINISTRADOR_SUCURSAL,
        CLIENTE,
    }
    
    public enum TipoLicencia{
        TIPO_A,
        TIPO_B,
        TIPO_C,
        TIPO_D,
        TIPO_E
    }
}
