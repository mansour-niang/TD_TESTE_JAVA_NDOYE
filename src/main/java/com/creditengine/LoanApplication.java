package com.creditengine;

public record LoanApplication(
        String clientId,
        int monthlyIncome,
        int monthlyExpenses,
        int requestedAmount,
        int durationInMonths
) {
}
