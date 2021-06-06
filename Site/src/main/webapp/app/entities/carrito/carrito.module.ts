import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { CarritoComponent } from './list/carrito.component';
import { CarritoDetailComponent } from './detail/carrito-detail.component';
import { CarritoUpdateComponent } from './update/carrito-update.component';
import { CarritoDeleteDialogComponent } from './delete/carrito-delete-dialog.component';
import { CarritoRoutingModule } from './route/carrito-routing.module';

@NgModule({
  imports: [SharedModule, CarritoRoutingModule],
  declarations: [CarritoComponent, CarritoDetailComponent, CarritoUpdateComponent, CarritoDeleteDialogComponent],
  entryComponents: [CarritoDeleteDialogComponent],
})
export class CarritoModule {}
