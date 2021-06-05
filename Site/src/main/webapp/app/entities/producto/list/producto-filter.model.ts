export class ProductoFilter {
  constructor(
    public nombre?: string,
    public calorias?: number,
    public precio?: number,
    public tipo?: string,
    public existencias?: number
  ) {}

  toMap(): any {
    const map = new Map();

    if (this.nombre != null && this.nombre !== '') {
      map.set('nombreProducto.contains', this.nombre);
    }
    if (this.calorias != null) {
      map.set('calorias.lessThan', this.calorias);
    }
    if (this.precio != null) {
      map.set('precio.lessThan', this.precio);
    }
    if (this.existencias != null) {
      map.set('existencias.lessThan', this.existencias);
    }

    return map;
  }
}
