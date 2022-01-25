package com.openosrs.injector.injectors.raw;

import com.openosrs.injector.injection.InjectData;
import com.openosrs.injector.injectors.AbstractInjector;
import net.runelite.asm.ClassFile;
import net.runelite.asm.Method;
import net.runelite.asm.attributes.Code;
import net.runelite.asm.attributes.code.Instruction;
import net.runelite.asm.attributes.code.InstructionType;
import net.runelite.asm.attributes.code.Instructions;
import net.runelite.asm.attributes.code.instructions.*;
import net.runelite.asm.signature.Signature;

import java.util.List;

public class RuneLiteIterableHashTable extends AbstractInjector
{
	private static final String RUNELITE_ITERABLE_HASHTABLE = "osrs/RuneLiteIterableHashTable";

	public RuneLiteIterableHashTable(InjectData inject)
	{
		super(inject);
	}

	public void inject()
	{
		ClassFile runeliteIterableHashTableVanilla = inject.vanilla.findClass(RUNELITE_ITERABLE_HASHTABLE);

		final ClassFile nodeHashTableVanilla = inject.vanilla.findClass("osrs/NodeHashTable");

		Method copy = new Method(nodeHashTableVanilla, "iterator", new Signature("()Ljava/util/Iterator;"));
		copy.setPublic();

		final Code code = new Code(copy);
		code.setMaxStack(3);
		copy.setCode(code);
		nodeHashTableVanilla.addMethod(copy);

		final Instructions instructions = code.getInstructions();
		final List<Instruction> ins = instructions.getInstructions();

		ins.add(new New(instructions, runeliteIterableHashTableVanilla.getPoolClass()));
		ins.add(new Dup(instructions));
		ins.add(new ALoad(instructions, 0));
		ins.add(new InvokeSpecial(instructions, new net.runelite.asm.pool.Method(runeliteIterableHashTableVanilla.getPoolClass(), "<init>", new Signature("(L" + nodeHashTableVanilla.getName() + ";)V"))));
		ins.add(new Return(instructions, InstructionType.ARETURN));
	}
}
