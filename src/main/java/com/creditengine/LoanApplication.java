package com.creditengine;

import java.time.LocalDate;

public record LoanApplication(
        String clientId,
        int monthlyIncome,
        int monthlyExpenses,
        int requestedAmount,
        int durationInMonths,
        boolean isCivilServant,
        LocalDate birthDate
) {
}
