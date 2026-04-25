package com.paas.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortNumberTest {

    @Test
    void shouldCreatePortNumberWhenValueIsValid() {
        PortNumber portNumber = new PortNumber(8080);

        assertThat(portNumber).isNotNull();
        assertThat(portNumber.value()).isEqualTo(8080);
    }

    @Test
    void shouldRejectPortNumberWhenValueIsLessThanOne() {
        assertThatThrownBy(() -> new PortNumber(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectPortNumberWhenValueIsGreaterThan65535() {
        assertThatThrownBy(() -> new PortNumber(65536))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
