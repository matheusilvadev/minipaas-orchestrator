package com.paas.infrastructure.deploy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paas.domain.valueobject.PortNumber;

public class DefaultPortAllocatorTest {

    @Test
    @DisplayName("Deve alocar e liberar uma porta com sucesso")
    void shouldAllocateAndReleasePort() {
        DefaultPortAllocator allocator = new DefaultPortAllocator(8081, 8081);

        // Aloca a única porta disponível
        PortNumber port = allocator.allocate();
        assertEquals(8081, port.value());

        // Tenta alocar novamente (deve falhar)
        assertThrows(RuntimeException.class, allocator::allocate);

        // Libera a porta
        allocator.release(port);

        // Deve conseguir alocar novamente agora que foi liberada
        PortNumber reallocatedPort = allocator.allocate();
        assertEquals(8081, reallocatedPort.value());
    }
}
