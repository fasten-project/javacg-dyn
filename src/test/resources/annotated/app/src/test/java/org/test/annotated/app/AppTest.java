package org.test.annotated.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void main() {
        App.main(new String[]{});
        assertTrue(true);
    }
}