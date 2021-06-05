import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICompra, Compra } from '../compra.model';
import { CompraService } from '../service/compra.service';
import { IFactura } from 'app/entities/factura/factura.model';
import { FacturaService } from 'app/entities/factura/service/factura.service';

@Component({
  selector: 'jhi-compra-update',
  templateUrl: './compra-update.component.html',
})
export class CompraUpdateComponent implements OnInit {
  isSaving = false;

  facturasSharedCollection: IFactura[] = [];

  editForm = this.fb.group({
    id: [],
    cantidadComprada: [],
    precioPagado: [],
    factura: [],
  });

  constructor(
    protected compraService: CompraService,
    protected facturaService: FacturaService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ compra }) => {
      this.updateForm(compra);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const compra = this.createFromForm();
    if (compra.id !== undefined) {
      this.subscribeToSaveResponse(this.compraService.update(compra));
    } else {
      this.subscribeToSaveResponse(this.compraService.create(compra));
    }
  }

  trackFacturaById(index: number, item: IFactura): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompra>>): void {
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

  protected updateForm(compra: ICompra): void {
    this.editForm.patchValue({
      id: compra.id,
      cantidadComprada: compra.cantidadComprada,
      precioPagado: compra.precioPagado,
      factura: compra.factura,
    });

    this.facturasSharedCollection = this.facturaService.addFacturaToCollectionIfMissing(this.facturasSharedCollection, compra.factura);
  }

  protected loadRelationshipsOptions(): void {
    this.facturaService
      .query()
      .pipe(map((res: HttpResponse<IFactura[]>) => res.body ?? []))
      .pipe(
        map((facturas: IFactura[]) => this.facturaService.addFacturaToCollectionIfMissing(facturas, this.editForm.get('factura')!.value))
      )
      .subscribe((facturas: IFactura[]) => (this.facturasSharedCollection = facturas));
  }

  protected createFromForm(): ICompra {
    return {
      ...new Compra(),
      id: this.editForm.get(['id'])!.value,
      cantidadComprada: this.editForm.get(['cantidadComprada'])!.value,
      precioPagado: this.editForm.get(['precioPagado'])!.value,
      factura: this.editForm.get(['factura'])!.value,
    };
  }
}
