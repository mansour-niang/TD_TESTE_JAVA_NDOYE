package com.creditengine;

public class CreditEligibilityEngine {

    private static final int RESTE_A_VIVRE_MINIMUM = 100_000;
    private static final double TAUX_ENDETTEMENT_MAXIMUM_STANDARD = 33.0;
    private static final double TAUX_ENDETTEMENT_MAXIMUM_FONCTIONNAIRE = 40.0;

    public EligibilityDecision evaluate(LoanApplication application) {
        int resteAVivre = application.monthlyIncome() - application.monthlyExpenses();

        if (resteAVivre < RESTE_A_VIVRE_MINIMUM) {
            return EligibilityDecision.REJETE;
        }

        int mensualite = application.requestedAmount() / application.durationInMonths();
        double tauxEndettement = ((application.monthlyExpenses() + mensualite) * 100.0) / application.monthlyIncome();

        double plafondTauxEndettement = application.isCivilServant()
                ? TAUX_ENDETTEMENT_MAXIMUM_FONCTIONNAIRE
                : TAUX_ENDETTEMENT_MAXIMUM_STANDARD;

        if (tauxEndettement > plafondTauxEndettement) {
            return EligibilityDecision.REJETE;
        }

        return EligibilityDecision.APPROUVE;
    }
}
