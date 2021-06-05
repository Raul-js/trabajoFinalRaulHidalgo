import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBanner, getBannerIdentifier } from '../banner.model';

export type EntityResponseType = HttpResponse<IBanner>;
export type EntityArrayResponseType = HttpResponse<IBanner[]>;

@Injectable({ providedIn: 'root' })
export class BannerService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/banners');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(banner: IBanner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(banner);
    return this.http
      .post<IBanner>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(banner: IBanner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(banner);
    return this.http
      .put<IBanner>(`${this.resourceUrl}/${getBannerIdentifier(banner) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(banner: IBanner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(banner);
    return this.http
      .patch<IBanner>(`${this.resourceUrl}/${getBannerIdentifier(banner) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBanner>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBanner[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBannerToCollectionIfMissing(bannerCollection: IBanner[], ...bannersToCheck: (IBanner | null | undefined)[]): IBanner[] {
    const banners: IBanner[] = bannersToCheck.filter(isPresent);
    if (banners.length > 0) {
      const bannerCollectionIdentifiers = bannerCollection.map(bannerItem => getBannerIdentifier(bannerItem)!);
      const bannersToAdd = banners.filter(bannerItem => {
        const bannerIdentifier = getBannerIdentifier(bannerItem);
        if (bannerIdentifier == null || bannerCollectionIdentifiers.includes(bannerIdentifier)) {
          return false;
        }
        bannerCollectionIdentifiers.push(bannerIdentifier);
        return true;
      });
      return [...bannersToAdd, ...bannerCollection];
    }
    return bannerCollection;
  }

  protected convertDateFromClient(banner: IBanner): IBanner {
    return Object.assign({}, banner, {
      fechaPuesta: banner.fechaPuesta?.isValid() ? banner.fechaPuesta.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.fechaPuesta = res.body.fechaPuesta ? dayjs(res.body.fechaPuesta) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((banner: IBanner) => {
        banner.fechaPuesta = banner.fechaPuesta ? dayjs(banner.fechaPuesta) : undefined;
      });
    }
    return res;
  }
}
