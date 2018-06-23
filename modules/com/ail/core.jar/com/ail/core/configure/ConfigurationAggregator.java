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
package com.ail.core.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.ail.core.XMLString;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileWriter;
import de.schlichtherle.truezip.fs.archive.zip.JarDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

/**
 * <b>Name</b>
 * <p>
 * &nbsp;&nbsp;com.ail.core.configure.ConfigurationAggregator - Utility to
 * aggregate annotation generated configurations
 * </p>
 * <b>Synopsis</b>
 * <p>
 * &nbsp;&nbsp;com.ail.core.configure.ConfigurationAggregator -o &lt;output
 * file&gt; -s &lt;search path&gt;
 * </p>
 * <b>Description</b>
 * <p>
 * The annotations processor generates configuration files in response to
 * various annotations that appear in the source code. These configuration files
 * are considered to be build time artifacts and are not references at run time.
 * The ConfigurationAggregator scans the file system for each of the search
 * paths defined on the command line, finding all of the annotation generated
 * configuration files and aggregating them into the single output file
 * specified with the -o option.
 * </p>
 * The search path may be made up of either directories or jar files. When a jar
 * file is encountered it will be searched internally for configuration files.
 * </p>
 * <p>
 * The output file may be either path to a file on the file system, or a path
 * pointing into a compressed jar file. When it is a jar file, the output will
 * be written into the jar.
 * </p>
 * <b>Examples</b>
 * <p>
 * The following would scan the files under the three 'directories', aggregate
 * all the annotation generated configuration files that it found and output the
 * results to outputfile.xml. The jar file included in the list will be searched
 * internally.
 * </p>
 * <code>$java com.ail.core.configure.ConfigurationAgregator \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-o ./outputfile.xml \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-s ./directoryA;./directoryB;./JarFile \</code>
 * <p>
 * In the following example, the output would be written directly into the jar
 * file into it's /path/to/file folder.
 * </p>
 * <code>$java com.ail.core.configure.ConfigurationAgregator \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-o ./myJar.jar!/path/to/file/outputfile.xml \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-s ./directoryA;./directoryB;./JarFile \</code>
 * <br/>
 * <br/>
 *
 * @version 1.0
 */
public class ConfigurationAggregator {

    public ConfigurationAggregator() {
        // Configure TrueZIP to recognise jar files.
        TConfig.get().setArchiveDetector(new TArchiveDetector("jar", new JarDriver(IOPoolLocator.SINGLETON)));
    }

    void run(String args[]) throws Exception {
        String annotationTypeConfig = null;
        String searchPath = null;

        for (int i = 0; i < args.length; i++) {
            if ("-o".equals(args[i])) {
                annotationTypeConfig = args[++i];
            } else if ("-s".equals(args[i])) {
                searchPath = FileUtils.readFileToString(new File(args[++i]));
            }
        }

        mergeAnnotationGeneratedConfigs(searchPath, annotationTypeConfig);
    }

    /**
     * Tree walk the specified folder list looking the configuration files
     * fragments which the annotations processor generates. The processor itself
     * is defined in the development project,
     * {@link com.ail.annotation.Processor}.
     *
     */
    public void mergeAnnotationGeneratedConfigs(String searchPath, String annotationTypeOutputFile) throws Exception {
        Collection<XMLString> configs = findAnnotationTypesFiles(searchPath);

        PrintWriter writer = openWriter(annotationTypeOutputFile);

        createAggregatedConfig(configs, writer);

        writer.close();
    }

    /**
     * Search the searchPath, tree walking each element, and find all of the
     * AnnotatedTypes.xml files. The elements in the search path can be both
     * exploded and unexploded jar files. They will be searched irrespective of
     * their type and the files found added to the returned as instances of
     * either File or TFile.
     *
     * @param searchPath
     *            List of paths to search in a File.pathSeparated list.
     * @return Collection of Files & TFiles
     * @throws IOException
     * @throws FileNotFoundException
     */
    Collection<XMLString> findAnnotationTypesFiles(String searchPath) throws FileNotFoundException, IOException {
        Collection<XMLString> configList = new ArrayList<>();

        if (searchPath == null) {
            return configList;
        }

        Queue<File> folderList = new LinkedList<>();

        for (String rootFolderName : searchPath.split(File.pathSeparator)) {
            File rootFolder = new File(rootFolderName);

            if (rootFolder.isDirectory()) {
                folderList.add(rootFolder);
            } else {
                folderList.add(new TFile(rootFolderName));
            }

            while (!folderList.isEmpty()) {
                File element = folderList.remove();

                if (element.isDirectory()) {
                    folderList.addAll(Arrays.asList(element.listFiles()));
                } else {
                    if (element.getName().matches("AnnotatedTypes.xml")) {
                        InputStream in = (element instanceof TFile) ? new TFileInputStream(element) : new FileInputStream(element);
                        configList.add(new XMLString(in));
                        in.close();
                    }
                }
            }
        }

        return configList;
    }

    /**
     * Open an output writer for the annotations to be written to. This can be
     * either a file, of a file inside jar file.
     *
     * @param annotationTypeOutputFile
     * @return PrintWriter to write aggregated configurations to
     * @throws FileNotFoundException
     */
    PrintWriter openWriter(String annotationTypeOutputFile) throws FileNotFoundException {
        if (annotationTypeOutputFile.indexOf('!') > -1) {
            String pathToJar = annotationTypeOutputFile.substring(0, annotationTypeOutputFile.indexOf('!'));
            String tidyPath = annotationTypeOutputFile.replace('!', '/');

            if (new File(pathToJar).isFile()) {
                Writer writer = new TFileWriter(new TFile(tidyPath));
                return new PrintWriter(writer);
            } else {
                return new PrintWriter(tidyPath);
            }
        } else {
            return new PrintWriter(annotationTypeOutputFile);
        }
    }

    void createAggregatedConfig(Collection<XMLString> configs, PrintWriter writer) throws FileNotFoundException, IOException, XPathExpressionException, SAXException, TransformerException {
        writer.printf("<!-- This is a generated file. The types defined here are automatically create -->\n");
        writer.printf("<!-- by the build system in response to annotations in source code. Edits to   -->\n");
        writer.printf("<!-- this file will be lost.                                                   -->\n");
        writer.printf("<configuration xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='http://www.appliedindustriallogic.com/schemas/Configuration.xsd'>\n");

        writer.printf("<builders>\n");
        for (XMLString config : configs) {
            writer.print(config.eval("configuration/builders/*"));
        }
        writer.printf("\n</builders>\n");

        writer.printf("<group name='JXPathExtensions'>\n");
        for (XMLString config : configs) {
            writer.print(config.eval("configuration/group[@name='JXPathExtensions']/*"));
        }
        writer.printf("\n</group>\n");

        writer.printf("<group name='NamespacesToResetOnResetAll'>\n");
        for (XMLString config : configs) {
            writer.print(config.eval("configuration/group[@name='NamespacesToResetOnResetAll']/*"));
        }
        writer.printf("\n</group>\n");

        writer.printf("<types>\n");
        for (XMLString config : configs) {
            writer.print(config.eval("configuration/types/*"));
        }
        writer.printf("\n</types>\n");

        writer.printf("</configuration>\n");
    }

    public static void main(String args[]) throws Exception {
        new ConfigurationAggregator().run(args);
    }
}
