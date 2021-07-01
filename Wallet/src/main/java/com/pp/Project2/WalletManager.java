package com.pp.Project2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletManager {

    @Autowired
    WalletRepository walletRepository;

    public WalletManager (WalletRepository walletRepository)
    {
        this.walletRepository = walletRepository;
    }

    public WalletEntity getWalletById(int custId)
    {
        return walletRepository.findById(custId);
    }

    public void store(WalletEntity wallet)
    {
        walletRepository.save(wallet);
    }

}
