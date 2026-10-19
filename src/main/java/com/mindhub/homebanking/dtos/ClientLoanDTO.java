package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;
import java.math.BigDecimal;

public class ClientLoanDTO{

    private Long id;
    private Long loanId;
    private String name;
    private BigDecimal amount;
    private Integer payments;

    public ClientLoanDTO(ClientLoan clientLoan){
        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
    }


    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Long getLoanId(){
        return loanId;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public Integer getPayments(){
        return payments;
    }
}
