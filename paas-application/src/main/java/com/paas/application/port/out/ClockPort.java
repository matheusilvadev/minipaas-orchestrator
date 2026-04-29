package com.paas.application.port.out;

import java.time.LocalDateTime;

public interface ClockPort {
    /**
     * @return O instante atual do sistema
     */
    LocalDateTime now();
}
