package com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.AsymmetricCryptography;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.interfaces.KeyPair;
import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.all_definition.wallet.Stock;
import com.bitdubai.fermat_cbp_api.all_definition.wallet.StockBalance;
import com.bitdubai.fermat_cbp_api.all_definition.wallet.StockTransaction;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddCreditCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddDebitCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetAvailableBalanceCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetBookedBalanceCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCollectionCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantPerformTransactionException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransactionRecord;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWallet;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletSetting;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.database.CryptoBrokerWalletDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantCreateCryptoBrokerWalletImplException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantCreateNewCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantGetCryptoBrokerWalletImplException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantGetCryptoBrokerWalletException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.ErrorManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jorge on 26-10-2015.
 * Modified by Yordin Alayn 27.10.15
 * Modified by Franklin Marcano 30.11.2015
 */
public class CryptoBrokerWalletImpl implements CryptoBrokerWallet{
    public static final String PATH_DIRECTORY = "cryptobrokerwallet-swap/";
    private static final String CRYPTO_BROKER_WALLET_FILE_NAME = "walletsIds";
    private Database database;
    private Map<String, UUID> walletAssetIssuer = new HashMap<>();
    private UUID pluginId;
    private ErrorManager errorManager;
    private PluginDatabaseSystem pluginDatabaseSystem;
    private PluginFileSystem pluginFileSystem;

    public CryptoBrokerWalletImpl(ErrorManager errorManager, PluginDatabaseSystem pluginDatabaseSystem, PluginFileSystem pluginFileSystem, UUID pluginId) {
        this.errorManager = errorManager;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
    }

    @Override
    public StockBalance getStockBalance() throws CantGetStockCryptoBrokerWalletException {
        return new StockBalanceImpl(database,pluginId, pluginFileSystem);
    }

