package com.persist.simple.web.rest;

import com.persist.simple.domain.Container;
import com.persist.simple.repository.ContainerRepository;
import com.persist.simple.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.persist.simple.domain.Container}.
 */
@RestController
@RequestMapping("/api")
public class ContainerResource {

    private final Logger log = LoggerFactory.getLogger(ContainerResource.class);

    private static final String ENTITY_NAME = "container";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContainerRepository containerRepository;

    public ContainerResource(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    /**
     * {@code POST  /containers} : Create a new container.
     *
     * @param container the container to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new container, or with status {@code 400 (Bad Request)} if the container has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/containers")
    public ResponseEntity<Container> createContainer(@RequestBody Container container) throws URISyntaxException {
        log.debug("REST request to save Container : {}", container);
        if (container.getId() != null) {
            throw new BadRequestAlertException("A new container cannot already have an ID", ENTITY_NAME, "idexists");
        }
        container.setId(UUID.randomUUID());
        Container result = containerRepository.save(container);
        return ResponseEntity
            .created(new URI("/api/containers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /containers/:id} : Updates an existing container.
     *
     * @param id the id of the container to save.
     * @param container the container to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated container,
     * or with status {@code 400 (Bad Request)} if the container is not valid,
     * or with status {@code 500 (Internal Server Error)} if the container couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/containers/{id}")
    public ResponseEntity<Container> updateContainer(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody Container container
    ) throws URISyntaxException {
        log.debug("REST request to update Container : {}, {}", id, container);
        if (container.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, container.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!containerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Container result = containerRepository.save(container);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, container.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /containers/:id} : Partial updates given fields of an existing container, field will ignore if it is null
     *
     * @param id the id of the container to save.
     * @param container the container to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated container,
     * or with status {@code 400 (Bad Request)} if the container is not valid,
     * or with status {@code 404 (Not Found)} if the container is not found,
     * or with status {@code 500 (Internal Server Error)} if the container couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/containers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Container> partialUpdateContainer(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody Container container
    ) throws URISyntaxException {
        log.debug("REST request to partial update Container partially : {}, {}", id, container);
        if (container.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, container.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!containerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Container> result = containerRepository
            .findById(container.getId())
            .map(existingContainer -> {
                if (container.getContainerNumber() != null) {
                    existingContainer.setContainerNumber(container.getContainerNumber());
                }
                if (container.getSize() != null) {
                    existingContainer.setSize(container.getSize());
                }

                return existingContainer;
            })
            .map(containerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, container.getId().toString())
        );
    }

    /**
     * {@code GET  /containers} : get all the containers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of containers in body.
     */
    @GetMapping("/containers")
    public List<Container> getAllContainers() {
        log.debug("REST request to get all Containers");
        return containerRepository.findAll();
    }

    /**
     * {@code GET  /containers/:id} : get the "id" container.
     *
     * @param id the id of the container to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the container, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/containers/{id}")
    public ResponseEntity<Container> getContainer(@PathVariable UUID id) {
        log.debug("REST request to get Container : {}", id);
        Optional<Container> container = containerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(container);
    }

    /**
     * {@code DELETE  /containers/:id} : delete the "id" container.
     *
     * @param id the id of the container to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/containers/{id}")
    public ResponseEntity<Void> deleteContainer(@PathVariable UUID id) {
        log.debug("REST request to delete Container : {}", id);
        containerRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
