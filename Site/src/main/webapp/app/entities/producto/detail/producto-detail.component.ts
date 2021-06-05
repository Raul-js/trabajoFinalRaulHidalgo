import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProducto } from '../producto.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-producto-detail',
  templateUrl: './producto-detail.component.html',
})
export class ProductoDetailComponent implements OnInit {
  producto: IProducto | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ producto }) => {
      this.producto = producto;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
