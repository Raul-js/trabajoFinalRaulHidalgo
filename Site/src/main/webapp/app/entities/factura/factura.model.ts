import * as dayjs from 'dayjs';
import { ICompra } from 'app/entities/compra/compra.model';
import { IUser } from 'app/entities/user/user.model';

export interface IFactura {
  id?: number;
  fechaFactura?: dayjs.Dayjs | null;
  cantidadPagada?: number | null;
  compras?: ICompra[] | null;
  assignedTo?: IUser | null;
}

export class Factura implements IFactura {
  constructor(
    public id?: number,
    public fechaFactura?: dayjs.Dayjs | null,
    public cantidadPagada?: number | null,
    public compras?: ICompra[] | null,
    public assignedTo?: IUser | null
  ) {}
}

export function getFacturaIdentifier(factura: IFactura): number | undefined {
  return factura.id;
}
