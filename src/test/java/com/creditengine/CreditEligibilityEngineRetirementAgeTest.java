package com.creditengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CreditEligibilityEngineRetirementAgeTest {

    @Mock
    private CentralBankPort centralBankPort;

    // "Aujourd'hui" fige au 1er janvier 2030 pour des tests reproductibles.
    private final Clock fixedClock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void rejette_un_client_de_60_ans_empruntant_sur_72_mois_car_il_aurait_66_ans_a_la_fin_du_pret() {
        CreditEligibilityEngine engine = new CreditEligibilityEngine(centralBankPort, fixedClock);

        LoanApplication application = new LoanApplication(
                "client-senior", 500_000, 50_000, 60_000, 72, false, LocalDate.of(1970, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }
}
