import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FacturaService } from '../service/factura.service';
import { IFactura } from '../factura.model';
import { FacturaFilter } from '../list/factura-filter.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'jhi-factura-detail',
  templateUrl: './factura-detail.component.html',
})
export class FacturaDetailComponent implements OnInit {
  factura: IFactura | null = null;
  predicate!: string;
  ascending!: boolean;
  facturas?: IFactura[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = 10;
  page?: number;
  ngbPaginationPage = 1;
  isFiltring = false;

  areFiltersCollapsed = true;
  filtros: FacturaFilter = new FacturaFilter();
  facturasSharedCollection: IFactura[] = [];
  filterForm = this.fb.group({
    nombre: [''],
    field_dateFrom: [null],
    field_dateto: [null],
    id: [''],
  });
  cols?: any[];
  exportColumns?: any[];
  constructor(
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected facturaService: FacturaService,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.handleNavigation();
    this.activatedRoute.data.subscribe(({ factura }) => {
      this.factura = factura;
    });
    this.cols = [{ field: 'id', header: 'id' }];
    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
  }
  exportPdf(): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(x => {
        const doc = new jsPDF.default();
        (doc as any).autoTable(this.exportColumns, this.facturas);
        doc.save('facturas.pdf');
      });
    });
  }
  previousState(): void {
    window.history.back();
  }
  trackId(index: number, item: IFactura): number {
    return item.id!;
  }
  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
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
  handleNavigation(): void {
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
  onSuccess(data: IFactura[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
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

  onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
