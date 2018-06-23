/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.XMLString;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.UnknownNamespaceError;

@ServiceImplementation
public class PackageCarService extends com.ail.core.Service<PackageCarService.PackageCarArgument> {
    
    @ServiceArgument
    public interface PackageCarArgument extends Argument {
        /**
         * Getter for the namespacesArg property. A collection of strings providing the names (namespaces) of the
         * configurations to be packaged
         * @return Value of namespacesArg, or null if it is unset
         */
        Collection<String> getNamespacesArg();

        /**
         * Setter for the namespacesArg property. * @see #getNamespacesArg
         * @param namespacesArg new value for property.
         */
        void setNamespacesArg(Collection<String> namespacesArg);

        /**
         * Getter for the carRet property. The packaged car as a byte array.
         * @return Value of carRet, or null if it is unset
         */
        byte[] getCarRet();

        /**
         * Setter for the parRet property.
         * @see #getCarRet
         * @param carRet new value for property.
         */
        void setCarRet(byte[] carRet);
    }

    @ServiceCommand(defaultServiceClass=PackageCarService.class)
    public interface PackageCarCommand extends Command, PackageCarArgument {}
    
    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        String namespace=null;

        if (args.getNamespacesArg()==null) {
            throw new PreconditionException("args.getNamespacesArg()==null");
        }

        if (args.getNamespacesArg().size()==0) {
            throw new PreconditionException("args.getNamespacesArg().size()==0");
        }

        try {
            XMLString config=null;
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ZipOutputStream zos=new ZipOutputStream(baos);
            
            for(Iterator<String> it=args.getNamespacesArg().iterator() ; it.hasNext() ; ) {
                namespace=it.next();
                config=core.toXML(ConfigurationHandler.getInstance().loadConfiguration(namespace, core, core));
                zos.putNextEntry(new ZipEntry(namespace));
                zos.write(config.toString().getBytes(), 0, config.toString().getBytes().length);
                zos.closeEntry();
            }
    
            zos.close();
            args.setCarRet(baos.toByteArray());
            baos.close();
        }
        catch(IOException ex) {
            throw new CarProcessingError("Failed to build Car file: ", ex);
        }
        catch(UnknownNamespaceError e) {
            throw new PreconditionException("Configuration is not defined:"+namespace);
        }
        
        if (args.getCarRet()==null) {
            throw new PostconditionException("args.getCarRet()==null");
        }
    }
}
