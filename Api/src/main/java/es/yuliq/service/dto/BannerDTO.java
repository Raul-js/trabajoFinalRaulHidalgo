package es.yuliq.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link es.yuliq.domain.Banner} entity.
 */
public class BannerDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] imagen;

    private String imagenContentType;

    @NotNull
    private Instant fechaPuesta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenContentType() {
        return imagenContentType;
    }

    public void setImagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
    }

    public Instant getFechaPuesta() {
        return fechaPuesta;
    }

    public void setFechaPuesta(Instant fechaPuesta) {
        this.fechaPuesta = fechaPuesta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BannerDTO)) {
            return false;
        }

        BannerDTO bannerDTO = (BannerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bannerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BannerDTO{" +
            "id=" + getId() +
            ", imagen='" + getImagen() + "'" +
            ", fechaPuesta='" + getFechaPuesta() + "'" +
            "}";
    }
}
