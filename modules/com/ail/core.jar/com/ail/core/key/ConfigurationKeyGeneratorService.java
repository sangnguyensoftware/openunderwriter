/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.ail.core.key;

import java.util.HashMap;
import java.util.Map;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreContext;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.Configuration;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyArgument;

/**
 * @deprecated Replaced by {@link TableKeyGeneratorService} in OU3.1
 */
@Deprecated
@ServiceImplementation
public class ConfigurationKeyGeneratorService extends Service<GenerateUniqueKeyArgument> {
    private static Map<String,UniqueNumberHandler> uniqueNumberHandlers=new HashMap<String,UniqueNumberHandler>();
    private String configurationNamespace;

    /**
     * Return the product name from the arguments as the configuration namespace.
     * The has the effect of selecting the product's configuration.
     * @return product name
     */
    @Override
    public String getConfigurationNamespace() {
        return configurationNamespace;
    }

    /**
     * The version effective date for this service is always "now".
     */
    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return new VersionEffectiveDate();
    }

    /** The 'business logic' of the entry point. */
    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        if (args.getKeyIdArg() == null || args.getKeyIdArg().length() == 0) {
            throw new PreconditionException("args.getKeyIdArg() == null || args.getKeyIdArg().length() == 0");
        }

        String keyIdArg = args.getKeyIdArg();
        String productTypeId = CoreContext.getProductName();

        if (productTypeId == null || productTypeId.length() == 0) {
            throw new PreconditionException("CoreContext.getProductName() == null || CoreContext.getProductName().length() == 0");
        }

        // Set the namespace to the product's so we search up the hierarchy from there
        configurationNamespace = Functions.productNameToConfigurationNamespace(productTypeId);

        // Get the actual namespace which defines the key, this will start searching up
        // the config hierarchy form the current namespace (set above).
        configurationNamespace = getCore().getParameter(keyIdArg + "NextNumber").getNamespace();

        String uniqueNumberHanderId = configurationNamespace + keyIdArg;

        synchronized (uniqueNumberHandlers) {
            if (!uniqueNumberHandlers.containsKey(uniqueNumberHanderId)) {
                uniqueNumberHandlers.put(uniqueNumberHanderId, new UniqueNumberHandler());
            }
        }

        UniqueNumberHandler uniqueNumberHandler = uniqueNumberHandlers.get(uniqueNumberHanderId);

        synchronized (uniqueNumberHandler) {
            if (uniqueNumberHandler.isBlockEmpty()) {
                String nextNumberParamId = keyIdArg + "NextNumber";
                String blockSizeParamId = keyIdArg + "BlockSize";

                Configuration config = getConfiguration();
                int nextNumber = Integer.parseInt(config.findParameter(nextNumberParamId).getValue());
                int blockSize = Integer.parseInt(config.findParameter(blockSizeParamId).getValue());

                uniqueNumberHandler.setNextNumber(nextNumber);
                uniqueNumberHandler.setBlockEnd(nextNumber + blockSize + 1);

                config.findParameter(nextNumberParamId).setValue(Integer.toString(nextNumber + blockSize + 1));

                setConfiguration(config);
            }

            args.setKeyRet(uniqueNumberHandler.getNextNumber());
        }

        if (args.getKeyRet() == null) {
            throw new PostconditionException("args.getKeyRet()==null");
        }
    }
}

/**
 * Private class used to track the usage of unique numbers.
 */
class UniqueNumberHandler {
    private long blockEnd=0;
    private long nextNumber=0;

    public boolean isBlockEmpty() {
        return blockEnd==nextNumber;
    }

    public long getNextNumber() {
        return nextNumber++;
    }

    public void setNextNumber(long next) {
        nextNumber=next;
    }

    public void setBlockEnd(long blockEnd) {
        this.blockEnd=blockEnd;
    }
}