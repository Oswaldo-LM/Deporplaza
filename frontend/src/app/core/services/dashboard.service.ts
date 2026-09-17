import {
  Injectable,
  inject
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import {
  environment
} from '../../../environments/environment';

import {
  DashboardResumen
} from '../models/dashboard.model';


@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly http =
    inject(HttpClient);


  obtenerResumen():
    Observable<DashboardResumen> {

    return this.http.get<DashboardResumen>(
      `${environment.apiUrl}/admin/dashboard/resumen`
    );
  }

}