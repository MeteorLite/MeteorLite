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
package org.jetbrains.java.decompiler.struct.attr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.java.decompiler.struct.consts.ConstantPool;
import org.jetbrains.java.decompiler.util.DataInputFullStream;

/*
  u2 local_variable_table_length;
  local_variable {
    u2 start_pc;
    u2 length;
    u2 name_index;
    u2 descriptor_index;
    u2 index;
  }
*/
public class StructLocalVariableTableAttribute extends StructGeneralAttribute {

  private List<LocalVariable> localVariables = Collections.emptyList();

  @Override
  public void initContent(DataInputFullStream data, ConstantPool pool) throws IOException {
    int len = data.readUnsignedShort();
    if (len > 0) {
      localVariables = new ArrayList<>(len);

      for (int i = 0; i < len; i++) {
        int start_pc = data.readUnsignedShort();
        int length = data.readUnsignedShort();
        int nameIndex = data.readUnsignedShort();
        int descriptorIndex = data.readUnsignedShort();
        int varIndex = data.readUnsignedShort();
        localVariables.add(new LocalVariable(start_pc,
            length,
            pool.getPrimitiveConstant(nameIndex).getString(),
            pool.getPrimitiveConstant(descriptorIndex).getString(),
            varIndex));
      }
    } else {
      localVariables = Collections.emptyList();
    }
  }

  public void add(StructLocalVariableTableAttribute attr) {
    localVariables.addAll(attr.localVariables);
  }

  public String getName(int index, int visibleOffset) {
    return matchingVars(index, visibleOffset).map(v -> v.name).findFirst().orElse(null);
  }

  public String getDescriptor(int index, int visibleOffset) {
    return matchingVars(index, visibleOffset).map(v -> v.descriptor).findFirst().orElse(null);
  }

  private Stream<LocalVariable> matchingVars(int index, int visibleOffset) {
    return localVariables.stream()
        .filter(v -> v.index == index && (visibleOffset >= v.start_pc
            && visibleOffset < v.start_pc + v.length));
  }

  public boolean containsName(String name) {
    return localVariables.stream().anyMatch(v -> v.name == name);
  }

  public Map<Integer, String> getMapParamNames() {
    return localVariables.stream().filter(v -> v.start_pc == 0)
        .collect(Collectors.toMap(v -> v.index, v -> v.name, (n1, n2) -> n2));
  }

  private static class LocalVariable {

    final int start_pc;
    final int length;
    final String name;
    final String descriptor;
    final int index;

    private LocalVariable(int start_pc, int length, String name, String descriptor, int index) {
      this.start_pc = start_pc;
      this.length = length;
      this.name = name;
      this.descriptor = descriptor;
      this.index = index;
    }
  }
}
