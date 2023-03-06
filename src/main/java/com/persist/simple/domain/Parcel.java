package com.persist.simple.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.persist.simple.domain.enumeration.ParcelSize;
import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * A Parcel.
 */
// Since spring-data-cassandra 3.4.2, table names needs to be in lowercase
// See https://github.com/spring-projects/spring-data-cassandra/issues/1293#issuecomment-1192555467
@Table("parcel")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Parcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    private String parcelNumber;

    private ParcelSize size;

    @JsonIgnoreProperties(value = { "parcels" }, allowSetters = true)
    private Container container;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Parcel id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getParcelNumber() {
        return this.parcelNumber;
    }

    public Parcel parcelNumber(String parcelNumber) {
        this.setParcelNumber(parcelNumber);
        return this;
    }

    public void setParcelNumber(String parcelNumber) {
        this.parcelNumber = parcelNumber;
    }

    public ParcelSize getSize() {
        return this.size;
    }

    public Parcel size(ParcelSize size) {
        this.setSize(size);
        return this;
    }

    public void setSize(ParcelSize size) {
        this.size = size;
    }

    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Parcel container(Container container) {
        this.setContainer(container);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parcel)) {
            return false;
        }
        return id != null && id.equals(((Parcel) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parcel{" +
            "id=" + getId() +
            ", parcelNumber='" + getParcelNumber() + "'" +
            ", size='" + getSize() + "'" +
            "}";
    }
}
