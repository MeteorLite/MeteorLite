/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package org.jetbrains.java.decompiler.modules.decompiler.exps;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jetbrains.java.decompiler.main.TextBuffer;
import org.jetbrains.java.decompiler.main.collectors.BytecodeMappingTracer;
import org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor;
import org.jetbrains.java.decompiler.modules.decompiler.vars.CheckTypesResult;
import org.jetbrains.java.decompiler.struct.gen.VarType;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

public class ArrayExprent extends Exprent {

  private final VarType hardType;
  private Exprent array;
  private Exprent index;

  public ArrayExprent(Exprent array, Exprent index, VarType hardType,
      Set<Integer> bytecodeOffsets) {
    super(EXPRENT_ARRAY);
    this.array = array;
    this.index = index;
    this.hardType = hardType;

    addBytecodeOffsets(bytecodeOffsets);
  }

  @Override
  public Exprent copy() {
    return new ArrayExprent(array.copy(), index.copy(), hardType, bytecode);
  }

  @Override
  public VarType getExprType() {
    VarType exprType = array.getExprType();
    if (exprType.equals(VarType.VARTYPE_NULL)) {
      return hardType.copy();
    } else {
      return exprType.decreaseArrayDim();
    }
  }

  public int getExprentUse() {
    return array.getExprentUse() & index.getExprentUse() & Exprent.MULTIPLE_USES;
  }

  public CheckTypesResult checkExprTypeBounds() {
    CheckTypesResult result = new CheckTypesResult();
    result.addMinTypeExprent(index, VarType.VARTYPE_BYTECHAR);
    result.addMaxTypeExprent(index, VarType.VARTYPE_INT);
    return result;
  }

  public List<Exprent> getAllExprents() {
    List<Exprent> lst = new ArrayList<>();
    lst.add(array);
    lst.add(index);
    return lst;
  }

  @Override
  public TextBuffer toJava(int indent, BytecodeMappingTracer tracer) {
    TextBuffer res = array.toJava(indent, tracer);

    if (array.getPrecedence() > getPrecedence()) { // array precedence equals 0
      res.enclose("(", ")");
    }

    VarType arrType = array.getExprType();
    if (arrType.arrayDim == 0) {
      VarType objArr = VarType.VARTYPE_OBJECT.resizeArrayDim(1); // type family does not change
      res.enclose("((" + ExprProcessor.getCastTypeName(objArr) + ")", ")");
    }

    tracer.addMapping(bytecode);

    return res.append('[').append(index.toJava(indent, tracer)).append(']');
  }

  @Override
  public void replaceExprent(Exprent oldExpr, Exprent newExpr) {
    if (oldExpr == array) {
      array = newExpr;
    }
    if (oldExpr == index) {
      index = newExpr;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null || !(o instanceof ArrayExprent)) {
      return false;
    }

    ArrayExprent arr = (ArrayExprent) o;
    return InterpreterUtil.equalObjects(array, arr.getArray()) &&
        InterpreterUtil.equalObjects(index, arr.getIndex());
  }

  public Exprent getArray() {
    return array;
  }

  public Exprent getIndex() {
    return index;
  }
}