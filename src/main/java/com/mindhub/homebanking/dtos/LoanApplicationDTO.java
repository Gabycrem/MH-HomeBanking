package com.mindhub.homebanking.dtos;

import java.math.BigDecimal;

public class LoanApplicationDTO {

    private Long loanId;
    private BigDecimal amount;
    private Integer payments;
    private String toAccountNumber;

    public LoanApplicationDTO() {
    }

    public Long getLoanId() {
        return loanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
