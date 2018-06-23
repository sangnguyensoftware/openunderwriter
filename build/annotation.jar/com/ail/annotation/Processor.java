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
package com.ail.annotation;

import static javax.lang.model.SourceVersion.RELEASE_8;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes({ "com.ail.annotation.ServiceArgument",
							"com.ail.annotation.ServiceCommand",
							"com.ail.annotation.ServiceImplementation",
							"com.ail.annotation.TypeDefinition",
							"com.ail.annotation.Configurable",
							"com.ail.annotation.Builder",
							"com.ail.annotation.XPathFunctionDefinition"})
public class Processor extends AbstractProcessor {

	public static final String JAR_LOCAL_ANNOTATED_TYPES_FILENAME = "AnnotatedTypes.xml";
    private Filer filer;
	private Messager messager;

	@Override
	public void init(ProcessingEnvironment env) {
		filer = env.getFiler();
		messager = env.getMessager();
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
		try {
			localProcess(elements, env);
		} catch (IOException e) {
			error("Annotation processor failure: " + e);
		}

		return true;
	}

	private static String COMMANDS="ServiceCommand";
	private static String ARGUMENTS="ServiceArgument";
	private static String SERVICES="ServiceImplementation";
	private static String TYPES="TypeDefinition";
	private static String BUILDERS="Builder";
	private static String XPATHS="XPathFunctionDefinition";
	private static String CONFIGS="Configurable";

	Map<String,Map<String,Element>> annots=new HashMap<>();

	private void localProcess(Set<? extends TypeElement> elements, RoundEnvironment env) throws IOException {

		annots.put(COMMANDS, new HashMap<String,Element>());
		annots.put(ARGUMENTS, new HashMap<String,Element>());
		annots.put(SERVICES, new HashMap<String,Element>());
		annots.put(TYPES, new HashMap<String,Element>());
		annots.put(BUILDERS, new HashMap<String,Element>());
		annots.put(XPATHS, new HashMap<String,Element>());
		annots.put(CONFIGS, new HashMap<String,Element>());

		for (TypeElement te : elements) {
			for (Element e : env.getElementsAnnotatedWith(te)) {
				annots.get(te.getSimpleName().toString()).put(e.toString(), e);
			}
		}

		if (annots.get(ARGUMENTS).size() != 0) {
			generateArgumentImpls();
		}

		if (annots.get(COMMANDS).size() != 0) {
			generateCommandImpls();
		}

		if (annots.get(ARGUMENTS).size()!=0
		||  annots.get(COMMANDS).size()!=0
		||  annots.get(TYPES).size()!=0
		||  annots.get(BUILDERS).size()!=0
		||  annots.get(XPATHS).size()!=0
		||  annots.get(CONFIGS).size()!=0) {
			generateCoreDefaultConfigTypes();
		}

		annots.get(ARGUMENTS).clear();
		annots.get(COMMANDS).clear();
		annots.get(TYPES).clear();
		annots.get(XPATHS).clear();
		annots.get(CONFIGS).clear();
		annots.get(BUILDERS).clear();
		annots.get(SERVICES).clear();
	}

