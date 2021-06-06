import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICarrito } from '../carrito.model';
import { CarritoService } from '../service/carrito.service';

@Component({
  templateUrl: './carrito-delete-dialog.component.html',
})
export class CarritoDeleteDialogComponent {
  carrito?: ICarrito;

  constructor(protected carritoService: CarritoService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.carritoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
