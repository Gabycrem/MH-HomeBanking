package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LoanDTO {

    private Long id;
    private String name;
    private BigDecimal maxAmount;
    private List<Integer> payments;

    public LoanDTO() {
    }

    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = new ArrayList<>(loan.getPayments());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }
}
