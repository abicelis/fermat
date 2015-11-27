package com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.incoming_intra_user.developer.bitdubai.version_1.structure.executors;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantProcessRequestAcceptedException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.bitcoin_wallet.interfaces.BitcoinWalletTransactionRecord;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.bitcoin_wallet.interfaces.BitcoinWalletWallet;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.exceptions.CantGetCryptoPaymentRegistryException;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.exceptions.CantUpdateRequestPaymentStateException;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.interfaces.CryptoPaymentManager;
import com.bitdubai.fermat_cry_api.layer.crypto_module.crypto_address_book.interfaces.CryptoAddressBookManager;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.incoming_intra_user.developer.bitdubai.version_1.exceptions.IncomingIntraUSerUnexpectedTransactionException;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.incoming_intra_user.developer.bitdubai.version_1.exceptions.IncomingIntraUserCantExecuteTransactionException;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.incoming_intra_user.developer.bitdubai.version_1.exceptions.IncomingIntraUserCantGenerateTransactionException;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.incoming_intra_user.developer.bitdubai.version_1.interfaces.TransactionExecutor;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.incoming_intra_user.developer.bitdubai.version_1.util.TransactionCompleteInformation;

import java.util.UUID;

/**
 * Created by eze on 2015.09.15..
 */
public class IncomingIntraUserBitcoinBasicWalletTransactionExecutor implements TransactionExecutor {

    private BitcoinWalletWallet      bitcoinWallet;
    private CryptoAddressBookManager cryptoAddressBookManager;

    public IncomingIntraUserBitcoinBasicWalletTransactionExecutor(final BitcoinWalletWallet bitcoinWallet, final CryptoAddressBookManager cryptoAddressBookManager){
        this.bitcoinWallet            = bitcoinWallet;
        this.cryptoAddressBookManager = cryptoAddressBookManager;

    }

    @Override
    public void executeTransaction(TransactionCompleteInformation transactionContainer) throws IncomingIntraUserCantExecuteTransactionException {
        Transaction<CryptoTransaction> transaction = transactionContainer.getCryptoTransaction();
        try {
            switch (transaction.getInformation().getCryptoStatus()) {
                case ON_CRYPTO_NETWORK:
                    processOnCryptoNetworkTransaction(transactionContainer);
                    break;
                case ON_BLOCKCHAIN:
                    processOnBlockChainTransaction(transactionContainer);
                    break;
                //todo ezequiel, cambie de Reversed a REVERSED_ON_CRYPTO_NETWORK
                case REVERSED_ON_CRYPTO_NETWORK:
                    processReversedOnCryptoNetworkTransaction(transactionContainer);
                    break;
                case REVERSED_ON_BLOCKCHAIN:
                    processReversedOnBlockchainTransaction(transactionContainer);
                    break;
                default:
                    throw new IncomingIntraUSerUnexpectedTransactionException("El crypto status no es esperado", null, "El cryptoStatus es: " + transaction.getInformation().getCryptoStatus().getCode(), "");
            }
        } catch (CantRegisterDebitException | CantRegisterCreditException  | IncomingIntraUSerUnexpectedTransactionException e) {
            throw new IncomingIntraUserCantExecuteTransactionException("An exception happened",e,"","");
        } catch (Exception e) {
            throw new IncomingIntraUserCantExecuteTransactionException("An Unexpected Exception Happened", FermatException.wrapException(e),"","");
        }
    }

    private void processOnCryptoNetworkTransaction(TransactionCompleteInformation transaction) throws CantRegisterCreditException {
        try {
            BitcoinWalletTransactionRecord record = transaction.generateBitcoinTransaction(cryptoAddressBookManager);
            bitcoinWallet.getBalance(BalanceType.BOOK).credit(record);


        } catch (IncomingIntraUserCantGenerateTransactionException e) {
            throw new CantRegisterCreditException("I couldn't generate the transaction",e,"","");
        }
    }

    private void processOnBlockChainTransaction(TransactionCompleteInformation transaction) throws CantRegisterCreditException{
        try {
            BitcoinWalletTransactionRecord record = transaction.generateBitcoinTransaction(cryptoAddressBookManager);
            bitcoinWallet.getBalance(BalanceType.AVAILABLE).credit(record);



        } catch (IncomingIntraUserCantGenerateTransactionException e) {
            throw new CantRegisterCreditException("I couldn't generate the transaction",e,"","");
        }

        //Esto se hiso aca hasta pensar mejor el proceso para cambiar el estado del request
        try
        {
        if(transaction.getTransactionMetadata().getInformation().getRequestId() != null)
        {
            processPaymentRequest(transaction.getTransactionMetadata().getInformation().getRequestId());
        }

        } catch (CantProcessRequestAcceptedException e) {
            throw new CantRegisterCreditException("I couldn't update request payment",e,"","");
        }
    }

    private void processReversedOnCryptoNetworkTransaction(TransactionCompleteInformation transaction) throws CantRegisterDebitException {
        try {
            BitcoinWalletTransactionRecord record = transaction.generateBitcoinTransaction(cryptoAddressBookManager);
            bitcoinWallet.getBalance(BalanceType.BOOK).debit(record);
        } catch (IncomingIntraUserCantGenerateTransactionException e) {
            throw new CantRegisterDebitException("I couldn't generate the transaction",e,"","");
        }
    }

    private void processReversedOnBlockchainTransaction(TransactionCompleteInformation transaction) throws CantRegisterDebitException {
        try {
            BitcoinWalletTransactionRecord record = transaction.generateBitcoinTransaction(cryptoAddressBookManager);
            bitcoinWallet.getBalance(BalanceType.AVAILABLE).debit(record);


        } catch (IncomingIntraUserCantGenerateTransactionException e) {
            throw new CantRegisterDebitException("I couldn't generate the transaction",e,"","");
        }
    }

    private void processPaymentRequest(UUID requestId) throws CantProcessRequestAcceptedException {
       /* try
        {
            //Hay que disparar un evento para que escuche el Crypto Payment
           // cryptoPaymentManager.getCryptoPaymentRegistry().acceptIncomingRequest(requestId);
        }
        catch(CantGetCryptoPaymentRegistryException e)
        {
            throw new CantProcessRequestAcceptedException("I couldn't update request payment accepted",e,"","");
        }
        catch(CantUpdateRequestPaymentStateException e)
        {
            throw new CantProcessRequestAcceptedException("I couldn't update request payment accepted",e,"","");
        }
        catch(Exception e)
        {
            throw new CantProcessRequestAcceptedException("I couldn't update request payment accepted",FermatException.wrapException(e),"","unknown error");
        }*/
    }
}
