package com.pp.Project2;

import org.springframework.data.repository.Repository;

public interface WalletRepository extends Repository <WalletEntity,Integer>{
    void save(WalletEntity wallet);
    WalletEntity findById(int id);
}
