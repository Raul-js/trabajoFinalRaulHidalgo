import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProducto, Producto } from '../producto.model';
import { ProductoService } from '../service/producto.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICompra } from 'app/entities/compra/compra.model';
import { CompraService } from 'app/entities/compra/service/compra.service';

@Component({
  selector: 'jhi-producto-update',
  templateUrl: './producto-update.component.html',
})
export class ProductoUpdateComponent implements OnInit {
  isSaving = false;

  comprasSharedCollection: ICompra[] = [];

  editForm = this.fb.group({
    id: [],
    nombreProducto: [null, [Validators.required, Validators.maxLength(250)]],
    calorias: [null, [Validators.max(1000)]],
    imagen: [null, []],
    imagenContentType: [],
    precio: [null, [Validators.required, Validators.max(200)]],
    existencias: [null, [Validators.required, Validators.max(50)]],
    tipoproducto: [],
    compra: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected productoService: ProductoService,
    protected compraService: CompraService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ producto }) => {
      this.updateForm(producto);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('yuliqApp.error', { ...err, key: 'error.file.' + err.key })
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const producto = this.createFromForm();
    if (producto.id !== undefined) {
      this.subscribeToSaveResponse(this.productoService.update(producto));
    } else {
      this.subscribeToSaveResponse(this.productoService.create(producto));
    }
  }

  trackCompraById(index: number, item: ICompra): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducto>>): void {
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

  protected updateForm(producto: IProducto): void {
    this.editForm.patchValue({
      id: producto.id,
      nombreProducto: producto.nombreProducto,
      calorias: producto.calorias,
      imagen: producto.imagen,
      imagenContentType: producto.imagenContentType,
      precio: producto.precio,
      existencias: producto.existencias,
      tipoproducto: producto.tipoproducto,
      compra: producto.compra,
    });

    this.comprasSharedCollection = this.compraService.addCompraToCollectionIfMissing(this.comprasSharedCollection, producto.compra);
  }

  protected loadRelationshipsOptions(): void {
    this.compraService
      .query()
      .pipe(map((res: HttpResponse<ICompra[]>) => res.body ?? []))
      .pipe(map((compras: ICompra[]) => this.compraService.addCompraToCollectionIfMissing(compras, this.editForm.get('compra')!.value)))
      .subscribe((compras: ICompra[]) => (this.comprasSharedCollection = compras));
  }

  protected createFromForm(): IProducto {
    return {
      ...new Producto(),
      id: this.editForm.get(['id'])!.value,
      nombreProducto: this.editForm.get(['nombreProducto'])!.value,
      calorias: this.editForm.get(['calorias'])!.value,
      imagenContentType: this.editForm.get(['imagenContentType'])!.value,
      imagen: this.editForm.get(['imagen'])!.value,
      precio: this.editForm.get(['precio'])!.value,
      existencias: this.editForm.get(['existencias'])!.value,
      tipoproducto: this.editForm.get(['tipoproducto'])!.value,
      compra: this.editForm.get(['compra'])!.value,
    };
  }
}
