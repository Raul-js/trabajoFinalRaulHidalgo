import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DataUtils } from 'app/core/util/data-util.service';

import { ProductoDetailComponent } from './producto-detail.component';

describe('Component Tests', () => {
  describe('Producto Management Detail Component', () => {
    let comp: ProductoDetailComponent;
    let fixture: ComponentFixture<ProductoDetailComponent>;
    let dataUtils: DataUtils;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ProductoDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ producto: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ProductoDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ProductoDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = TestBed.inject(DataUtils);
    });

    describe('OnInit', () => {
      it('Should load producto on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.producto).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from DataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from DataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeBase64, fakeContentType);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeBase64, fakeContentType);
      });
    });
  });
});
