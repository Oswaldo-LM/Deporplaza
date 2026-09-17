export interface DashboardProximaReserva {

  idReserva: number;

  nombreCliente: string;

  nombreSede: string;

  nombreCancha: string;

  fechaTurno: string;

  horaInicio: string;

  horaFin: string;

  total: number;

  origen:
    'WEB'
    | 'PRESENCIAL';

}


export interface DashboardResumen {

  reservasHoy: number;

  confirmadasHoy: number;

  pagosPendientes: number;

  ingresosHoy: number;

  proximasReservas:
    DashboardProximaReserva[];

}