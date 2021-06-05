import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICarrito, Carrito } from '../carrito.model';
import { CarritoService } from '../service/carrito.service';

@Injectable({ providedIn: 'root' })
export class CarritoRoutingResolveService implements Resolve<ICarrito> {
  constructor(protected service: CarritoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICarrito> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((carrito: HttpResponse<Carrito>) => {
          if (carrito.body) {
            return of(carrito.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Carrito());
  }
}
