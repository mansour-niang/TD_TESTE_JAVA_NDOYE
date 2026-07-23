package com.creditengine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditEligibilityEngineCentralBankTest {

    @Mock
    private CentralBankPort centralBankPort;

    @Test
    void rejette_toujours_un_client_interdit_bancaire_meme_fonctionnaire_avec_excellent_taux() {
        when(centralBankPort.isBanned("client-banni")).thenReturn(true);
        CreditEligibilityEngine engine = new CreditEligibilityEngine(centralBankPort);

        LoanApplication application = new LoanApplication("client-banni", 500_000, 50_000, 12_000, 12, true);

        EligibilityDecision decision = engine.evaluate(application);

        assertEquals(EligibilityDecision.REJETE, decision);
    }
}
