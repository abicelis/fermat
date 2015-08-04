package unit.com.bitdubai.fermat_cry_plugin.layer.crypto_module.wallet_address_book.developer.bitdubai.version_1.structure.WalletAddressBookCryptoModuleDao;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.ReferenceWallet;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_cry_plugin.layer.crypto_module.wallet_address_book.developer.bitdubai.version_1.structure.WalletAddressBookCryptoModuleDatabaseConstants;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_cry_api.layer.crypto_module.wallet_address_book.exceptions.CantGetWalletAddressBookException;
import com.bitdubai.fermat_cry_api.layer.crypto_module.wallet_address_book.exceptions.WalletAddressBookNotFoundException;
import com.bitdubai.fermat_cry_plugin.layer.crypto_module.wallet_address_book.developer.bitdubai.version_1.structure.WalletAddressBookCryptoModuleDao;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class GetAllWalletAddressBookByWalletIdTest extends TestCase {

    @Mock
    ErrorManager errorManager;

    @Mock
    PluginDatabaseSystem pluginDatabaseSystem;

    @Mock
    Database database;

    @Mock
    DatabaseTable databaseTable;

    @Mock
    DatabaseTableRecord databaseTableRecord;

    WalletAddressBookCryptoModuleDao dao;

    UUID walletId;

    UUID pluginId;

    @Before
    public void setUp() throws Exception {
        walletId = UUID.randomUUID();
        pluginId = UUID.randomUUID();
        dao = new WalletAddressBookCryptoModuleDao(errorManager, pluginDatabaseSystem, pluginId);
        when(pluginDatabaseSystem.openDatabase(pluginId, pluginId.toString())).thenReturn(database);

        dao.initialize();
    }

    @Test
    public void testGetAllWalletAddressBookByWalletId_NotNull() throws Exception {
        when(databaseTableRecord.getStringValue(WalletAddressBookCryptoModuleDatabaseConstants.CRYPTO_WALLET_ADDRESS_BOOK_TABLE_WALLET_TYPE)).thenReturn(ReferenceWallet.BASIC_WALLET_BITCOIN_WALLET.getCode());
        when(databaseTableRecord.getStringValue(WalletAddressBookCryptoModuleDatabaseConstants.CRYPTO_WALLET_ADDRESS_BOOK_TABLE_CRYPTO_CURRENCY)).thenReturn(CryptoCurrency.BITCOIN.getCode());
        List<DatabaseTableRecord> databaseTableRecordList = new ArrayList<>();
        databaseTableRecordList.add(databaseTableRecord);
        when(databaseTable.getRecords()).thenReturn(databaseTableRecordList);
        when(database.getTable(anyString())).thenReturn(databaseTable);

        assertNotNull(dao.getAllWalletAddressBookModuleByWalletId(walletId));
    }

    @Test(expected=CantGetWalletAddressBookException.class)
    public void testGetAllWalletAddressBookByWalletId_walletIdNull_CantGetWalletAddressBookException() throws Exception {
        dao.getAllWalletAddressBookModuleByWalletId(null);
    }

    @Test(expected=WalletAddressBookNotFoundException.class)
    public void testGetAllWalletAddressBookByWalletId_WalletAddressBookNotFoundException() throws Exception {
        when(database.getTable(anyString())).thenReturn(databaseTable);

        dao.getAllWalletAddressBookModuleByWalletId(walletId);
    }

    @Test(expected=CantGetWalletAddressBookException.class)
    public void testGetAllWalletAddressBookByWalletId_CantLoadTableToMemoryException_WalletAddressBookNotFoundException() throws Exception {
        doThrow(new CantLoadTableToMemoryException()).when(databaseTable).loadToMemory();
        when(database.getTable(anyString())).thenReturn(databaseTable);

        dao.getAllWalletAddressBookModuleByWalletId(walletId);
    }
}
