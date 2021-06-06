jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { FacturaService } from '../service/factura.service';
import { IFactura, Factura } from '../factura.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { FacturaUpdateComponent } from './factura-update.component';

describe('Component Tests', () => {
  describe('Factura Management Update Component', () => {
    let comp: FacturaUpdateComponent;
    let fixture: ComponentFixture<FacturaUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let facturaService: FacturaService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [FacturaUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(FacturaUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(FacturaUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      facturaService = TestBed.inject(FacturaService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const factura: IFactura = { id: 456 };
        const assignedTo: IUser = { id: 13820 };
        factura.assignedTo = assignedTo;

        const userCollection: IUser[] = [{ id: 8136 }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [assignedTo];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ factura });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const factura: IFactura = { id: 456 };
        const assignedTo: IUser = { id: 62330 };
        factura.assignedTo = assignedTo;

        activatedRoute.data = of({ factura });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(factura));
        expect(comp.usersSharedCollection).toContain(assignedTo);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const factura = { id: 123 };
        spyOn(facturaService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ factura });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: factura }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(facturaService.update).toHaveBeenCalledWith(factura);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const factura = new Factura();
        spyOn(facturaService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ factura });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: factura }));
        saveSubject.complete();

        // THEN
        expect(facturaService.create).toHaveBeenCalledWith(factura);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const factura = { id: 123 };
        spyOn(facturaService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ factura });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(facturaService.update).toHaveBeenCalledWith(factura);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
