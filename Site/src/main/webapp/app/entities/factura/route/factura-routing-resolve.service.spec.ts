jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IFactura, Factura } from '../factura.model';
import { FacturaService } from '../service/factura.service';

import { FacturaRoutingResolveService } from './factura-routing-resolve.service';

describe('Service Tests', () => {
  describe('Factura routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: FacturaRoutingResolveService;
    let service: FacturaService;
    let resultFactura: IFactura | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(FacturaRoutingResolveService);
      service = TestBed.inject(FacturaService);
      resultFactura = undefined;
    });

    describe('resolve', () => {
      it('should return IFactura returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFactura = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultFactura).toEqual({ id: 123 });
      });

      it('should return new IFactura if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFactura = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultFactura).toEqual(new Factura());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFactura = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultFactura).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
