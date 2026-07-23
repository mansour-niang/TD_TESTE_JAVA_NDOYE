package com.creditengine;

public class CreditEligibilityEngine {

    private static final int RESTE_A_VIVRE_MINIMUM = 100_000;
    private static final double TAUX_ENDETTEMENT_MAXIMUM_STANDARD = 33.0;
    private static final double TAUX_ENDETTEMENT_MAXIMUM_FONCTIONNAIRE = 40.0;

    public EligibilityDecision evaluate(LoanApplication application) {
        if (resteAVivre(application) < RESTE_A_VIVRE_MINIMUM) {
            return EligibilityDecision.REJETE;
        }

        if (tauxEndettement(application) > plafondTauxEndettement(application)) {
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
}
