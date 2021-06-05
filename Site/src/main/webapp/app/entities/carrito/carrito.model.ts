import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';
import { IProducto } from 'app/entities/producto/producto.model';

export interface ICarrito {
  id?: number;
  cantidad?: number | null;
  fechaCarrito?: dayjs.Dayjs | null;
  assignedTo?: IUser | null;
  productos?: IProducto[] | null;
}

export class Carrito implements ICarrito {
  constructor(
    public id?: number,
    public cantidad?: number | null,
    public fechaCarrito?: dayjs.Dayjs | null,
    public assignedTo?: IUser | null,
    public productos?: IProducto[] | null
  ) {}
}

export function getCarritoIdentifier(carrito: ICarrito): number | undefined {
  return carrito.id;
}
