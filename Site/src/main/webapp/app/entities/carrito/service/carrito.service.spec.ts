import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICarrito, Carrito } from '../carrito.model';

import { CarritoService } from './carrito.service';

describe('Service Tests', () => {
  describe('Carrito Service', () => {
    let service: CarritoService;
    let httpMock: HttpTestingController;
    let elemDefault: ICarrito;
    let expectedResult: ICarrito | ICarrito[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CarritoService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        cantidad: 0,
        fechaCarrito: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            fechaCarrito: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Carrito', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            fechaCarrito: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            fechaCarrito: currentDate,
          },
          returnedFromService
        );

        service.create(new Carrito()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Carrito', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            cantidad: 1,
            fechaCarrito: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            fechaCarrito: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Carrito', () => {
        const patchObject = Object.assign(
          {
            fechaCarrito: currentDate.format(DATE_TIME_FORMAT),
          },
          new Carrito()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            fechaCarrito: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Carrito', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            cantidad: 1,
            fechaCarrito: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            fechaCarrito: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Carrito', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCarritoToCollectionIfMissing', () => {
        it('should add a Carrito to an empty array', () => {
          const carrito: ICarrito = { id: 123 };
          expectedResult = service.addCarritoToCollectionIfMissing([], carrito);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(carrito);
        });

        it('should not add a Carrito to an array that contains it', () => {
          const carrito: ICarrito = { id: 123 };
          const carritoCollection: ICarrito[] = [
            {
              ...carrito,
            },
            { id: 456 },
          ];
          expectedResult = service.addCarritoToCollectionIfMissing(carritoCollection, carrito);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Carrito to an array that doesn't contain it", () => {
          const carrito: ICarrito = { id: 123 };
          const carritoCollection: ICarrito[] = [{ id: 456 }];
          expectedResult = service.addCarritoToCollectionIfMissing(carritoCollection, carrito);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(carrito);
        });

        it('should add only unique Carrito to an array', () => {
          const carritoArray: ICarrito[] = [{ id: 123 }, { id: 456 }, { id: 82419 }];
          const carritoCollection: ICarrito[] = [{ id: 123 }];
          expectedResult = service.addCarritoToCollectionIfMissing(carritoCollection, ...carritoArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const carrito: ICarrito = { id: 123 };
          const carrito2: ICarrito = { id: 456 };
          expectedResult = service.addCarritoToCollectionIfMissing([], carrito, carrito2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(carrito);
          expect(expectedResult).toContain(carrito2);
        });

        it('should accept null and undefined values', () => {
          const carrito: ICarrito = { id: 123 };
          expectedResult = service.addCarritoToCollectionIfMissing([], null, carrito, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(carrito);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
