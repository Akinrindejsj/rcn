package com.example.rcn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton bank details shown on the Donate page.
 */
@Entity
@Table(name = "payment_details")
public class PaymentDetails {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "sort_code")
    private String sortCode;

    protected PaymentDetails() {
    }

    public static PaymentDetails defaults() {
        PaymentDetails p = defaultsTransient();
        p.id = SINGLETON_ID;
        return p;
    }

    public static PaymentDetails defaultsTransient() {
        PaymentDetails p = new PaymentDetails();
        p.bankName = "First Bank of Nigeria";
        p.accountName = "Revolutionary Communists of Nigeria";
        p.accountNumber = "1234567890";
        p.sortCode = "011";
        return p;
    }

    // ---- Getters & setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
