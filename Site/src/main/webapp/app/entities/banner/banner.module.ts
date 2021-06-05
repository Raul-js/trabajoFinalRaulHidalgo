import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { BannerComponent } from './list/banner.component';
import { BannerDetailComponent } from './detail/banner-detail.component';
import { BannerUpdateComponent } from './update/banner-update.component';
import { BannerDeleteDialogComponent } from './delete/banner-delete-dialog.component';
import { BannerRoutingModule } from './route/banner-routing.module';

@NgModule({
  imports: [SharedModule, BannerRoutingModule],
  declarations: [BannerComponent, BannerDetailComponent, BannerUpdateComponent, BannerDeleteDialogComponent],
  entryComponents: [BannerDeleteDialogComponent],
})
export class BannerModule {}
