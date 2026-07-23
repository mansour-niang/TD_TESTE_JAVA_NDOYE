package com.creditengine;

public class CreditEligibilityEngine {

    private static final int RESTE_A_VIVRE_MINIMUM = 100_000;
    private static final double TAUX_ENDETTEMENT_MAXIMUM = 33.0;

    public EligibilityDecision evaluate(LoanApplication application) {
        int resteAVivre = application.monthlyIncome() - application.monthlyExpenses();

        if (resteAVivre < RESTE_A_VIVRE_MINIMUM) {
            return EligibilityDecision.REJETE;
        }

        int mensualite = application.requestedAmount() / application.durationInMonths();
        double tauxEndettement = ((application.monthlyExpenses() + mensualite) * 100.0) / application.monthlyIncome();

        if (tauxEndettement > TAUX_ENDETTEMENT_MAXIMUM) {
            return EligibilityDecision.REJETE;
        }

        return EligibilityDecision.APPROUVE;
    }
}
