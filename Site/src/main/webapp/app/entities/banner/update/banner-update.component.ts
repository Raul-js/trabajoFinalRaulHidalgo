import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IBanner, Banner } from '../banner.model';
import { BannerService } from '../service/banner.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-banner-update',
  templateUrl: './banner-update.component.html',
})
export class BannerUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    imagen: [null, []],
    imagenContentType: [],
    fechaPuesta: [null, [Validators.required]],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected bannerService: BannerService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ banner }) => {
      if (banner.id === undefined) {
        const today = dayjs().startOf('day');
        banner.fechaPuesta = today;
      }

      this.updateForm(banner);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('yuliqApp.error', { ...err, key: 'error.file.' + err.key })
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const banner = this.createFromForm();
    if (banner.id !== undefined) {
      this.subscribeToSaveResponse(this.bannerService.update(banner));
    } else {
      this.subscribeToSaveResponse(this.bannerService.create(banner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBanner>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(banner: IBanner): void {
    this.editForm.patchValue({
      id: banner.id,
      imagen: banner.imagen,
      imagenContentType: banner.imagenContentType,
      fechaPuesta: banner.fechaPuesta ? banner.fechaPuesta.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IBanner {
    return {
      ...new Banner(),
      id: this.editForm.get(['id'])!.value,
      imagenContentType: this.editForm.get(['imagenContentType'])!.value,
      imagen: this.editForm.get(['imagen'])!.value,
      fechaPuesta: this.editForm.get(['fechaPuesta'])!.value
        ? dayjs(this.editForm.get(['fechaPuesta'])!.value, DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
