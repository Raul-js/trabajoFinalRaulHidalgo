import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICarrito } from '../carrito.model';

@Component({
  selector: 'jhi-carrito-detail',
  templateUrl: './carrito-detail.component.html',
})
export class CarritoDetailComponent implements OnInit {
  carrito: ICarrito | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carrito }) => {
      this.carrito = carrito;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
