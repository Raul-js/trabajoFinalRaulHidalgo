import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBanner, Banner } from '../banner.model';
import { BannerService } from '../service/banner.service';

@Injectable({ providedIn: 'root' })
export class BannerRoutingResolveService implements Resolve<IBanner> {
  constructor(protected service: BannerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBanner> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((banner: HttpResponse<Banner>) => {
          if (banner.body) {
            return of(banner.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Banner());
  }
}
