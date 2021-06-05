package es.yuliq.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link es.yuliq.domain.Banner} entity. This class is used
 * in {@link es.yuliq.web.rest.BannerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /banners?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BannerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter fechaPuesta;

    public BannerCriteria() {}

    public BannerCriteria(BannerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fechaPuesta = other.fechaPuesta == null ? null : other.fechaPuesta.copy();
    }

    @Override
    public BannerCriteria copy() {
        return new BannerCriteria(this);
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

    public InstantFilter getFechaPuesta() {
        return fechaPuesta;
    }

    public InstantFilter fechaPuesta() {
        if (fechaPuesta == null) {
            fechaPuesta = new InstantFilter();
        }
        return fechaPuesta;
    }

    public void setFechaPuesta(InstantFilter fechaPuesta) {
        this.fechaPuesta = fechaPuesta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BannerCriteria that = (BannerCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(fechaPuesta, that.fechaPuesta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fechaPuesta);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BannerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fechaPuesta != null ? "fechaPuesta=" + fechaPuesta + ", " : "") +
            "}";
    }
}
