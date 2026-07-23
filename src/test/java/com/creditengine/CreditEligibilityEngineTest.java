package com.creditengine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditEligibilityEngineTest {

    private final CreditEligibilityEngine engine = new CreditEligibilityEngine();

    @Test
    void rejette_quand_le_reste_a_vivre_est_strictement_inferieur_a_100000() {
        LoanApplication application = new LoanApplication("client-1", 200_000, 150_001, 500_000, 12);

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }

    @Test
    void approuve_quand_le_reste_a_vivre_est_exactement_100000() {
        LoanApplication application = new LoanApplication("client-2", 200_000, 100_000, 500_000, 12);

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }

    @Test
    void approuve_quand_le_reste_a_vivre_est_superieur_a_100000() {
        LoanApplication application = new LoanApplication("client-3", 300_000, 100_000, 500_000, 12);

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }

    @Test
    void rejette_quand_le_taux_d_endettement_depasse_33_pourcent() {
        // revenus=300000, charges=90000, mensualite=120000/12=10000 => taux=(90000+10000)/300000*100=33.33%
        LoanApplication application = new LoanApplication("client-4", 300_000, 90_000, 120_000, 12);

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }

    @Test
    void approuve_quand_le_taux_d_endettement_est_exactement_33_pourcent() {
        // revenus=300000, charges=89000, mensualite=120000/12=10000 => taux=(89000+10000)/300000*100=33.0%
        LoanApplication application = new LoanApplication("client-5", 300_000, 89_000, 120_000, 12);

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }
}
