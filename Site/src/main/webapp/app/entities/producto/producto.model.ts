import { ICompra } from 'app/entities/compra/compra.model';
import { ICarrito } from 'app/entities/carrito/carrito.model';
import { TipoProducto } from 'app/entities/enumerations/tipo-producto.model';

export interface IProducto {
  id?: number;
  nombreProducto?: string;
  calorias?: number | null;
  imagenContentType?: string | null;
  imagen?: string | null;
  precio?: number;
  existencias?: number;
  tipoproducto?: TipoProducto | null;
  compra?: ICompra | null;
  carritos?: ICarrito[] | null;
}

export class Producto implements IProducto {
  constructor(
    public id?: number,
    public nombreProducto?: string,
    public calorias?: number | null,
    public imagenContentType?: string | null,
    public imagen?: string | null,
    public precio?: number,
    public existencias?: number,
    public tipoproducto?: TipoProducto | null,
    public compra?: ICompra | null,
    public carritos?: ICarrito[] | null
  ) {}
}

export function getProductoIdentifier(producto: IProducto): number | undefined {
  return producto.id;
}
