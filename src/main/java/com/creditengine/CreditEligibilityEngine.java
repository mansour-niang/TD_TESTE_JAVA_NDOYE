package com.creditengine;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

public class CreditEligibilityEngine {

    private static final int RESTE_A_VIVRE_MINIMUM = 100_000;
    private static final double TAUX_ENDETTEMENT_MAXIMUM_STANDARD = 33.0;
    private static final double TAUX_ENDETTEMENT_MAXIMUM_FONCTIONNAIRE = 40.0;
    private static final int AGE_MAXIMUM_FIN_DE_PRET = 65;
    private static final int MONTANT_SEUIL_FRAUDE = 10_000_000;

    private final CentralBankPort centralBankPort;
    private final Clock clock;
    private final FraudAlertService fraudAlertService;

    public CreditEligibilityEngine(CentralBankPort centralBankPort, Clock clock, FraudAlertService fraudAlertService) {
        this.centralBankPort = centralBankPort;
        this.clock = clock;
        this.fraudAlertService = fraudAlertService;
    }

    public EligibilityDecision evaluate(LoanApplication application) {
        if (centralBankPort.isBanned(application.clientId())) {
            if (application.requestedAmount() > MONTANT_SEUIL_FRAUDE) {
                fraudAlertService.flagSuspiciousClient(application.clientId());
            }
            return EligibilityDecision.REJETE;
        }

        if (resteAVivre(application) < RESTE_A_VIVRE_MINIMUM) {
            return EligibilityDecision.REJETE;
        }

        if (tauxEndettement(application) > plafondTauxEndettement(application)) {
            return EligibilityDecision.REJETE;
        }

        if (ageALaFinDuPret(application) > AGE_MAXIMUM_FIN_DE_PRET) {
            return EligibilityDecision.REJETE;
        }

        return EligibilityDecision.APPROUVE;
    }

    private int resteAVivre(LoanApplication application) {
        return application.monthlyIncome() - application.monthlyExpenses();
    }

    private int mensualiteEstimee(LoanApplication application) {
        return application.requestedAmount() / application.durationInMonths();
    }

    private double tauxEndettement(LoanApplication application) {
        int chargesTotales = application.monthlyExpenses() + mensualiteEstimee(application);
        return (chargesTotales * 100.0) / application.monthlyIncome();
    }

    private double plafondTauxEndettement(LoanApplication application) {
        return application.isCivilServant()
                ? TAUX_ENDETTEMENT_MAXIMUM_FONCTIONNAIRE
                : TAUX_ENDETTEMENT_MAXIMUM_STANDARD;
    }

    private int ageALaFinDuPret(LoanApplication application) {
        LocalDate finDuPret = LocalDate.now(clock).plusMonths(application.durationInMonths());
        return Period.between(application.birthDate(), finDuPret).getYears();
    }
}
