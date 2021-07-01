package com.pp.Project2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Wallet {

    @Autowired
    WalletManager walletManager;


    @RequestMapping("/inputfile")
    public String inputFile() throws IOException {
        FileReader file = new FileReader("/var/inputfile.txt");
        List<String> lines = Files.readAllLines(Paths.get("/var/inputfile.txt"));
        int i=-1;
        ArrayList<Integer> customerList =  new ArrayList<>();
        Integer initialWalletBalance = -1;
        for (String line : lines){
            if(line.contentEquals("****")) {
                i = i + 1;
                continue;
            }

            if(i == 0){
                // add cabs


            }
            else if(i == 1){
                // add customer
                WalletEntity w = new WalletEntity(1,2);
                customerList.add(Integer.parseInt(line));
            }
            else{
                // add wallet
                initialWalletBalance = Integer.parseInt(line);
            }

        }

        for(Integer custId : customerList ){
            WalletEntity w = new WalletEntity(custId, initialWalletBalance);
            walletManager.walletRepository.save(w);
        }

        return "Done Reading";


    }

    @RequestMapping("/getBalance")
    public int getBalance(@RequestParam("custId") int custId)
    {
        //get the balance
        WalletEntity wallet = walletManager.getWalletById(custId);
        return wallet.getBalance();
    }

    @RequestMapping("/deductAmount")
    public boolean deductAmount(@RequestParam("custId") int custId,@RequestParam("amount") int amount)
    {
        //requested by RideService.requestRide
        //get the balance
        WalletEntity wallet = walletManager.getWalletById(custId);

        //if balance is at least same as amount
        int balance = wallet.getBalance();
        if (balance >= amount)
        {
            wallet.setBalance(balance-amount); //deduct the balance
            walletManager.walletRepository.save(wallet);
            return true;
        }

        return false;
    }

    @RequestMapping("/addAmount")
    public boolean addAmount(@RequestParam("custId") int custId,@RequestParam("amount") int amount)
    {
        //may be requested by RideService.rideCancelled
        //get the balance
        WalletEntity wallet = walletManager.getWalletById(custId);

        int balance = wallet.getBalance();
        wallet.setBalance(balance+amount); //increase the balance
        walletManager.walletRepository.save(wallet);
        return true;
    }

    @RequestMapping("/reset")
    public void reset() throws IOException {
        //set the balance to initial balance for all the customers
        FileReader file = new FileReader("/var/inputfile.txt");
        List<String> lines = Files.readAllLines(Paths.get("/var/inputfile.txt"));
        //FileReader file = new FileReader("/home/tarun/inputfile");
        //List<String> lines = Files.readAllLines(Paths.get("/home/tarun/inputfile"));
        ArrayList<Integer> customerList =  new ArrayList<>();
        Integer initialWalletBalance = -1;
        int i=-1;
        for (String line : lines){
            if(line.contentEquals("****")) {
                i = i + 1;
                continue;
            }

            if(i == 0){
                // add cabs


            }
            else if(i == 1){
                // add customer
                WalletEntity w = new WalletEntity(1,2);
                customerList.add(Integer.parseInt(line));
            }
            else{
                // add wallet
                initialWalletBalance = Integer.parseInt(line);
            }

        }
        i = 1;
        while (true)
        {
            WalletEntity wallet = walletManager.getWalletById(i++);
            if (wallet == null)
                break;
            wallet.setBalance(initialWalletBalance);
        for(Integer custId : customerList ){
            wallet = walletManager.getWalletById(custId);
            wallet.setBalance(initialWalletBalance);
            walletManager.walletRepository.save(wallet);
        }



    }
}
}
