package com.persist.simple.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.persist.simple.IntegrationTest;
import com.persist.simple.domain.Parcel;
import com.persist.simple.domain.enumeration.ParcelSize;
import com.persist.simple.repository.ParcelRepository;
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
 * Integration tests for the {@link ParcelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParcelResourceIT {

    private static final String DEFAULT_PARCEL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PARCEL_NUMBER = "BBBBBBBBBB";

    private static final ParcelSize DEFAULT_SIZE = ParcelSize.SMALL;
    private static final ParcelSize UPDATED_SIZE = ParcelSize.MEDIUM;

    private static final String ENTITY_API_URL = "/api/parcels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private MockMvc restParcelMockMvc;

    private Parcel parcel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcel createEntity() {
        Parcel parcel = new Parcel().parcelNumber(DEFAULT_PARCEL_NUMBER).size(DEFAULT_SIZE);
        return parcel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcel createUpdatedEntity() {
        Parcel parcel = new Parcel().parcelNumber(UPDATED_PARCEL_NUMBER).size(UPDATED_SIZE);
        return parcel;
    }

    @BeforeEach
    public void initTest() {
        parcelRepository.deleteAll();
        parcel = createEntity();
    }

    @Test
    void createParcel() throws Exception {
        int databaseSizeBeforeCreate = parcelRepository.findAll().size();
        // Create the Parcel
        restParcelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parcel)))
            .andExpect(status().isCreated());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeCreate + 1);
        Parcel testParcel = parcelList.get(parcelList.size() - 1);
        assertThat(testParcel.getParcelNumber()).isEqualTo(DEFAULT_PARCEL_NUMBER);
        assertThat(testParcel.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void createParcelWithExistingId() throws Exception {
        // Create the Parcel with an existing ID
        parcel.setId(UUID.randomUUID());

        int databaseSizeBeforeCreate = parcelRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParcelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parcel)))
            .andExpect(status().isBadRequest());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllParcels() throws Exception {
        // Initialize the database
        parcel.setId(UUID.randomUUID());
        parcelRepository.save(parcel);

        // Get all the parcelList
        restParcelMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parcel.getId().toString())))
            .andExpect(jsonPath("$.[*].parcelNumber").value(hasItem(DEFAULT_PARCEL_NUMBER)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())));
    }

    @Test
    void getParcel() throws Exception {
        // Initialize the database
        parcel.setId(UUID.randomUUID());
        parcelRepository.save(parcel);

        // Get the parcel
        restParcelMockMvc
            .perform(get(ENTITY_API_URL_ID, parcel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parcel.getId().toString()))
            .andExpect(jsonPath("$.parcelNumber").value(DEFAULT_PARCEL_NUMBER))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.toString()));
    }

    @Test
    void getNonExistingParcel() throws Exception {
        // Get the parcel
        restParcelMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    void putExistingParcel() throws Exception {
        // Initialize the database
        parcel.setId(UUID.randomUUID());
        parcelRepository.save(parcel);

        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();

        // Update the parcel
        Parcel updatedParcel = parcelRepository.findById(parcel.getId()).get();
        updatedParcel.parcelNumber(UPDATED_PARCEL_NUMBER).size(UPDATED_SIZE);

        restParcelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedParcel.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedParcel))
            )
            .andExpect(status().isOk());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
        Parcel testParcel = parcelList.get(parcelList.size() - 1);
        assertThat(testParcel.getParcelNumber()).isEqualTo(UPDATED_PARCEL_NUMBER);
        assertThat(testParcel.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void putNonExistingParcel() throws Exception {
        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();
        parcel.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParcelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parcel.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchParcel() throws Exception {
        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();
        parcel.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamParcel() throws Exception {
        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();
        parcel.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parcel)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParcelWithPatch() throws Exception {
        // Initialize the database
        parcel.setId(UUID.randomUUID());
        parcelRepository.save(parcel);

        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();

        // Update the parcel using partial update
        Parcel partialUpdatedParcel = new Parcel();
        partialUpdatedParcel.setId(parcel.getId());

        partialUpdatedParcel.size(UPDATED_SIZE);

        restParcelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParcel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParcel))
            )
            .andExpect(status().isOk());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
        Parcel testParcel = parcelList.get(parcelList.size() - 1);
        assertThat(testParcel.getParcelNumber()).isEqualTo(DEFAULT_PARCEL_NUMBER);
        assertThat(testParcel.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void fullUpdateParcelWithPatch() throws Exception {
        // Initialize the database
        parcel.setId(UUID.randomUUID());
        parcelRepository.save(parcel);

        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();

        // Update the parcel using partial update
        Parcel partialUpdatedParcel = new Parcel();
        partialUpdatedParcel.setId(parcel.getId());

        partialUpdatedParcel.parcelNumber(UPDATED_PARCEL_NUMBER).size(UPDATED_SIZE);

        restParcelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParcel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParcel))
            )
            .andExpect(status().isOk());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
        Parcel testParcel = parcelList.get(parcelList.size() - 1);
        assertThat(testParcel.getParcelNumber()).isEqualTo(UPDATED_PARCEL_NUMBER);
        assertThat(testParcel.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void patchNonExistingParcel() throws Exception {
        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();
        parcel.setId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParcelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parcel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parcel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchParcel() throws Exception {
        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();
        parcel.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parcel))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamParcel() throws Exception {
        int databaseSizeBeforeUpdate = parcelRepository.findAll().size();
        parcel.setId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(parcel)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parcel in the database
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteParcel() throws Exception {
        // Initialize the database
        parcel.setId(UUID.randomUUID());
        parcelRepository.save(parcel);

        int databaseSizeBeforeDelete = parcelRepository.findAll().size();

        // Delete the parcel
        restParcelMockMvc
            .perform(delete(ENTITY_API_URL_ID, parcel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Parcel> parcelList = parcelRepository.findAll();
        assertThat(parcelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
