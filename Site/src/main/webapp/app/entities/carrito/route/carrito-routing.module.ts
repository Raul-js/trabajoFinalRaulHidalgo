import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CarritoComponent } from '../list/carrito.component';
import { CarritoDetailComponent } from '../detail/carrito-detail.component';
import { CarritoUpdateComponent } from '../update/carrito-update.component';
import { CarritoRoutingResolveService } from './carrito-routing-resolve.service';

const carritoRoute: Routes = [
  {
    path: '',
    component: CarritoComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CarritoDetailComponent,
    resolve: {
      carrito: CarritoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CarritoUpdateComponent,
    resolve: {
      carrito: CarritoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CarritoUpdateComponent,
    resolve: {
      carrito: CarritoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(carritoRoute)],
  exports: [RouterModule],
})
export class CarritoRoutingModule {}
