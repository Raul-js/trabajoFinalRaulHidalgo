package es.yuliq.service.criteria;

import es.yuliq.domain.enumeration.TipoProducto;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link es.yuliq.domain.Producto} entity. This class is used
 * in {@link es.yuliq.web.rest.ProductoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /productos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TipoProducto
     */
    public static class TipoProductoFilter extends Filter<TipoProducto> {

        public TipoProductoFilter() {}

        public TipoProductoFilter(TipoProductoFilter filter) {
            super(filter);
        }

        @Override
        public TipoProductoFilter copy() {
            return new TipoProductoFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nombreProducto;

    private IntegerFilter calorias;

    private FloatFilter precio;

    private IntegerFilter existencias;

    private TipoProductoFilter tipoproducto;

    private LongFilter compraId;

    private LongFilter carritoId;

    public ProductoCriteria() {}

    public ProductoCriteria(ProductoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nombreProducto = other.nombreProducto == null ? null : other.nombreProducto.copy();
        this.calorias = other.calorias == null ? null : other.calorias.copy();
        this.precio = other.precio == null ? null : other.precio.copy();
        this.existencias = other.existencias == null ? null : other.existencias.copy();
        this.tipoproducto = other.tipoproducto == null ? null : other.tipoproducto.copy();
        this.compraId = other.compraId == null ? null : other.compraId.copy();
        this.carritoId = other.carritoId == null ? null : other.carritoId.copy();
    }

    @Override
    public ProductoCriteria copy() {
        return new ProductoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNombreProducto() {
        return nombreProducto;
    }

    public StringFilter nombreProducto() {
        if (nombreProducto == null) {
            nombreProducto = new StringFilter();
        }
        return nombreProducto;
    }

    public void setNombreProducto(StringFilter nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public IntegerFilter getCalorias() {
        return calorias;
    }

    public IntegerFilter calorias() {
        if (calorias == null) {
            calorias = new IntegerFilter();
        }
        return calorias;
    }

    public void setCalorias(IntegerFilter calorias) {
        this.calorias = calorias;
    }

    public FloatFilter getPrecio() {
        return precio;
    }

    public FloatFilter precio() {
        if (precio == null) {
            precio = new FloatFilter();
        }
        return precio;
    }

    public void setPrecio(FloatFilter precio) {
        this.precio = precio;
    }

    public IntegerFilter getExistencias() {
        return existencias;
    }

    public IntegerFilter existencias() {
        if (existencias == null) {
            existencias = new IntegerFilter();
        }
        return existencias;
    }

    public void setExistencias(IntegerFilter existencias) {
        this.existencias = existencias;
    }

    public TipoProductoFilter getTipoproducto() {
        return tipoproducto;
    }

    public TipoProductoFilter tipoproducto() {
        if (tipoproducto == null) {
            tipoproducto = new TipoProductoFilter();
        }
        return tipoproducto;
    }

    public void setTipoproducto(TipoProductoFilter tipoproducto) {
        this.tipoproducto = tipoproducto;
    }

    public LongFilter getCompraId() {
        return compraId;
    }

    public LongFilter compraId() {
        if (compraId == null) {
            compraId = new LongFilter();
        }
        return compraId;
    }

    public void setCompraId(LongFilter compraId) {
        this.compraId = compraId;
    }

    public LongFilter getCarritoId() {
        return carritoId;
    }

    public LongFilter carritoId() {
        if (carritoId == null) {
            carritoId = new LongFilter();
        }
        return carritoId;
    }

    public void setCarritoId(LongFilter carritoId) {
        this.carritoId = carritoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductoCriteria that = (ProductoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nombreProducto, that.nombreProducto) &&
            Objects.equals(calorias, that.calorias) &&
            Objects.equals(precio, that.precio) &&
            Objects.equals(existencias, that.existencias) &&
            Objects.equals(tipoproducto, that.tipoproducto) &&
            Objects.equals(compraId, that.compraId) &&
            Objects.equals(carritoId, that.carritoId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreProducto, calorias, precio, existencias, tipoproducto, compraId, carritoId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nombreProducto != null ? "nombreProducto=" + nombreProducto + ", " : "") +
            (calorias != null ? "calorias=" + calorias + ", " : "") +
            (precio != null ? "precio=" + precio + ", " : "") +
            (existencias != null ? "existencias=" + existencias + ", " : "") +
            (tipoproducto != null ? "tipoproducto=" + tipoproducto + ", " : "") +
            (compraId != null ? "compraId=" + compraId + ", " : "") +
            (carritoId != null ? "carritoId=" + carritoId + ", " : "") +
            "}";
    }
}
