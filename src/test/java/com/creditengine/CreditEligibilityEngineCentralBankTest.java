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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditEligibilityEngineCentralBankTest {

    @Mock
    private CentralBankPort centralBankPort;

    @Test
    void rejette_toujours_un_client_interdit_bancaire_meme_fonctionnaire_avec_excellent_taux() {
        when(centralBankPort.isBanned("client-banni")).thenReturn(true);
        Clock fixedClock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        CreditEligibilityEngine engine = new CreditEligibilityEngine(centralBankPort, fixedClock);

        LoanApplication application = new LoanApplication("client-banni", 500_000, 50_000, 12_000, 12, true, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }
}
