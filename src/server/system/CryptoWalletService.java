package server.system;

import server.system.user.CryptoWallet;

public class CryptoWalletService {

    public void depositMoneyInWallet(double amount, CryptoWallet cryptoWallet) {
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }
        cryptoWallet.depositMoney(amount);
    }
}
