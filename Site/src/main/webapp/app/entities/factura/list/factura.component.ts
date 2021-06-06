import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFactura } from '../factura.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { FacturaService } from '../service/factura.service';
import { FacturaDeleteDialogComponent } from '../delete/factura-delete-dialog.component';
import { FormBuilder } from '@angular/forms';
import { FacturaFilter } from './factura-filter.model';
import { IUser } from 'app/entities/user/user.model';

@Component({
  selector: 'jhi-factura',
  templateUrl: './factura.component.html',
})
export class FacturaComponent implements OnInit {
  facturas?: IFactura[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  isFiltring = false;
  areFiltersCollapsed = true;

  filterForm = this.fb.group({
    nombre: [''],
    field_dateFrom: [null],
    field_dateto: [null],
    id: [''],
  });
  filtros: FacturaFilter = new FacturaFilter();
  facturasSharedCollection: IFactura[] = [];
  cols?: any[];
  exportColumns?: any[];

  constructor(
    protected facturaService: FacturaService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected fb: FormBuilder
  ) {}
  filter(): void {
    this.isFiltring = true;
    this.createFilterFromForm();
    this.loadPage(1, true);

    /*eslint-disable*/
  }
  resetFilter(): void {
    this.isFiltring = false;
    this.filterForm.reset();
    this.loadPage();
  }
  clear(): void {
    this.resetFilter();
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.facturaService
      .query({
        filter: this.filtros.toMap(),
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IFactura[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          this.onError();
        }
      );
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.cols = [
      { field: 'id', header: 'Id' },
      { field: 'fechaFactura', header: 'FechaFactura' },
      { field: 'cantidadPagada', header: 'Cantidad' },
    ];
    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
  }

  trackId(index: number, item: IFactura): number {
    return item.id!;
  }

  delete(factura: IFactura): void {
    const modalRef = this.modalService.open(FacturaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.factura = factura;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }
  exportPdf() {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(x => {
        const doc = new jsPDF.default();
        (doc as any).autoTable(this.exportColumns, this.facturas);
        doc.save('fact.pdf');
      });
    });
  }

  protected onSuccess(data: IFactura[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/factura'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.facturas = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
  private createFilterFromForm(): void {
    this.filtros.field_dateFrom = this.filterForm.get(['field_dateFrom'])?.value;
    this.filtros.id = this.filterForm.get(['id'])?.value;
    this.filtros.field_dateto = this.filterForm.get(['field_dateto'])?.value;
  }
}