	private void generateCoreDefaultConfigTypes() throws IOException {
		OutputStream os = filer.createResource(StandardLocation.CLASS_OUTPUT, "com.ail.core", JAR_LOCAL_ANNOTATED_TYPES_FILENAME, (Element[]) null).openOutputStream();
		PrintWriter pw = new PrintWriter(os);

		pw.printf("<!-- This is a generated file. The TYPES defined here are automatically create -->\n");
		pw.printf("<!-- by the annotation processor. Edits to this file will be lost.             -->\n");
		pw.printf("<configuration xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='http://www.appliedindustriallogic.com/schemas/Configuration.xsd'>\n");
		pw.printf("\n");
		pw.printf("  <group name='NamespacesToResetOnResetAll'>\n");
		for(TypeElement te: ElementFilter.typesIn(annots.get(CONFIGS).values())) {
			pw.printf("     <parameter name='%s'/>\n", te);
		}
		pw.printf("  </group>\n");
		pw.printf("\n");
		pw.printf("  <group name='JXPathExtensions'>\n");
		for(TypeElement te: ElementFilter.typesIn(annots.get(XPATHS).values())) {
			pw.printf("     <parameter name='%s'>%s</parameter>\n", te.getAnnotation(XPathFunctionDefinition.class).namespace(), te);
		}
		pw.printf("  </group>\n");
		pw.printf("\n");
		pw.printf("  <builders>\n");
		for(TypeElement te: ElementFilter.typesIn(annots.get(BUILDERS).values())) {
			pw.printf("     <builder name='%s' factory='%s'/>\n", te.getAnnotation(Builder.class).name(), te);
		}
		pw.printf("  </builders>\n");
		pw.printf("\n");
		pw.printf("  <types>\n");
		for (TypeElement te : ElementFilter.typesIn(annots.get(SERVICES).values())) {
			pw.printf("<service name='%s' builder='ClassBuilder' key='com.ail.core.command.ClassAccessor'>\n", te);
			pw.printf("  <parameter name='ServiceClass'>%s</parameter>\n", te);
			pw.printf("</service>\n");
		}
		pw.printf("\n");
		for (TypeElement te : ElementFilter.typesIn(annots.get(COMMANDS).values())) {
			ServiceCommand sc = te.getAnnotation(ServiceCommand.class);
			TypeMirror value=null;
			try {
				sc.defaultServiceClass();
			}
			catch(MirroredTypeException e) {
				value=e.getTypeMirror();
			}

			if (sc != null && value!=null) {
		        String impl = te.getQualifiedName().toString().replaceFirst("[A-Z]{1}.*\\.(.*)Command$","$1CommandImpl");
		        pw.printf("<command name='%s' builder='ClassBuilder' key='%s'>\n", te, impl);
				pw.printf("  <parameter name='Service'>%s</parameter>\n", value);
				pw.printf("</command>\n");
			}
		}
		pw.printf("\n");
		for (TypeElement te : ElementFilter.typesIn(annots.get(ARGUMENTS).values())) {
		    String impl=te.getQualifiedName().toString().replaceFirst("[A-Z]{1}.*\\.(.*)Argument$","$1ArgumentImpl");
		    pw.printf("<type name='%s' builder='ClassBuilder' key='%s'/>\n", te, impl);
		}
		pw.printf("\n");
		for (TypeElement te : ElementFilter.typesIn(annots.get(TYPES).values())) {
		    // default the name to the fully qualified class name
		    String name = te.toString();

		    // if the annotation defined a name (e.g. @TypeDefinition(name="MyName")), use that name.
		    TypeDefinition td=te.getAnnotation(TypeDefinition.class);
		    if (!TypeDefinition.DEFAULT_NAME.equals(td.name())) {
		        name=td.name();
		    }

			pw.printf("<type name='%s' builder='ClassBuilder' key='%s'/>\n", name, te);
		}
		pw.printf("  </types>\n");
		pw.printf("</configuration>\n");

		pw.close();
	}

	private void generateArgumentImpls() throws IOException {
		for (TypeElement te : ElementFilter.typesIn(annots.get(ARGUMENTS).values())) {
			if (!te.getQualifiedName().toString().endsWith("Argument")) {
				error("Argument class name '" + te.getQualifiedName() + "' does not end in \"Argument\"");
			}
			generateArgumentImpl(te);
		}
	}

	private void generateCommandImpls() throws IOException {
		for (TypeElement te : ElementFilter.typesIn(annots.get(COMMANDS).values())) {
			if (!te.getQualifiedName().toString().endsWith("Command")) {
				error("Command class name '" + te.getQualifiedName() + "' does not end in \"Command\"");
			}
			generateCommandImpl(te);
		}
	}

	/**
	 * Generate the ArgImp class for the command.
	 */
	private void generateArgumentImpl(TypeElement te) throws IOException {
        // com.ail.core.thing.ThingService.ThingArgument -> com.ail.core.thing.ThingArgumentImpl
		String qualifiedClassName = te.getQualifiedName().toString().replaceFirst("[A-Z]{1}.*\\.(.*)Argument$","$1ArgumentImpl");
		// ThingArgument -> ThingArgumentImp
		String simpleClassName = te.getSimpleName().toString().replaceFirst("Argument$", "ArgumentImpl");

		OutputStream os = filer.createSourceFile(qualifiedClassName, (Element[]) null).openOutputStream();
		PrintWriter pw = new PrintWriter(os);

		pw.printf("// Generated by %s\n", Processor.class.getName());
		pw.printf("\n");
		pw.printf("package %s;\n", qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf('.')));
		pw.printf("\n");
		pw.printf("import com.ail.core.command.ArgumentImpl;\n");
		pw.printf("\n");
		pw.printf("public class %s extends ArgumentImpl implements %s {\n", simpleClassName, te);
        pw.printf("  static final long serialVersionUID = %dL;\n", (long)(Math.random()*Long.MAX_VALUE));
        pw.printf("\n");

		for (ExecutableElement ee : ElementFilter.methodsIn(te.getEnclosedElements())) {
			if ("get".equals(ee.getSimpleName().subSequence(0, 3))) {
				String type = ee.getReturnType().toString();
				String methodName = ee.getSimpleName().toString();
				String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
				pw.printf("  private %s %s;\n", type, fieldName);
			}
		}

		pw.printf("\n");

