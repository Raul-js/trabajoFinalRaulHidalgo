import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICarrito, getCarritoIdentifier } from '../carrito.model';

export type EntityResponseType = HttpResponse<ICarrito>;
export type EntityArrayResponseType = HttpResponse<ICarrito[]>;

@Injectable({ providedIn: 'root' })
export class CarritoService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/carritos');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(carrito: ICarrito): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(carrito);
    return this.http
      .post<ICarrito>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(carrito: ICarrito): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(carrito);
    return this.http
      .put<ICarrito>(`${this.resourceUrl}/${getCarritoIdentifier(carrito) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(carrito: ICarrito): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(carrito);
    return this.http
      .patch<ICarrito>(`${this.resourceUrl}/${getCarritoIdentifier(carrito) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICarrito>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICarrito[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCarritoToCollectionIfMissing(carritoCollection: ICarrito[], ...carritosToCheck: (ICarrito | null | undefined)[]): ICarrito[] {
    const carritos: ICarrito[] = carritosToCheck.filter(isPresent);
    if (carritos.length > 0) {
      const carritoCollectionIdentifiers = carritoCollection.map(carritoItem => getCarritoIdentifier(carritoItem)!);
      const carritosToAdd = carritos.filter(carritoItem => {
        const carritoIdentifier = getCarritoIdentifier(carritoItem);
        if (carritoIdentifier == null || carritoCollectionIdentifiers.includes(carritoIdentifier)) {
          return false;
        }
        carritoCollectionIdentifiers.push(carritoIdentifier);
        return true;
      });
      return [...carritosToAdd, ...carritoCollection];
    }
    return carritoCollection;
  }

  protected convertDateFromClient(carrito: ICarrito): ICarrito {
    return Object.assign({}, carrito, {
      fechaCarrito: carrito.fechaCarrito?.isValid() ? carrito.fechaCarrito.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.fechaCarrito = res.body.fechaCarrito ? dayjs(res.body.fechaCarrito) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((carrito: ICarrito) => {
        carrito.fechaCarrito = carrito.fechaCarrito ? dayjs(carrito.fechaCarrito) : undefined;
      });
    }
    return res;
  }
}
