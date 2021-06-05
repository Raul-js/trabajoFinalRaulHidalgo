import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'producto',
        data: { pageTitle: 'yuliqApp.producto.home.title' },
        loadChildren: () => import('./producto/producto.module').then(m => m.ProductoModule),
      },
      {
        path: 'banner',
        data: { pageTitle: 'yuliqApp.banner.home.title' },
        loadChildren: () => import('./banner/banner.module').then(m => m.BannerModule),
      },
      {
        path: 'carrito',
        data: { pageTitle: 'yuliqApp.carrito.home.title' },
        loadChildren: () => import('./carrito/carrito.module').then(m => m.CarritoModule),
      },
      {
        path: 'factura',
        data: { pageTitle: 'yuliqApp.factura.home.title' },
        loadChildren: () => import('./factura/factura.module').then(m => m.FacturaModule),
      },
      {
        path: 'compra',
        data: { pageTitle: 'yuliqApp.compra.home.title' },
        loadChildren: () => import('./compra/compra.module').then(m => m.CompraModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
