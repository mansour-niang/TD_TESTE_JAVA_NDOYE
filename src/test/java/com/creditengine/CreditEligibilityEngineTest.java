package com.creditengine;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditEligibilityEngineTest {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
    private final CreditEligibilityEngine engine = new CreditEligibilityEngine(clientId -> false, fixedClock);

    @Test
    void rejette_quand_le_reste_a_vivre_est_strictement_inferieur_a_100000() {
        LoanApplication application = new LoanApplication("client-1", 200_000, 150_001, 500_000, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }

    @Test
    void approuve_quand_le_reste_a_vivre_est_exactement_100000() {
        // revenus=140000, charges=40000 => reste=100000, taux=(40000+583)/140000*100=28.99% (<33%, non bloquant)
        LoanApplication application = new LoanApplication("client-2", 140_000, 40_000, 7_000, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }

    @Test
    void approuve_quand_le_reste_a_vivre_est_superieur_a_100000() {
        // revenus=400000, charges=100000 => reste=300000, taux=(100000+1000)/400000*100=25.25% (<33%, non bloquant)
        LoanApplication application = new LoanApplication("client-3", 400_000, 100_000, 12_000, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }

    @Test
    void rejette_quand_le_taux_d_endettement_depasse_33_pourcent() {
        // revenus=300000, charges=90000, mensualite=120000/12=10000 => taux=(90000+10000)/300000*100=33.33%
        LoanApplication application = new LoanApplication("client-4", 300_000, 90_000, 120_000, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }

    @Test
    void approuve_quand_le_taux_d_endettement_est_exactement_33_pourcent() {
        // revenus=300000, charges=89000, mensualite=120000/12=10000 => taux=(89000+10000)/300000*100=33.0%
        LoanApplication application = new LoanApplication("client-5", 300_000, 89_000, 120_000, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }

    @Test
    void approuve_un_fonctionnaire_a_38_pourcent_d_endettement_alors_qu_un_client_standard_serait_rejete() {
        // revenus=300000, charges=104000, mensualite=120000/12=10000 => taux=(104000+10000)/300000*100=38.0%
        LoanApplication application = new LoanApplication("client-6", 300_000, 104_000, 120_000, 12, true, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.APPROUVE, decision);
    }
}
