export class ProductoFilter {
  constructor(public nombre?: string) {}

  toMap(): any {
    const map = new Map();

    if (this.nombre != null && this.nombre !== '') {
      map.set('nombreProducto.contains', this.nombre);
      /*eslint-disable*/
      console.log(map);
    }
    return map;
  }
}
