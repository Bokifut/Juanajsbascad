package org.example.juegofinalsupremo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {

    @Test
    void testLauncherInstantiation() {
        Launcher launcher = new Launcher();
        assertNotNull(launcher, "Launcher instance should be creatable");
    }
}