		for (ExecutableElement ee : ElementFilter.methodsIn(te.getEnclosedElements())) {
			String methodName = ee.getSimpleName().toString();
			String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

			if (methodName.startsWith("get")) {
				String type = ee.getReturnType().toString();

				pw.printf("  public %s %s() {\n", type, methodName);
				pw.printf("    return %s;\n", fieldName);
				pw.printf("  }\n\n");
			} else if (methodName.startsWith("set")) {
				String type = ee.getParameters().get(0).asType().toString();

				pw.printf("  public void %s(%s %s) {\n", methodName, type, fieldName);
				pw.printf("    this.%1$s=%1$s;\n", fieldName);
				pw.printf("  }\n\n");
			}
		}

		pw.printf("}\n");

		pw.close();
	}

	/**
	 * Generate a CommandImp class for a CommandDefinition
	 */
	private void generateCommandImpl(TypeElement te) throws IOException {
		// com.ail.core.thing.ThingService.ThingCommand -> com.ail.core.thing.ThingCommandImp
        String qualifiedClassName = te.getQualifiedName().toString().replaceFirst("[A-Z]{1}.*\\.(.*)Command$","$1CommandImpl");
		// ThingCommand -> ThingCommandImp;
		String simpleClassName = te.getSimpleName() + "Impl";
		// com.ail.core.thing.ThingCommand -> com.ail.core.thing.ThingArgumentImpl
        String qualifiedArgumentImpl = te.getQualifiedName().toString().replaceFirst("[A-Z]{1}.*\\.(.*)Command$","$1ArgumentImpl");
		// com.ail.core.thing.ThingCommand -> com.ail.core.thing.ThingArgument
		String qualifiedArgument = te.getQualifiedName().toString().replaceFirst("Command$", "Argument");

		OutputStream os = filer.createSourceFile(qualifiedClassName, (Element[]) null).openOutputStream();
		PrintWriter pw = new PrintWriter(os);

		pw.printf("// Generated by %s\n", Processor.class.getName());
		pw.printf("\n");
		pw.printf("package %s;\n\n", qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf('.')));
		pw.printf("\n");
		pw.printf("import com.ail.core.command.Accessor;\n");
		pw.printf("import com.ail.core.command.Argument;\n");
		pw.printf("import com.ail.core.command.CommandImpl;\n");
		pw.printf("\n");
		pw.printf("public class %s extends CommandImpl implements %s {\n", simpleClassName, te);
		pw.printf("  static final long serialVersionUID = %dL;\n", (long)(Math.random()*Long.MAX_VALUE));
        pw.printf("\n");
		pw.printf("  private Accessor accessor;\n");
		pw.printf("  private %s args;\n", qualifiedArgument);
		pw.printf("\n");
		pw.printf("  public %s() {\n", simpleClassName);
		pw.printf("    super();\n");
		pw.printf("    args=new %s();\n", qualifiedArgumentImpl);
		pw.printf("  }\n");
		pw.printf("\n");
		pw.printf("  public %s(Accessor accessor, %s args) {\n", simpleClassName, qualifiedArgument);
		pw.printf("    setAccessor(accessor);\n");
		pw.printf("    setArgs(args);\n");
		pw.printf("  }\n");
		pw.printf("\n");
		pw.printf("  public Accessor getAccessor() {\n");
		pw.printf("    return accessor;\n");
		pw.printf("  }\n");
		pw.printf("\n");
		pw.printf("  public void setAccessor(Accessor accessor) {\n");
		pw.printf("    this.accessor=accessor;\n");
		pw.printf("  }\n");
		pw.printf("\n");
		pw.printf("  public void setService(Accessor accessor) {\n");
		pw.printf("    this.accessor=accessor;\n");
		pw.printf("  }\n");
		pw.printf("\n");
		pw.printf("  public void setArgs(Argument arg) {\n");
		pw.printf("    this.args=(%s)arg;\n", qualifiedArgument);
		pw.printf("  }\n");
		pw.printf("\n");
		pw.printf("  public Argument getArgs() {\n");
		pw.printf("    return args;\n");
		pw.printf("  }\n");
		pw.printf("\n");

		Element interfaceElement = annots.get(ARGUMENTS).get(qualifiedArgument);

		for (ExecutableElement ee : ElementFilter.methodsIn(interfaceElement.getEnclosedElements())) {
			String methodName = ee.getSimpleName().toString();
			String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

			if (methodName.startsWith("get")) {
				String type = ee.getReturnType().toString();

				pw.printf("  public %s %s() {\n", type, methodName);
				pw.printf("    return args.%s();\n", methodName);
				pw.printf("  }\n\n");
			} else if (methodName.startsWith("set")) {
				String type = ee.getParameters().get(0).asType().toString();

				pw.printf("  public void %s(%s %s) {\n", methodName, type, fieldName);
				pw.printf("    args.%s(%s);\n", methodName, fieldName);
				pw.printf("  }\n\n");
			}
		}

		pw.printf("}");

		pw.close();
	}

	private void error(String message) {
		messager.printMessage(Kind.ERROR, message);
	}
}
