package com.example.biblioteca;

import org.junit.jupiter.api.Test;

class DebugTest {
    @Test
    void printUserHome() {
        System.out.println("user.home = " + System.getProperty("user.home"));
    }
}