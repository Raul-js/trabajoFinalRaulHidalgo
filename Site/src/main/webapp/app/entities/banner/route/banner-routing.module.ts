import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BannerComponent } from '../list/banner.component';
import { BannerDetailComponent } from '../detail/banner-detail.component';
import { BannerUpdateComponent } from '../update/banner-update.component';
import { BannerRoutingResolveService } from './banner-routing-resolve.service';

const bannerRoute: Routes = [
  {
    path: '',
    component: BannerComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BannerDetailComponent,
    resolve: {
      banner: BannerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BannerUpdateComponent,
    resolve: {
      banner: BannerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BannerUpdateComponent,
    resolve: {
      banner: BannerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bannerRoute)],
  exports: [RouterModule],
})
export class BannerRoutingModule {}
