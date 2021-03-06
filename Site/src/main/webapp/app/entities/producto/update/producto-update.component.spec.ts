jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProductoService } from '../service/producto.service';
import { IProducto, Producto } from '../producto.model';
import { ICompra } from 'app/entities/compra/compra.model';
import { CompraService } from 'app/entities/compra/service/compra.service';

import { ProductoUpdateComponent } from './producto-update.component';

describe('Component Tests', () => {
  describe('Producto Management Update Component', () => {
    let comp: ProductoUpdateComponent;
    let fixture: ComponentFixture<ProductoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let productoService: ProductoService;
    let compraService: CompraService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ProductoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ProductoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProductoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      productoService = TestBed.inject(ProductoService);
      compraService = TestBed.inject(CompraService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Compra query and add missing value', () => {
        const producto: IProducto = { id: 456 };
        const compra: ICompra = { id: 41786 };
        producto.compra = compra;

        const compraCollection: ICompra[] = [{ id: 99945 }];
        spyOn(compraService, 'query').and.returnValue(of(new HttpResponse({ body: compraCollection })));
        const additionalCompras = [compra];
        const expectedCollection: ICompra[] = [...additionalCompras, ...compraCollection];
        spyOn(compraService, 'addCompraToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ producto });
        comp.ngOnInit();

        expect(compraService.query).toHaveBeenCalled();
        expect(compraService.addCompraToCollectionIfMissing).toHaveBeenCalledWith(compraCollection, ...additionalCompras);
        expect(comp.comprasSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const producto: IProducto = { id: 456 };
        const compra: ICompra = { id: 60447 };
        producto.compra = compra;

        activatedRoute.data = of({ producto });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(producto));
        expect(comp.comprasSharedCollection).toContain(compra);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const producto = { id: 123 };
        spyOn(productoService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ producto });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: producto }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(productoService.update).toHaveBeenCalledWith(producto);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const producto = new Producto();
        spyOn(productoService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ producto });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: producto }));
        saveSubject.complete();

        // THEN
        expect(productoService.create).toHaveBeenCalledWith(producto);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const producto = { id: 123 };
        spyOn(productoService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ producto });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(productoService.update).toHaveBeenCalledWith(producto);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCompraById', () => {
        it('Should return tracked Compra primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCompraById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
