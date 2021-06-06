import { IFactura } from 'app/entities/factura/factura.model';

export interface ICompra {
  id?: number;
  cantidadComprada?: number | null;
  precioPagado?: number | null;
  factura?: IFactura | null;
}

export class Compra implements ICompra {
  constructor(
    public id?: number,
    public cantidadComprada?: number | null,
    public precioPagado?: number | null,
    public factura?: IFactura | null
  ) {}
}

export function getCompraIdentifier(compra: ICompra): number | undefined {
  return compra.id;
}
