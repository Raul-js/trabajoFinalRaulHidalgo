jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CarritoService } from '../service/carrito.service';
import { ICarrito, Carrito } from '../carrito.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IProducto } from 'app/entities/producto/producto.model';
import { ProductoService } from 'app/entities/producto/service/producto.service';

import { CarritoUpdateComponent } from './carrito-update.component';

describe('Component Tests', () => {
  describe('Carrito Management Update Component', () => {
    let comp: CarritoUpdateComponent;
    let fixture: ComponentFixture<CarritoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let carritoService: CarritoService;
    let userService: UserService;
    let productoService: ProductoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CarritoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CarritoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CarritoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      carritoService = TestBed.inject(CarritoService);
      userService = TestBed.inject(UserService);
      productoService = TestBed.inject(ProductoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const carrito: ICarrito = { id: 456 };
        const assignedTo: IUser = { id: 27699 };
        carrito.assignedTo = assignedTo;

        const userCollection: IUser[] = [{ id: 87926 }];
        spyOn(userService, 'query').and.returnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [assignedTo];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        spyOn(userService, 'addUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ carrito });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Producto query and add missing value', () => {
        const carrito: ICarrito = { id: 456 };
        const productos: IProducto[] = [{ id: 77166 }];
        carrito.productos = productos;

        const productoCollection: IProducto[] = [{ id: 81998 }];
        spyOn(productoService, 'query').and.returnValue(of(new HttpResponse({ body: productoCollection })));
        const additionalProductos = [...productos];
        const expectedCollection: IProducto[] = [...additionalProductos, ...productoCollection];
        spyOn(productoService, 'addProductoToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ carrito });
        comp.ngOnInit();

        expect(productoService.query).toHaveBeenCalled();
        expect(productoService.addProductoToCollectionIfMissing).toHaveBeenCalledWith(productoCollection, ...additionalProductos);
        expect(comp.productosSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const carrito: ICarrito = { id: 456 };
        const assignedTo: IUser = { id: 47918 };
        carrito.assignedTo = assignedTo;
        const productos: IProducto = { id: 32882 };
        carrito.productos = [productos];

        activatedRoute.data = of({ carrito });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(carrito));
        expect(comp.usersSharedCollection).toContain(assignedTo);
        expect(comp.productosSharedCollection).toContain(productos);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const carrito = { id: 123 };
        spyOn(carritoService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ carrito });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: carrito }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(carritoService.update).toHaveBeenCalledWith(carrito);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const carrito = new Carrito();
        spyOn(carritoService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ carrito });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: carrito }));
        saveSubject.complete();

        // THEN
        expect(carritoService.create).toHaveBeenCalledWith(carrito);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const carrito = { id: 123 };
        spyOn(carritoService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ carrito });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(carritoService.update).toHaveBeenCalledWith(carrito);
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

      describe('trackProductoById', () => {
        it('Should return tracked Producto primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackProductoById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedProducto', () => {
        it('Should return option if no Producto is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedProducto(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Producto for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedProducto(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Producto is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedProducto(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
