package com.persist.simple.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.persist.simple.IntegrationTest;
import com.persist.simple.domain.Container;
import com.persist.simple.domain.enumeration.ContainerSize;
import com.persist.simple.repository.ContainerRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link ContainerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContainerResourceIT {

    private static final String DEFAULT_CONTAINER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTAINER_NUMBER = "BBBBBBBBBB";

    private static final ContainerSize DEFAULT_SIZE = ContainerSize.SMALL;
    private static final ContainerSize UPDATED_SIZE = ContainerSize.MEDIUM;

    private static final String ENTITY_API_URL = "/api/containers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private MockMvc restContainerMockMvc;

    private Container container;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Container createEntity() {
        Container container = new Container().containerNumber(DEFAULT_CONTAINER_NUMBER).size(DEFAULT_SIZE);
        return container;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Container createUpdatedEntity() {
        Container container = new Container().containerNumber(UPDATED_CONTAINER_NUMBER).size(UPDATED_SIZE);
        return container;
    }

    @BeforeEach
    public void initTest() {
        containerRepository.deleteAll();
        container = createEntity();
    }

    @Test
    void createContainer() throws Exception {
        int databaseSizeBeforeCreate = containerRepository.findAll().size();
        // Create the Container
        restContainerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(container)))
            .andExpect(status().isCreated());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeCreate + 1);
        Container testContainer = containerList.get(containerList.size() - 1);
        assertThat(testContainer.getContainerNumber()).isEqualTo(DEFAULT_CONTAINER_NUMBER);
        assertThat(testContainer.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void createContainerWithExistingId() throws Exception {
        // Create the Container with an existing ID
        container.setId(UUID.randomUUID());

        int databaseSizeBeforeCreate = containerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContainerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(container)))
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllContainers() throws Exception {
        // Initialize the database
        container.setId(UUID.randomUUID());
        containerRepository.save(container);

        // Get all the containerList
        restContainerMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(container.getId().toString())))
            .andExpect(jsonPath("$.[*].containerNumber").value(hasItem(DEFAULT_CONTAINER_NUMBER)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())));
    }

    @Test
    void getContainer() throws Exception {
        // Initialize the database
        container.setId(UUID.randomUUID());
        containerRepository.save(container);

        // Get the container
        restContainerMockMvc
            .perform(get(ENTITY_API_URL_ID, container.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(container.getId().toString()))
            .andExpect(jsonPath("$.containerNumber").value(DEFAULT_CONTAINER_NUMBER))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.toString()));
    }

    @Test
    void getNonExistingContainer() throws Exception {
        // Get the container
        restContainerMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    void putExistingContainer() throws Exception {
        // Initialize the database
        container.setId(UUID.randomUUID());
        containerRepository.save(container);

        int databaseSizeBeforeUpdate = containerRepository.findAll().size();

        // Update the container
        Container updatedContainer = containerRepository.findById(container.getId()).get();
        updatedContainer.containerNumber(UPDATED_CONTAINER_NUMBER).size(UPDATED_SIZE);

        restContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContainer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedContainer))
            )
            .andExpect(status().isOk());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
        Container testContainer = containerList.get(containerList.size() - 1);
        assertThat(testContainer.getContainerNumber()).isEqualTo(UPDATED_CONTAINER_NUMBER);
        assertThat(testContainer.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void putNonExistingContainer() throws Exception {
        int databaseSizeBeforeUpdate = containerRepository.findAll().size();
        container.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, container.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(container))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchContainer() throws Exception {
        int databaseSizeBeforeUpdate = containerRepository.findAll().size();
        container.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(container))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamContainer() throws Exception {
        int databaseSizeBeforeUpdate = containerRepository.findAll().size();
        container.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(container)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateContainerWithPatch() throws Exception {
        // Initialize the database
        container.setId(UUID.randomUUID());
        containerRepository.save(container);

        int databaseSizeBeforeUpdate = containerRepository.findAll().size();

        // Update the container using partial update
        Container partialUpdatedContainer = new Container();
        partialUpdatedContainer.setId(container.getId());

        partialUpdatedContainer.size(UPDATED_SIZE);

        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContainer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContainer))
            )
            .andExpect(status().isOk());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
        Container testContainer = containerList.get(containerList.size() - 1);
        assertThat(testContainer.getContainerNumber()).isEqualTo(DEFAULT_CONTAINER_NUMBER);
        assertThat(testContainer.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void fullUpdateContainerWithPatch() throws Exception {
        // Initialize the database
        container.setId(UUID.randomUUID());
        containerRepository.save(container);

        int databaseSizeBeforeUpdate = containerRepository.findAll().size();

        // Update the container using partial update
        Container partialUpdatedContainer = new Container();
        partialUpdatedContainer.setId(container.getId());

        partialUpdatedContainer.containerNumber(UPDATED_CONTAINER_NUMBER).size(UPDATED_SIZE);

        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContainer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContainer))
            )
            .andExpect(status().isOk());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
        Container testContainer = containerList.get(containerList.size() - 1);
        assertThat(testContainer.getContainerNumber()).isEqualTo(UPDATED_CONTAINER_NUMBER);
        assertThat(testContainer.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void patchNonExistingContainer() throws Exception {
        int databaseSizeBeforeUpdate = containerRepository.findAll().size();
        container.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, container.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(container))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchContainer() throws Exception {
        int databaseSizeBeforeUpdate = containerRepository.findAll().size();
        container.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(container))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamContainer() throws Exception {
        int databaseSizeBeforeUpdate = containerRepository.findAll().size();
        container.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(container))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Container in the database
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteContainer() throws Exception {
        // Initialize the database
        container.setId(UUID.randomUUID());
        containerRepository.save(container);

        int databaseSizeBeforeDelete = containerRepository.findAll().size();

        // Delete the container
        restContainerMockMvc
            .perform(delete(ENTITY_API_URL_ID, container.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Container> containerList = containerRepository.findAll();
        assertThat(containerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
