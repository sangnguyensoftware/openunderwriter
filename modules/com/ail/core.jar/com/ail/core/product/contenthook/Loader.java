/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
package com.ail.core.product.contenthook;

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.CoreContext.setCoreProxy;
import static com.ail.core.configure.ConfigurationHandler.resetCache;
import static com.ail.core.product.contenthook.FolderManager.PRODUCT_ROOT;

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.configure.AbstractConfigurationLoader;
import com.ail.core.product.ResetAllProductsService.ResetAllProductsCommand;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;

public class Loader extends SimpleAction {
    private static final Logger LOGGER = Logger.getLogger(Loader.class);
    private static final Pattern EXCLUSION_PATTERN = Pattern.compile("(^.*/Build.xml$)");

    private static final Long GLOBAL_REPOSITORY_ID = 10197L;
    private static final Long CONTENT_CREATOR = 13135L;

    private FolderManager folderManager;
    private FileManager fileManager;

    public Loader(Long repositoryId, Long creatorId) {
        folderManager = new FolderManager(repositoryId, creatorId);
        fileManager = new FileManager(folderManager, repositoryId, creatorId);
        setCoreProxy(new CoreProxy());
    }

    public Loader() {
        this(GLOBAL_REPOSITORY_ID, CONTENT_CREATOR);
    }

    @Override
    public void run(String[] ids) throws ActionException {
        if (configurationsHaveNotBeenReset()) {
            getCoreProxy().resetConfigurations();
            getCoreProxy().setVersionEffectiveDateToNow();
        }

        if (loadContent()) {
            resetProducts();
        }

        resetCache();
    }

    private boolean loadContent() throws ActionException {
        boolean changeMade = false;

        try {
            getCoreProxy().getParameter("ProductRepository.Mode").setValue("Manual");

            JarInputStream jis = (JarInputStream) Thread.currentThread().getContextClassLoader().getResourceAsStream(PRODUCT_ROOT);

            if (!folderManager.isExistingFolder(PRODUCT_ROOT)) {
                folderManager.createFolder(PRODUCT_ROOT);
                changeMade = true;
            }

            for (JarEntry je = jis.getNextJarEntry(); je != null; je = jis.getNextJarEntry()) {

                String path = PRODUCT_ROOT + je.getName();

                if (EXCLUSION_PATTERN.matcher(path).matches()) {
                    continue;
                }

                if (je.isDirectory()) {
                    if (!folderManager.isExistingFolder(path)) {
                        folderManager.createFolder(path);
                        changeMade = true;
                    }
                } else {
                    if (!fileManager.isExistingFile(path)) {
                        fileManager.createFile(path);
                        LOGGER.log(Level.INFO, "Loaded content: " + path);
                        changeMade = true;
                    }
                }
            }
        } catch (Throwable t) {
            throw new ActionException(t);
        }
        finally {
            getCoreProxy().getParameter("ProductRepository.Mode").setValue("Automatic");
        }

        return changeMade;
    }

    private void resetProducts() throws ActionException {
        try {
            ResetAllProductsCommand resetAllProductsCommand = getCoreProxy().newCommand(ResetAllProductsCommand.class);
            resetAllProductsCommand.invoke();
        } catch (BaseException e) {
            throw new ActionException(e);
        }
    }

    private boolean configurationsHaveNotBeenReset() {
        return !AbstractConfigurationLoader.loadLoader().isConfigurationRepositoryCreated();
    }
}
