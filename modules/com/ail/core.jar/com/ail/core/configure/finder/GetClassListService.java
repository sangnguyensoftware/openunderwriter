/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.core.configure.finder;

import static com.ail.core.Functions.classForName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

/**
 * This service finds the classes that implement or extend a given base class. The <code>SearchClass</code>
 * argument specifies the name of the class to search for implementors of, and the service searches its own
 * classpath (i.e. the classpath of the instance of the service) for implementors which are returned as
 * a collection of class names.<p>
 *
 * By default the search covers only the <code>com.ail</code> package tree. However, the <code>SearchPackage</code>
 * argument may be used to specify an alternate package tree to search.<p>
 * An empty list is returned if the class being searched for does not exist.
 *
 * @pre args.getSearchClassArg() != null
 * @post args.getFoundImplementorsRet() !=null
 * @post args.getFoundImplementorsRet() returns a Collection of String instances representing the names of the classes found.
 *
 * @todo This service searches the system classpath for classes. This means that it will not find system classes (java.*, etc.) or dynamically created classes.
 */
@ServiceImplementation
public class GetClassListService extends Service<GetClassListService.GetClassListArgument> {
    private Vector<String> allFiles = new Vector<String>();
    private String FILESEPARATOR = System.getProperty("file.separator");
	private String PATHSEPARATOR = System.getProperty("path.separator");

	@ServiceArgument
	public interface GetClassListArgument extends Argument {

	    /**
	     * Set the value of the search class argument.
	     * @see #getSearchClassArg
	     * @param searchClassName New value for class argument.
	     */
	    void setSearchClassArg(String searchClassName);

	    /**
	     * Fetch the value of the class argument. 
	     * @see #setSearchClassArg
	     * @return value of searchClass
	     */
	    String getSearchClassArg();

	     /**
	     * Set value of search package argument. Defaults to com.ail if not set.
	     * @see #getSearchPackageArg
	     * @param searchPackage New value for searchPackage argument.
	     */
	    public void setSearchPackageArg(String searchPackage);

	    /**
	     * Get value of search package argument.
	     * @see #setSearchPackageArg
	     * @return value of searchPackage
	     */
	    public String getSearchPackageArg();

	    /**
	     * Set value of implementors found argument.
	     * @see #getFoundImplementorsRet
	     * @param foundImplementors New value for foundImplementors argument.
	     */
	    public void setFoundImplementorsRet(Collection<String> foundImplementors);

	    /**
	     * Get value of implementors found argument.
	     * @see #setFoundImplementorsRet
	     * @return value of foundImplementors
	     */
	    public Collection<String> getFoundImplementorsRet();
	}

	@ServiceCommand(defaultServiceClass=GetClassListService.class)
	public interface GetClassListCommand extends Command, GetClassListArgument {}
	
	/** Default constructor */
    public GetClassListService() {
        super();
    }

    /**
     * TODO This really needs a good refactoring - it build up vectors of files, vectors of directories, and vectors of
     * classes. There's no need for any of them, the processing can be done on the fly without any lists - which would 
     * reduce the memory req. a lot.
     * @throws  
     */
    public void invoke() throws PreconditionException, PostconditionException {
        Class<?> classToCheck = null;

        if (args.getSearchClassArg() == null) {
            throw new PreconditionException("No Class to search for.");
        }

        args.setFoundImplementorsRet(new ArrayList<String>());
        ArrayList<String> jars = new ArrayList<String>();
        ArrayList<String> dirs = new ArrayList<String>();
        String cp = System.getProperty("java.class.path");

        // Get the Class we're searching for
        try {
            classToCheck = classForName(args.getSearchClassArg());
        }
        catch(ClassNotFoundException e) {
            return;
        }

        // Parse the CLASSPATH into two lists: one of jars, the other of directories
        while (cp.indexOf(PATHSEPARATOR) != -1) {
            String seg = cp.substring(0, cp.indexOf(PATHSEPARATOR));
            if (seg.substring(seg.lastIndexOf(".") + 1).equals("jar")) {
            	jars.add(seg);
            } else {
                dirs.add(seg);
            }
            cp = cp.substring(cp.indexOf(PATHSEPARATOR) + 1);
        }

        // Search for implementors in the jars
        while (jars.size() > 0) {
            String name=(String)jars.remove(0);
            JarFile jar = null;

            try {
            	jar = new JarFile(name);
                for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
                    String entryName = entries.nextElement().getName();
                	allFiles.add(entryName);
                }
            } catch (IOException ioe) {
                // if isn't a jar file, but is it an exploded jar?
                File file=new File(name);
                if (file.exists() && file.isDirectory()) {
                    dirs.add(name);
                }
                // ignore this - probably just means the classpath is pointing at something that doesn't exist.
            }
            finally {
                try {
                    if (jar!=null) {
                        jar.close();
                    }
                } catch (IOException e) {
                    throw new PostconditionException("Failed to close jar file.");
                }
            }
        }

        // Search for implementors in the directories
        while (dirs.size() > 0) {
            File dir = new File((String)dirs.remove(0));
        	getFiles(dir.getAbsolutePath().length() + 1, dir);
        }

        // proccess files found
        while (!allFiles.isEmpty()) {

        	String validClassName = validClass((String)allFiles.remove(0));

            // ignore nulls, and any names that are already in the list
            if (validClassName != null && !args.getFoundImplementorsRet().contains(validClassName))  {
            	try {
               		Class<?> validClass = Class.forName(validClassName);
					if(classToCheck.isAssignableFrom(validClass)){
						args.getFoundImplementorsRet().add(validClassName);
					}
            	} catch (ClassNotFoundException cnfe) {
                	//allow through
            	} catch (NoClassDefFoundError ncdfe) {
                	//allow through
            	}
				catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
    }

  /**
   * Takes a raw file name and assess whether it is a valid class.
   * Filters against package name provided, defaulting to com.ail if
   * none give.
   * @param fileName Raw file name
   * @return Class name or null if invalid
   */
  private String validClass(String fileName) {
    StringBuffer name=new StringBuffer(fileName);

    int lastDot=name.lastIndexOf(".");

    // if the name doesn't end '.class' or '.CLASS' or similar, then bail out.
    if (lastDot == -1 || !name.substring(lastDot).equalsIgnoreCase(".class")) {
        return null;
    }

    for(int i=name.length()-1 ; i>=0 ; i--) {
        if (name.charAt(i)==FILESEPARATOR.charAt(0) || name.charAt(i) == '\\' || name.charAt(i) == '/') {
            name.setCharAt(i, '.');
        }
    }

    if (args.getSearchPackageArg() == null) {
      	if (name.length() < 7 || name.indexOf("com.ail.")!=0) {
            return null;
          }
    }
    else {
        if (name.indexOf(args.getSearchPackageArg())!=0) {
         	return null;
        }
    }

    return name.substring(0, lastDot);
  }

  /**
   * Cycles through a directory and subdirectories returning files contained therein.
   * @param offset Offsets path name to leave only file name.
   * @param dir Directory to find files in.
   */
  private void getFiles(int offset, File dir) {
	 File[] files = dir.listFiles();

     if (files != null) {
		 for (int i = 0; i < files.length; i ++) {
		 	if (files[i].isDirectory()){
		    	getFiles(offset, files[i]);
		 	} else {
		    	allFiles.add(files[i].getAbsolutePath().substring(offset));
		 	}
		 }
     }
  }
}


