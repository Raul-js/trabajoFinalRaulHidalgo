import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProducto } from '../producto.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ProductoService } from '../service/producto.service';
import { ProductoDeleteDialogComponent } from '../delete/producto-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';
import { ProductoFilter } from './producto-filter.model';
import { FormBuilder } from '@angular/forms';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { LoginService } from 'app/login/login.service';
import { SessionStorageService } from 'ngx-webstorage';
import { AccountService } from 'app/core/auth/account.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';

@Component({
  selector: 'jhi-producto',
  templateUrl: './producto.component.html',
  styleUrls: ['./producto.component.scss'],
})
export class ProductoComponent implements OnInit {
  productos?: IProducto[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  isHidden = true;
  isFiltring = false;
  areFiltersCollapsed = true;
  links: { [key: string]: number };
  filterForm = this.fb.group({
    nombre: [],
    calorias: [],
    precio: [],
    tipo: [],
    existencias: [],
  });
  cols?: any[];
  exportColumns?: any[];
  filtros: ProductoFilter = new ProductoFilter();
  productosSharedCollection: IProducto[] = [];

  constructor(
    protected parseLinks: ParseLinks,
    protected productoService: ProductoService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: DataUtils,
    protected router: Router,
    protected modalService: NgbModal,
    protected fb: FormBuilder,
    private loginService: LoginService,

    private sessionStorage: SessionStorageService,
    private accountService: AccountService,
    private profileService: ProfileService
  ) {
    this.links = {
      last: 0,
    };
  }

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

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }
  clear(): void {
    this.resetFilter();
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.productoService
      .query({
        filter: this.filtros.toMap(),
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IProducto[]>) => {
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
      { field: 'nombreProducto', header: 'nombre' },
      { field: 'precio', header: 'Precio' },
      { field: 'existencias', header: 'existencias' },
      { field: 'calorias', header: 'Calorias' },
    ];
    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
  }

  exportPdf() {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(x => {
        const doc = new jsPDF.default();
        (doc as any).autoTable(this.exportColumns, this.productos);
        doc.save('productos.pdf');
      });
    });
  }
  trackId(index: number, item: IProducto): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(producto: IProducto): void {
    const modalRef = this.modalService.open(ProductoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.producto = producto;
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

  protected onSuccess(data: IProducto[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/producto'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.productos = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
  private createFilterFromForm(): void {
    this.filtros.nombre = this.filterForm.get(['nombre'])?.value;
    this.filtros.calorias = this.filterForm.get(['calorias'])?.value;
    this.filtros.precio = this.filterForm.get(['precio'])?.value;
    this.filtros.tipo = this.filterForm.get(['tipo'])?.value;
    this.filtros.existencias = this.filterForm.get(['existencias'])?.value;
  }
}
