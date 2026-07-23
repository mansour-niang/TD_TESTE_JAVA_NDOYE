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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditEligibilityEngineFraudAlertTest {

    @Mock
    private CentralBankPort centralBankPort;

    @Mock
    private FraudAlertService fraudAlertService;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void signale_le_client_interdit_bancaire_qui_demande_plus_de_10_millions() {
        when(centralBankPort.isBanned("client-fraude")).thenReturn(true);
        CreditEligibilityEngine engine = new CreditEligibilityEngine(centralBankPort, fixedClock, fraudAlertService);

        LoanApplication application = new LoanApplication(
                "client-fraude", 500_000, 50_000, 10_000_001, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
        verify(fraudAlertService, times(1)).flagSuspiciousClient("client-fraude");
    }

    @Test
    void ne_signale_pas_le_client_interdit_bancaire_qui_demande_10_millions_ou_moins() {
        when(centralBankPort.isBanned("client-banni-normal")).thenReturn(true);
        CreditEligibilityEngine engine = new CreditEligibilityEngine(centralBankPort, fixedClock, fraudAlertService);

        LoanApplication application = new LoanApplication(
                "client-banni-normal", 500_000, 50_000, 10_000_000, 12, false, LocalDate.of(1990, 1, 1));

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
        verify(fraudAlertService, never()).flagSuspiciousClient("client-banni-normal");
    }
}
