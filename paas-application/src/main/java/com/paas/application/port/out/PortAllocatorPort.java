package com.paas.application.port.out;

import com.paas.domain.valueobject.PortNumber;

public interface PortAllocatorPort {
    /**
     * Reserva uma porta disponível para um novo container.
     * 
     * @return O PortNumber (VO do domain) alocado.
     */
    PortNumber allocate();

    /**
     * Libera uma porta quando um container é parado.
     * 
     * @param port A porta a ser liberada.
     */
    void release(PortNumber port);
}
