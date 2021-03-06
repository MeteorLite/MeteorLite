/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.java.decompiler.main.collectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.java.decompiler.main.ClassesProcessor.ClassNode;
import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.TextBuffer;
import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.struct.StructContext;
import org.jetbrains.java.decompiler.struct.StructField;

public class ImportCollector {

  private static final String JAVA_LANG_PACKAGE = "java.lang";

  private final Map<String, String> mapSimpleNames = new HashMap<>();
  private final Set<String> setNotImportedNames = new HashSet<>();
  // set of field names in this class and all its predecessors.
  private final Set<String> setFieldNames = new HashSet<>();
  private final String currentPackageSlash;
  private final String currentPackagePoint;

  public ImportCollector(ClassNode root) {
    String clName = root.classStruct.qualifiedName;
    int index = clName.lastIndexOf('/');
    if (index >= 0) {
      String packageName = clName.substring(0, index);
      currentPackageSlash = packageName + '/';
      currentPackagePoint = packageName.replace('/', '.');
    } else {
      currentPackageSlash = "";
      currentPackagePoint = "";
    }

    Map<String, StructClass> classes = DecompilerContext.getStructContext().getClasses();
    StructClass currentClass = root.classStruct;
    while (currentClass != null) {
      // all field names for the current class ..
      for (StructField f : currentClass.getFields()) {
        setFieldNames.add(f.getName());
      }

      // .. and traverse through parent.
      currentClass =
          currentClass.superClass != null ? classes.get(currentClass.superClass.getString()) : null;
    }
  }

  /**
   * Check whether the package-less name ClassName is shaded by variable in a context of the
   * decompiled class
   *
   * @param classToName - pkg.name.ClassName - class to find shortname for
   * @return ClassName if the name is not shaded by local field, pkg.name.ClassName otherwise
   */
  public String getShortNameInClassContext(String classToName) {
    String shortName = getShortName(classToName);
    if (setFieldNames.contains(shortName)) {
      return classToName;
    } else {
      return shortName;
    }
  }

  public String getShortName(String fullName) {
    return getShortName(fullName, true);
  }

  public String getShortName(String fullName, boolean imported) {
    ClassNode node = DecompilerContext.getClassProcessor().getMapRootClasses()
        .get(fullName.replace('.', '/'));

    String result = null;
    if (node != null && node.classStruct.isOwn()) {
      result = node.simpleName;

      while (node.parent != null && node.type == ClassNode.CLASS_MEMBER) {
        result = node.parent.simpleName + '.' + result;
        node = node.parent;
      }

      if (node.type == ClassNode.CLASS_ROOT) {
        fullName = node.classStruct.qualifiedName;
        fullName = fullName.replace('/', '.');
      } else {
        return result;
      }
    } else {
      fullName = fullName.replace('$', '.');
    }

    String shortName = fullName;
    String packageName = "";

    int lastDot = fullName.lastIndexOf('.');
    if (lastDot >= 0) {
      shortName = fullName.substring(lastDot + 1);
      packageName = fullName.substring(0, lastDot);
    }

    StructContext context = DecompilerContext.getStructContext();

    // check for another class which could 'shadow' this one. Two cases:
    // 1) class with the same short name in the current package
    // 2) class with the same short name in the default package
    boolean existsDefaultClass =
        (context.getClass(currentPackageSlash + shortName) != null && !packageName
            .equals(currentPackagePoint)) || // current package
            (context.getClass(shortName) != null && !currentPackagePoint
                .isEmpty());  // default package

    if (existsDefaultClass ||
        (mapSimpleNames.containsKey(shortName) && !packageName
            .equals(mapSimpleNames.get(shortName)))) {
      //  don't return full name because if the class is a inner class, full name refers to the parent full name, not the child full name
      return result == null ? fullName : (packageName + "." + result);
    } else if (!mapSimpleNames.containsKey(shortName)) {
      mapSimpleNames.put(shortName, packageName);
      if (!imported) {
        setNotImportedNames.add(shortName);
      }
    }

    return result == null ? shortName : result;
  }

  public int writeImports(TextBuffer buffer) {
    int importLinesWritten = 0;

    List<String> imports = packImports();

    for (String s : imports) {
      buffer.append("import ");
      buffer.append(s);
      buffer.append(';');
      buffer.appendLineSeparator();

      importLinesWritten++;
    }

    return importLinesWritten;
  }

  private List<String> packImports() {
    return mapSimpleNames.entrySet().stream()
        .filter(ent ->
            // exclude the current class or one of the nested ones
            // empty, java.lang and the current packages
            !setNotImportedNames.contains(ent.getKey()) &&
                !ent.getValue().isEmpty() &&
                !JAVA_LANG_PACKAGE.equals(ent.getValue()) &&
                !ent.getValue().equals(currentPackagePoint)
        )
        .sorted(
            Map.Entry.<String, String>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
        .map(ent -> ent.getValue() + "." + ent.getKey())
        .collect(Collectors.toList());
  }
}