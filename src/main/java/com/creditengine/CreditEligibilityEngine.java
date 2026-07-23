package com.creditengine;

public class CreditEligibilityEngine {

    private static final int RESTE_A_VIVRE_MINIMUM = 100_000;

    public EligibilityDecision evaluate(LoanApplication application) {
        int resteAVivre = application.monthlyIncome() - application.monthlyExpenses();

        if (resteAVivre < RESTE_A_VIVRE_MINIMUM) {
            return EligibilityDecision.REJETE;
        }

        return EligibilityDecision.APPROUVE;
    }
}
