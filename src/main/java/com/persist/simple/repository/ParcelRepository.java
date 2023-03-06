package com.persist.simple.repository;

import com.persist.simple.domain.Parcel;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Cassandra repository for the Parcel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParcelRepository extends CassandraRepository<Parcel, UUID> {}
