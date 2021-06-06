import * as dayjs from 'dayjs';

export class FacturaFilter {
  constructor(
    public field_dateFrom?: dayjs.Dayjs | null,
    public field_dateto?: dayjs.Dayjs | null,
    public id?: number,
    public user?: number
  ) {}

  toMap(): any {
    const map = new Map();

    if (this.field_dateFrom?.toString() != null) {
      map.set('fechaFactura.greaterThanOrEqual', dayjs(this.field_dateFrom).toISOString());
    }

    if (this.field_dateto?.toString() != null) {
      map.set('fechaFactura.lessThanOrEqual', dayjs(this.field_dateto).toISOString());
    }
    if (this.id != null) {
      map.set('id.equals', this.id);
    }

    return map;
  }
}
