package com.persist.simple.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.persist.simple.domain.enumeration.ContainerSize;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * A Container.
 */
// Since spring-data-cassandra 3.4.2, table names needs to be in lowercase
// See https://github.com/spring-projects/spring-data-cassandra/issues/1293#issuecomment-1192555467
@Table("container")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Container implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    private String containerNumber;

    private ContainerSize size;

    @JsonIgnoreProperties(value = { "container" }, allowSetters = true)
    private Set<Parcel> parcels = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Container id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContainerNumber() {
        return this.containerNumber;
    }

    public Container containerNumber(String containerNumber) {
        this.setContainerNumber(containerNumber);
        return this;
    }

    public void setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
    }

    public ContainerSize getSize() {
        return this.size;
    }

    public Container size(ContainerSize size) {
        this.setSize(size);
        return this;
    }

    public void setSize(ContainerSize size) {
        this.size = size;
    }

    public Set<Parcel> getParcels() {
        return this.parcels;
    }

    public void setParcels(Set<Parcel> parcels) {
        if (this.parcels != null) {
            this.parcels.forEach(i -> i.setContainer(null));
        }
        if (parcels != null) {
            parcels.forEach(i -> i.setContainer(this));
        }
        this.parcels = parcels;
    }

    public Container parcels(Set<Parcel> parcels) {
        this.setParcels(parcels);
        return this;
    }

    public Container addParcel(Parcel parcel) {
        this.parcels.add(parcel);
        parcel.setContainer(this);
        return this;
    }

    public Container removeParcel(Parcel parcel) {
        this.parcels.remove(parcel);
        parcel.setContainer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Container)) {
            return false;
        }
        return id != null && id.equals(((Container) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Container{" +
            "id=" + getId() +
            ", containerNumber='" + getContainerNumber() + "'" +
            ", size='" + getSize() + "'" +
            "}";
    }
}
