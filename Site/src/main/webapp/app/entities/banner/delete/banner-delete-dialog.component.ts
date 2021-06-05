import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBanner } from '../banner.model';
import { BannerService } from '../service/banner.service';

@Component({
  templateUrl: './banner-delete-dialog.component.html',
})
export class BannerDeleteDialogComponent {
  banner?: IBanner;

  constructor(protected bannerService: BannerService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bannerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
