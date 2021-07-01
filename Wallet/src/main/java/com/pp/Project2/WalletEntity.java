package com.pp.Project2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class WalletEntity {

    @Id
    int custId;

    @Column
    int balance;

    public WalletEntity()
    {}

    public WalletEntity(int id, int balance)
    {
        this.custId = id;
        this.balance = balance;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
