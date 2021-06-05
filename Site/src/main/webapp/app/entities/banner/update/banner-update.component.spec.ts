jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BannerService } from '../service/banner.service';
import { IBanner, Banner } from '../banner.model';

import { BannerUpdateComponent } from './banner-update.component';

describe('Component Tests', () => {
  describe('Banner Management Update Component', () => {
    let comp: BannerUpdateComponent;
    let fixture: ComponentFixture<BannerUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let bannerService: BannerService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BannerUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BannerUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BannerUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      bannerService = TestBed.inject(BannerService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const banner: IBanner = { id: 456 };

        activatedRoute.data = of({ banner });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(banner));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const banner = { id: 123 };
        spyOn(bannerService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ banner });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: banner }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(bannerService.update).toHaveBeenCalledWith(banner);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const banner = new Banner();
        spyOn(bannerService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ banner });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: banner }));
        saveSubject.complete();

        // THEN
        expect(bannerService.create).toHaveBeenCalledWith(banner);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const banner = { id: 123 };
        spyOn(bannerService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ banner });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(bannerService.update).toHaveBeenCalledWith(banner);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
