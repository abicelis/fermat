package com.bitdubai.fermat_cbp_core.layer.negotiation.customer_broker_purchase;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPluginSubsystem;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantStartSubsystemException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation.customer_broker_purchase.developer.bitdubai.DeveloperBitDubai;

/**
 * Created by Angel on 28/11/15.
 */
public class CustomerBrokerPurchasePluginSubsystem extends AbstractPluginSubsystem {

    public CustomerBrokerPurchasePluginSubsystem() {
        super(new PluginReference(Plugins.NEGOTIATION_PURCHASE));
    }

    @Override
    public void start() throws CantStartSubsystemException {
        try {
            registerDeveloper(new DeveloperBitDubai());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            throw new CantStartSubsystemException(e, null, null);
        }
    }
}
