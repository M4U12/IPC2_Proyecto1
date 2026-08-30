CREATE DATABASE IF NOT EXISTS code_n_bugs;
USE code_n_bugs;


CREATE TABLE Usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    dpi VARCHAR(13) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    nit VARCHAR(13),
    telefono VARCHAR(8) NOT NULL UNIQUE,
    direccion VARCHAR(200),
    estado BOOLEAN DEFAULT TRUE,
    rol ENUM('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_SUCURSAL', 'CLIENTE') NOT NULL
);


CREATE TABLE Sucursales (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(15) NOT NULL UNIQUE
);


CREATE TABLE Admin_Sucursal (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_sucursal INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_sucursal) REFERENCES Sucursales(id_sucursal)
);


CREATE TABLE Configuracion_Sistema (
    id_configuracion INT AUTO_INCREMENT PRIMARY KEY,
    depreciacion_por_km DECIMAL(10,2) NOT NULL
);


CREATE TABLE Choferes (
    id_chofer INT AUTO_INCREMENT PRIMARY KEY,
    id_sucursal INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    foto MEDIUMBLOB,
    num_licencia VARCHAR(50) NOT NULL UNIQUE,
    tipo_licencia ENUM('TIPO_A', 'TIPO_B', 'TIPO_C', 'TIPO_D', 'TIPO_E') NOT NULL,
    fecha_vencimiento_licencia DATE NOT NULL,
    telefono VARCHAR(15) NOT NULL UNIQUE,
    salario_base_por_viaje DECIMAL(10,2) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_sucursal) REFERENCES Sucursales(id_sucursal)
);


CREATE TABLE Buses (
    id_bus INT AUTO_INCREMENT PRIMARY KEY,
    id_sucursal INT NOT NULL,
    id_chofer INT NULL,
    foto MEDIUMBLOB,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio_fabricacion INT NOT NULL,
    capacidad INT NOT NULL,
    estado_operativo ENUM('Disponible', 'En Ruta', 'En Mantenimiento', 'Inactivo') NOT NULL,
    kilometraje_actual DECIMAL(10,2) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_sucursal) REFERENCES Sucursales(id_sucursal),
    FOREIGN KEY (id_chofer) REFERENCES Choferes(id_chofer)
);


CREATE TABLE Mantenimiento (
    id_mantenimiento INT AUTO_INCREMENT PRIMARY KEY,
    bus_id INT NOT NULL,
    fecha_mantenimiento DATE NOT NULL,
    monto_mano_obra DECIMAL(10,2) NOT NULL,
    monto_repuestos DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(260),
    FOREIGN KEY (bus_id) REFERENCES Buses(id_bus)
);


CREATE TABLE Rutas (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    id_origen INT NOT NULL,
    id_destino INT NOT NULL,
    distancia_km DECIMAL(10,2) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_origen) REFERENCES Sucursales(id_sucursal),
    FOREIGN KEY (id_destino) REFERENCES Sucursales(id_sucursal)
);


CREATE TABLE Viajes (
    id_viaje INT AUTO_INCREMENT PRIMARY KEY,
    tipo_viaje ENUM('REGULAR', 'PRIVADO') NOT NULL,
    estado_viaje ENUM('PROGRAMADO', 'EN_CURSO', 'FINALIZADO', 'CANCELADO') DEFAULT 'PROGRAMADO',
    
    id_bus INT NOT NULL,
    id_chofer INT NOT NULL,
    id_ruta INT NULL, 
    id_cliente INT NULL, 
    
    -- exclusivos de viajes privados
    origen_privado VARCHAR(150) NULL,
    destino_privado VARCHAR(150) NULL,
    cantidad_pasajeros_privado INT NULL,
    precio_total_privado DECIMAL(10,2) NULL,
    fecha_retorno_privado DATETIME NULL,
    --
    fecha_hora_salida_estimada DATETIME NOT NULL,
    fecha_hora_llegada_estimada DATETIME NOT NULL,
    fecha_hora_salida_real DATETIME NULL,
    fecha_hora_llegada_real DATETIME NULL,
    
    kilometraje_salida DECIMAL(10,2) NULL,
    kilometraje_llegada DECIMAL(10,2) NULL,
    gasto_combustible DECIMAL(10,2) NULL,
    
    FOREIGN KEY (id_bus) REFERENCES Buses(id_bus),
    FOREIGN KEY (id_chofer) REFERENCES Choferes(id_chofer),
    FOREIGN KEY (id_ruta) REFERENCES Rutas(id_ruta),
    FOREIGN KEY (id_cliente) REFERENCES Usuarios(id_usuario)
);


CREATE TABLE Boletos (
    id_boleto INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_viaje INT NOT NULL,
    numero_asiento INT NOT NULL,
    precio_pagado DECIMAL(10,2) NOT NULL,
    fecha_pago DATETIME NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_viaje) REFERENCES Viajes(id_viaje)
);


CREATE TABLE Cartera (
    id_cartera INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL UNIQUE,
    cantidad_dinero DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)
);


CREATE TABLE Transacciones (
    id_transaccion INT AUTO_INCREMENT PRIMARY KEY,
    id_cartera INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    tipo ENUM('Recarga', 'Pago Boleto', 'Pago Alquiler') NOT NULL,
    fecha_hora DATETIME NOT NULL,
    descripcion VARCHAR(150),
    FOREIGN KEY (id_cartera) REFERENCES Cartera(id_cartera)
);