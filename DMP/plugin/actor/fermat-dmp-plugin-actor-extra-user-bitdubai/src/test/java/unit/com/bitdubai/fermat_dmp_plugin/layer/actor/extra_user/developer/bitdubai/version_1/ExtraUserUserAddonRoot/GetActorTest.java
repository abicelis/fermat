package unit.com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.ExtraUserUserAddonRoot;

import com.bitdubai.fermat_api.layer.dmp_actor.Actor;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFactory;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.ExtraUserUserAddonRoot;
import com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.structure.ExtraUserDatabaseConstants;
import com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.structure.ExtraUserDatabaseFactory;
import java.util.UUID;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Created by Manuel Perez on 26/07/15.
 */
public class GetActorTest {

    ExtraUserUserAddonRoot extraUserUserAddonRoot=new ExtraUserUserAddonRoot();
    //@Mock
    //ExtraUserUserAddonRoot extraUserUserAddonRoot=new ExtraUserUserAddonRoot();
    Database mockDatabase= Mockito.mock(Database.class);
    @Mock
    DatabaseFactory mockDatabaseFactory;
    DatabaseTable mockDatabaseTable=Mockito.mock(DatabaseTable.class);
    DatabaseTableRecord mockDatabaseTableRecord=Mockito.mock(DatabaseTableRecord.class);
    MockErrorManager mockErrorManager;
    @Mock
    ExtraUserDatabaseFactory mockExtraUserDatabaseFactory;
    @Mock
    PluginDatabaseSystem mockPluginDatabaseSystem;
    UUID pluginId;
    String testUserName;



    @Test
    public void getActorTest_CannotLoadDatabaseTableToMemory_getsNullActor() throws Exception{

        Actor actor;
        extraUserUserAddonRoot.start();
        doThrow(new CantLoadTableToMemoryException()).when(mockDatabaseTable).loadToMemory();
        actor=extraUserUserAddonRoot.getActor(pluginId);
        Assertions.assertThat(actor).isNull();

    }

    @Test
    public void getActorTest_extraUserUserAddonRootNotStarted_throwsGenericException() throws Exception{

        Actor actor=extraUserUserAddonRoot.getActor(null);
        Assertions.assertThat(mockErrorManager).isNotNull();
        Assertions.assertThat(actor).isNull();

    }

    @Test
    public void getActorTest_setValidUUID_getsValidActor() throws Exception{

        Actor actor;
        extraUserUserAddonRoot.start();
        actor=extraUserUserAddonRoot.getActor(pluginId);
        Assertions.assertThat(actor).isNotNull();

    }

    @Before
    public void setUp() throws Exception{

        testUserName="Test User Name";
        pluginId= UUID.randomUUID();

        MockitoAnnotations.initMocks(this);

        mockExtraUserDatabaseFactory.setPluginDatabaseSystem(mockPluginDatabaseSystem);
        when(mockDatabase.getDatabaseFactory()).thenReturn(mockDatabaseFactory);
        when(mockDatabaseTable.getEmptyRecord()).thenReturn(mockDatabaseTableRecord);
        when(mockDatabase.getTable(ExtraUserDatabaseConstants.EXTRA_USER_TABLE_NAME)).thenReturn(mockDatabaseTable);
        when(mockPluginDatabaseSystem.openDatabase(pluginId, "ExtraUser")).thenReturn(mockDatabase);

        //when(mockPluginDatabaseSystem.createDatabase(pluginId, "ExtraUser")).thenReturn(mockDatabase);
        when(mockExtraUserDatabaseFactory.createDatabase(pluginId)).thenReturn(mockDatabase);
        mockErrorManager=new MockErrorManager();
        extraUserUserAddonRoot.setErrorManager(mockErrorManager);
        extraUserUserAddonRoot.setId(pluginId);
        extraUserUserAddonRoot.setPluginDatabaseSystem(mockPluginDatabaseSystem);


    }

}