/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

package com.ail.core.configure.server;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationHandler;

@ServiceImplementation
public class DeployCarService extends Service<DeployCarService.DeployCarArgument> {
    
    @ServiceArgument
    public interface DeployCarArgument extends Argument {
        /**
         * Getter for the namespacesArg property. The configuration namespaces to be imported from the supplied par
         * @return Value of namespacesArg, or null if it is unset
         */
        Collection<String> getNamespacesArg();

        /**
         * Setter for the namespacesArg property. * @see #getNamespacesArg
         * @param namespacesArg new value for property.
         */
        void setNamespacesArg(Collection<String> namespacesArg);

        /**
         * Getter for the carArg property. The par to import configurations from
         * @return Value of carArg, or null if it is unset
         */
        byte[] getCarArg();

        /**
         * Setter for the carArg property. * @see #getCarArg
         * @param carArg new value for property.
         */
        void setCarArg(byte[] carArg);
    }

    @ServiceCommand(defaultServiceClass=DeployCarService.class)
    public interface DeployCarCommand extends Command, DeployCarArgument {}
    
    /** The 'business logic' of the entry point. */
    @Override
    public void invoke() throws PreconditionException {
        if (args.getNamespacesArg()==null) {
            throw new PreconditionException("args.getNamespacesArg()==null");
        }

        if (args.getCarArg()==null) {
            throw new PreconditionException("args.getCarArg()==null");
        }

        if (args.getNamespacesArg().size()==0) {
            // Odd to call deploy and not specifiy anything to deploy. Odd, but not wrong.
            return;
        }

        try {
            ZipInputStream zis=new ZipInputStream(new ByteArrayInputStream(args.getCarArg()));
            Configuration config=null;
            byte[] block=new byte[1024];
            StringBuffer buffer=null;
            int bytesRead=0;

            // Loop through the entries in the zip. Each entry is a configuration, each entry's name is
            // the configurations namespace.
            for(ZipEntry ze=zis.getNextEntry() ; ze!=null ; ze=zis.getNextEntry() ) {
                // Does the list of namespaces we've been asked to deploy include this one?
                if (args.getNamespacesArg().contains(ze.getName())) {

                    core.logInfo("Reading '"+ze.getName()+"' from car file");
                    
                    // Create a buffer to hold the contents of the entry - the configuration as a string of XML
                    buffer=new StringBuffer();
                    
                    // Read all the bytes for this entry from the zip into the buffer
                    for(bytesRead=zis.read(block, 0, block.length) ; bytesRead!=-1 ; bytesRead=zis.read(block, 0, block.length)) {
                        buffer.append(new String(block, 0, bytesRead));
                    }
                    
                    // Marshal the String of XML into a configuration object.
                    config=core.fromXML(Configuration.class, new XMLString(buffer.toString()));
                    
                    // This allows (or forces) the existing config to be overwritten. The configuration loaders
                    // check this date in the config we're saving against the one in the database, and if they're
                    // not the same, throw an update collision exception. Setting this to null prevents the check.
                    config.setValidFrom(null);
                    
                    // Deploy the config.
                    ConfigurationHandler.getInstance().saveConfiguration(ze.getName(), config, core);
                }
                
                zis.closeEntry();
            }

            zis.close();
        }
        catch(Exception e) {
            throw new CarProcessingError("Failed to deploy car", e);
        }
    }
}


