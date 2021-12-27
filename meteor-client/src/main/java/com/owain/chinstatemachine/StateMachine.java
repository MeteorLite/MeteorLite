package com.owain.chinstatemachine;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.HashMap;
import java.util.Map;

import meteor.Logger;

public class StateMachine<T, E> implements Consumer<E>
{
	private Logger log = new Logger("Chin-StateMachine");
	public static class State<T, E>
	{
		private final String name;
		private BiConsumer<T, State<T, E>> enter;
		private BiConsumer<T, State<T, E>> exit;
		private final Map<E, State<T, E>> transitions = new HashMap<>();

		public State(String name)
		{
			this.name = name;
		}

		public State<T, E> onEnter(BiConsumer<T, State<T, E>> func)
		{
			this.enter = func;
			return this;
		}

		public State<T, E> onExit(BiConsumer<T, State<T, E>> func)
		{
			this.exit = func;
			return this;
		}

		public void enter(T context)
		{
			try
			{
				enter.accept(context, this);
			}
			catch (Throwable ignored)
			{
			}
		}

		public void exit(T context)
		{
			try
			{
				exit.accept(context, this);
			}
			catch (Throwable ignored)
			{
			}
		}

		public State<T, E> transition(E event, State<T, E> state)
		{
			transitions.put(event, state);
			return this;
		}

		public State<T, E> next(E event)
		{
			return transitions.get(event);
		}

		public String toString()
		{
			return name;
		}
	}

	private volatile State<T, E> state;
	private final PublishSubject<State<T, E>> stateObservable = PublishSubject.create();
	private final T context;
	private final PublishSubject<E> events = PublishSubject.create();

	public StateMachine(T context, State<T, E> initial)
	{
		this.context = context;
		this.state = initial;

		stateObservable.onNext(initial);
	}

	public Observable<Void> connect()
	{
		return Observable.create(sub -> {
			state.enter(context);

			sub.setDisposable(events.collect(() -> context, (context, event) -> {
				final State<T, E> next = state.next(event);

				if (next != null)
				{
					if (!state.name.equals(event.toString()))
					{
						log.debug("Transition: " + state + " -> " + event);

						stateObservable.onNext(state);

						state.exit(context);
						state = next;
						next.enter(context);
					}
				}
				else
				{
					log.debug("Invalid event: " + state + " -> " + event);
					log.debug("Valid transitions: {} ", state.transitions);
				}
			}).subscribe());
		});
	}

	@Override
	public void accept(E event)
	{
		events.onNext(event);
	}

	public @NonNull Observable<State<T, E>> getStateObservable()
	{
		return stateObservable.hide();
	}

	public State<T, E> getState()
	{
		return state;
	}
}