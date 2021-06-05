import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CarritoDetailComponent } from './carrito-detail.component';

describe('Component Tests', () => {
  describe('Carrito Management Detail Component', () => {
    let comp: CarritoDetailComponent;
    let fixture: ComponentFixture<CarritoDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CarritoDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ carrito: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CarritoDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CarritoDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load carrito on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.carrito).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
