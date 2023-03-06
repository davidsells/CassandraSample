package com.persist.simple.repository;

import com.persist.simple.domain.Container;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Cassandra repository for the Container entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContainerRepository extends CassandraRepository<Container, UUID> {}
