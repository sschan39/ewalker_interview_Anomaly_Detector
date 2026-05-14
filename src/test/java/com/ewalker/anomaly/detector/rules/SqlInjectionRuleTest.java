package com.ewalker.anomaly.detector.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlInjectionRuleTest {
    private final SqlInjectionRule rule = new SqlInjectionRule();

    @Test
    void detectsSqlInjectionPatterns() {
        assertTrue(rule.evaluate("1 = 1").triggered());
        assertTrue(rule.evaluate("OR 1=1").triggered());
        assertTrue(rule.evaluate("'; DROP TABLE users; --").triggered());
    }

    @Test
    void rejectNormalInput() {
        assertFalse(rule.evaluate("SELECT * FROM users").triggered());
        assertFalse(rule.evaluate("User login successful").triggered());
    }
}
