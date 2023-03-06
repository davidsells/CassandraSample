package com.persist.simple.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.persist.simple.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ParcelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parcel.class);
        Parcel parcel1 = new Parcel();
        parcel1.setId(UUID.randomUUID());
        Parcel parcel2 = new Parcel();
        parcel2.setId(parcel1.getId());
        assertThat(parcel1).isEqualTo(parcel2);
        parcel2.setId(UUID.randomUUID());
        assertThat(parcel1).isNotEqualTo(parcel2);
        parcel1.setId(null);
        assertThat(parcel1).isNotEqualTo(parcel2);
    }
}
