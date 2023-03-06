package com.persist.simple.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.persist.simple.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ContainerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Container.class);
        Container container1 = new Container();
        container1.setId(UUID.randomUUID());
        Container container2 = new Container();
        container2.setId(container1.getId());
        assertThat(container1).isEqualTo(container2);
        container2.setId(UUID.randomUUID());
        assertThat(container1).isNotEqualTo(container2);
        container1.setId(null);
        assertThat(container1).isNotEqualTo(container2);
    }
}