    @Override
    public CryptoBrokerWalletSetting getCryptoWalletSetting() throws CantGetCryptoBrokerWalletSettingException {
        return null;
    }

//public class CryptoBrokerWalletImpl implements CryptoBrokerWallet {

//    private final KeyPair walletKeyPair;
//    private final String ownerPublicKey;
//    private final ConcurrentHashMap<CurrencyType, Stock> stockMap;
//    private final CryptoBrokerWalletDatabaseDao databaseDao;
//
//    public CryptoBrokerWalletImpl(final KeyPair walletKeyPair, final String ownerPublicKey, final CryptoBrokerWalletDatabaseDao databaseDao){
//        this.walletKeyPair = walletKeyPair;
//        this.ownerPublicKey = ownerPublicKey;
//        this.databaseDao = databaseDao;
//        stockMap = new ConcurrentHashMap<>();
//    }
//
//    @Override
//    public String getWalletPublicKey() { return this.walletKeyPair.getPublicKey(); }
//
//    @Override
//    public String getOwnerPublicKey() { return this.ownerPublicKey; }
//
//    @Override
//    public void addStock(CurrencyType currencyType, FermatEnum merchandise) throws CantAddStockCryptoBrokerWalletException{
//        try {
//            StockTransaction stockRecordBook = stockRecord(currencyType, merchandise, BalanceType.BOOK);
//            StockTransaction stockRecordAvailable = stockRecord(currencyType, merchandise, BalanceType.AVAILABLE);
//            CryptoBrokerStock stock = new CryptoBrokerStock(currencyType, this.walletKeyPair,this.databaseDao);
//            stock.addDebit(stockRecordBook);
//            stock.addDebit(stockRecordAvailable);
//        } catch (CantAddDebitCryptoBrokerWalletException e) {
//            throw new CantAddStockCryptoBrokerWalletException("CRYPTO BROKER WALLET", e, "CAN'T ADD STOCK CRYPTO BROKER WALLET", "");
//        } catch (Exception e) {
//            throw new CantAddStockCryptoBrokerWalletException("CRYPTO BROKER WALLET", e, "CAN'T ADD STOCK CRYPTO BROKER WALLET", "");
//        }
//    }
//
//    @Override
//    public Stock getStock(CurrencyType currencyType) throws CantGetStockCryptoBrokerWalletException {
//        try{
//            CryptoBrokerStock stock = new CryptoBrokerStock(currencyType, this.walletKeyPair,this.databaseDao);
//            stock.getBookedBalance();
//            stock.getAvailableBalance();
//            return stock;
//        } catch (CantGetBookedBalanceCryptoBrokerWalletException e) {
//            throw new CantGetStockCryptoBrokerWalletException("CRYPTO BROKER WALLET", e, "CAN'T GET STOCK CRYPTO BROKER WALLET", "");
//        } catch (CantGetAvailableBalanceCryptoBrokerWalletException e) {
//            throw new CantGetStockCryptoBrokerWalletException("CRYPTO BROKER WALLET", e, "CAN'T GET STOCK CRYPTO BROKER WALLET", "");
//        } catch (Exception e) {
//            throw new CantGetStockCryptoBrokerWalletException("CRYPTO BROKER WALLET", e, "CAN'T GET STOCK CRYPTO BROKER WALLET", "");
//        }
//    }
//
//    @Override
//    public Collection<Stock> getStocks() throws CantGetStockCollectionCryptoBrokerWalletException {
//        try {
//            HashSet<Stock> stocks = new HashSet<>();
//            for (final Map.Entry<CurrencyType, Stock> StockReference : stockMap.entrySet())
//                stocks.add(StockReference.getValue());
//            return stocks;
//        } catch (Exception e) {
//            throw new CantGetStockCollectionCryptoBrokerWalletException("CRYPTO BROKER WALLET", e, "CAN'T GET STOCK CRYPTO BROKER WALLET", "");
//        }
//    }
//
//    @Override
//    public void performTransaction(StockTransaction transaction) throws CantPerformTransactionException {
//        try{
//            TransactionType transactionType = transaction.getTransactionType();
//            StockTransaction stockTransaction = stockTransactionRecord(transaction);
//            CryptoBrokerStock stock = new CryptoBrokerStock(transaction.getCurrencyType(), this.walletKeyPair,this.databaseDao);
//            if(transactionType == TransactionType.CREDIT){
//                stock.addCredit(stockTransaction);
//            }else{
//                stock.addDebit(stockTransaction);
//            }
//        } catch (CantAddCreditCryptoBrokerWalletException e) {
//            throw new CantPerformTransactionException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW TRANSACTION CRYPTO BROKER WALLET", "");
//        } catch (CantAddDebitCryptoBrokerWalletException e) {
//            throw new CantPerformTransactionException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW TRANSACTION CRYPTO BROKER WALLET", "");
//        } catch (Exception e) {
//            throw new CantPerformTransactionException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW TRANSACTION CRYPTO BROKER WALLET", "");
//        }
//    }
//
//    public void createWallet() throws CantCreateCryptoBrokerWalletImplException{
//        try{
//            databaseDao.createCryptoBrokerWallet(this.walletKeyPair,this.ownerPublicKey);
//        } catch (CantCreateNewCryptoBrokerWalletException e) {
//            throw new CantCreateCryptoBrokerWalletImplException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW CRYPTO BROKER WALLET", "");
//        } catch (Exception e) {
//            throw new CantCreateCryptoBrokerWalletImplException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW CRYPTO BROKER WALLET", "");
//        }
//    }
//
//    public void getWallet() throws CantGetCryptoBrokerWalletImplException {
//        try{
//            databaseDao.getCryptoBrokerWallet(this.ownerPublicKey);
//        } catch (CantGetCryptoBrokerWalletException e) {
//            throw new CantGetCryptoBrokerWalletImplException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW CRYPTO BROKER WALLET", "");
//        } catch (Exception e) {
//            throw new CantGetCryptoBrokerWalletImplException("CRYPTO BROKER WALLET", e, "CAN'T CREATE NEW CRYPTO BROKER WALLET", "");
//        }
//    }
//
//    private StockTransaction stockTransactionRecord(StockTransaction transaction){
//        KeyPair walletKeyPair = AsymmetricCryptography.createKeyPair(transaction.getWalletPublicKey());
//        StockTransaction record = new CryptoBrokerStockTransactionRecordImpl(
//                transaction.getTransactionId(),
////                transaction.getContractId(),
//                walletKeyPair,
//                transaction.getOwnerPublicKey(),
//                transaction.getBalanceType(),
//                transaction.getTransactionType(),
//                transaction.getCurrencyType(),
//                transaction.getMerchandise(),
//                transaction.getAmount(),
//                0,
//                0,
//                transaction.getTimestamp(),
//                transaction.getMemo()
//        );
//        return record;
//    }
//
//    private StockTransaction stockRecord(CurrencyType currencyType, FermatEnum merchandise, BalanceType balanceType){
//        UUID transactionId = UUID.randomUUID();
//        long timestamp = 0;
//        String memo = "";
//        StockTransaction record = new CryptoBrokerStockTransactionRecordImpl(
//                transactionId,
////                null,
//                this.walletKeyPair,
//                this.ownerPublicKey,
//                balanceType,
//                TransactionType.DEBIT,
//                currencyType,
//                merchandise,
//                0,
//                0,
//                0,
//                timestamp,
//                memo
//        );
//        return record;
//    }

}
