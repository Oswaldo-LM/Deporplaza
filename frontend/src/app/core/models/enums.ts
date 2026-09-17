export type RolUsuario =
  | 'ADMIN'
  | 'EMPLEADO'
  | 'CLIENTE';


export type EstadoUsuario =
  | 'ACTIVO'
  | 'INACTIVO'
  | 'BLOQUEADO';


export type TipoDocumento =
  | 'DNI'
  | 'CE'
  | 'PASAPORTE';


export type EstadoSede =
  | 'ACTIVA'
  | 'INACTIVA';


export type SuperficieCancha =
  | 'GRASS'
  | 'LOSA';


export type EstadoCancha =
  | 'ACTIVA'
  | 'INACTIVA'
  | 'MANTENIMIENTO';


export type DiaSemana =
  | 'LUNES'
  | 'MARTES'
  | 'MIERCOLES'
  | 'JUEVES'
  | 'VIERNES'
  | 'SABADO'
  | 'DOMINGO';


export type EstadoHorario =
  | 'ACTIVO'
  | 'INACTIVO';


export type EstadoReserva =
  | 'PENDIENTE_PAGO'
  | 'PENDIENTE_CONFIRMACION'
  | 'CONFIRMADA'
  | 'CANCELADA'
  | 'EXPIRADA'
  | 'COMPLETADA';


export type OrigenReserva =
  | 'WEB'
  | 'PRESENCIAL';


export type MetodoPago =
  | 'YAPE'
  | 'PLIN'
  | 'TRANSFERENCIA'
  | 'TARJETA'
  | 'EFECTIVO';


export type EstadoPago =
  | 'PENDIENTE_VALIDACION'
  | 'APROBADO'
  | 'RECHAZADO';