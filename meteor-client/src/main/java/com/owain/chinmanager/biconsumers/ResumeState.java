package com.owain.chinmanager.biconsumers;

import com.owain.chinmanager.ChinManagerContext;
import com.owain.chinmanager.ChinManagerPlugin;
import com.owain.chinmanager.ChinManagerStates;
import com.owain.chinstatemachine.StateMachine;
import com.owain.chinmanager.ChinManagerState;
import io.reactivex.rxjava3.functions.BiConsumer;

public class ResumeState
{
	public BiConsumer<ChinManagerContext, StateMachine.State<ChinManagerContext, ChinManagerStates>> resume()
	{
		return (t1, state) -> {
			if (ChinManagerPlugin.shouldSetup)
			{
				ChinManagerState.stateMachine.accept(ChinManagerStates.SETUP);
			}
			else
			{
				ChinManagerState.stateMachine.accept(ChinManagerStates.IDLE);
			}
		};
	}
}
