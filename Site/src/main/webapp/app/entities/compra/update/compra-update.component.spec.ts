jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CompraService } from '../service/compra.service';
import { ICompra, Compra } from '../compra.model';
import { IFactura } from 'app/entities/factura/factura.model';
import { FacturaService } from 'app/entities/factura/service/factura.service';

import { CompraUpdateComponent } from './compra-update.component';

describe('Component Tests', () => {
  describe('Compra Management Update Component', () => {
    let comp: CompraUpdateComponent;
    let fixture: ComponentFixture<CompraUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let compraService: CompraService;
    let facturaService: FacturaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CompraUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CompraUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CompraUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      compraService = TestBed.inject(CompraService);
      facturaService = TestBed.inject(FacturaService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Factura query and add missing value', () => {
        const compra: ICompra = { id: 456 };
        const factura: IFactura = { id: 82538 };
        compra.factura = factura;

        const facturaCollection: IFactura[] = [{ id: 25896 }];
        spyOn(facturaService, 'query').and.returnValue(of(new HttpResponse({ body: facturaCollection })));
        const additionalFacturas = [factura];
        const expectedCollection: IFactura[] = [...additionalFacturas, ...facturaCollection];
        spyOn(facturaService, 'addFacturaToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ compra });
        comp.ngOnInit();

        expect(facturaService.query).toHaveBeenCalled();
        expect(facturaService.addFacturaToCollectionIfMissing).toHaveBeenCalledWith(facturaCollection, ...additionalFacturas);
        expect(comp.facturasSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const compra: ICompra = { id: 456 };
        const factura: IFactura = { id: 32126 };
        compra.factura = factura;

        activatedRoute.data = of({ compra });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(compra));
        expect(comp.facturasSharedCollection).toContain(factura);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const compra = { id: 123 };
        spyOn(compraService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ compra });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: compra }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(compraService.update).toHaveBeenCalledWith(compra);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const compra = new Compra();
        spyOn(compraService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ compra });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: compra }));
        saveSubject.complete();

        // THEN
        expect(compraService.create).toHaveBeenCalledWith(compra);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const compra = { id: 123 };
        spyOn(compraService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ compra });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(compraService.update).toHaveBeenCalledWith(compra);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackFacturaById', () => {
        it('Should return tracked Factura primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackFacturaById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
