package com.ewalker.anomaly.detector.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminKeywordRuleTest {
    private final AdminKeywordRule rule = new AdminKeywordRule();

    @Test
    void detectsAdminKeywords() {
        assertTrue(rule.evaluate("admin").triggered());
        assertTrue(rule.evaluate("root password").triggered());
        assertTrue(rule.evaluate("ADMINISTRATOR").triggered());
    }

    @Test
    void rejectNormalInput() {
        assertFalse(rule.evaluate("User login successful").triggered());
        assertFalse(rule.evaluate("server health check").triggered());
    }
}
