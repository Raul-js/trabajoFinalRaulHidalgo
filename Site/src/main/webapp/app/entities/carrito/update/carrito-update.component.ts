import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICarrito, Carrito } from '../carrito.model';
import { CarritoService } from '../service/carrito.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IProducto } from 'app/entities/producto/producto.model';
import { ProductoService } from 'app/entities/producto/service/producto.service';

@Component({
  selector: 'jhi-carrito-update',
  templateUrl: './carrito-update.component.html',
})
export class CarritoUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  productosSharedCollection: IProducto[] = [];

  editForm = this.fb.group({
    id: [],
    cantidad: [],
    fechaCarrito: [],
    assignedTo: [],
    productos: [],
  });

  constructor(
    protected carritoService: CarritoService,
    protected userService: UserService,
    protected productoService: ProductoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ carrito }) => {
      if (carrito.id === undefined) {
        const today = dayjs().startOf('day');
        carrito.fechaCarrito = today;
      }

      this.updateForm(carrito);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const carrito = this.createFromForm();
    if (carrito.id !== undefined) {
      this.subscribeToSaveResponse(this.carritoService.update(carrito));
    } else {
      this.subscribeToSaveResponse(this.carritoService.create(carrito));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackProductoById(index: number, item: IProducto): number {
    return item.id!;
  }

  getSelectedProducto(option: IProducto, selectedVals?: IProducto[]): IProducto {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICarrito>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(carrito: ICarrito): void {
    this.editForm.patchValue({
      id: carrito.id,
      cantidad: carrito.cantidad,
      fechaCarrito: carrito.fechaCarrito ? carrito.fechaCarrito.format(DATE_TIME_FORMAT) : null,
      assignedTo: carrito.assignedTo,
      productos: carrito.productos,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, carrito.assignedTo);
    this.productosSharedCollection = this.productoService.addProductoToCollectionIfMissing(
      this.productosSharedCollection,
      ...(carrito.productos ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('assignedTo')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.productoService
      .query()
      .pipe(map((res: HttpResponse<IProducto[]>) => res.body ?? []))
      .pipe(
        map((productos: IProducto[]) =>
          this.productoService.addProductoToCollectionIfMissing(productos, ...(this.editForm.get('productos')!.value ?? []))
        )
      )
      .subscribe((productos: IProducto[]) => (this.productosSharedCollection = productos));
  }

  protected createFromForm(): ICarrito {
    return {
      ...new Carrito(),
      id: this.editForm.get(['id'])!.value,
      cantidad: this.editForm.get(['cantidad'])!.value,
      fechaCarrito: this.editForm.get(['fechaCarrito'])!.value
        ? dayjs(this.editForm.get(['fechaCarrito'])!.value, DATE_TIME_FORMAT)
        : undefined,
      assignedTo: this.editForm.get(['assignedTo'])!.value,
      productos: this.editForm.get(['productos'])!.value,
    };
  }
}
