jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICarrito, Carrito } from '../carrito.model';
import { CarritoService } from '../service/carrito.service';

import { CarritoRoutingResolveService } from './carrito-routing-resolve.service';

describe('Service Tests', () => {
  describe('Carrito routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CarritoRoutingResolveService;
    let service: CarritoService;
    let resultCarrito: ICarrito | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CarritoRoutingResolveService);
      service = TestBed.inject(CarritoService);
      resultCarrito = undefined;
    });

    describe('resolve', () => {
      it('should return ICarrito returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCarrito = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCarrito).toEqual({ id: 123 });
      });

      it('should return new ICarrito if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCarrito = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCarrito).toEqual(new Carrito());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCarrito = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCarrito).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
