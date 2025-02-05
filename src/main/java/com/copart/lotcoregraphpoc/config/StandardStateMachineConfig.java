package com.copart.lotcoregraphpoc.config;

import java.util.EnumSet;
import java.util.Set;

import com.copart.lotcoregraphpoc.config.statemachine.LotEvent;
import com.copart.lotcoregraphpoc.config.statemachine.LotState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachineFactory
public class StandardStateMachineConfig extends EnumStateMachineConfigurerAdapter<LotState, LotEvent> {

	@Override
	public void configure(StateMachineConfigurationConfigurer<LotState, LotEvent> config)
			throws Exception {
		config
				.withConfiguration()
				.autoStartup(true)
				.listener(listener());
	}

	@Override
	public void configure(StateMachineStateConfigurer<LotState, LotEvent> states)
			throws Exception {
		states
				.withStates()
				.initial(LotState.NEW)
				.end(LotState.CLOSED)
				.states(EnumSet.allOf(LotState.class));
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<LotState, LotEvent> transitions)
			throws Exception {
		transitions
				.withExternal()
				.source(LotState.NEW).target(LotState.WAITING_FOR_CLEAR_FOR_PICKUP).event(LotEvent.PICKUP_REQUIRED)
				.and()
				.withExternal()
				.source(LotState.WAITING_FOR_CLEAR_FOR_PICKUP).target(LotState.WAITING_FOR_CLEAR_CHARGES).event(LotEvent.CLEAR_FOR_PICKUP)
				.and()
				.withExternal()
				.source(LotState.WAITING_FOR_CLEAR_CHARGES).target(LotState.WAITING_FOR_DISPATCH).event(LotEvent.CLEAR_CHARGES)
				.and()
				.withExternal()
				.source(LotState.WAITING_FOR_DISPATCH).target(LotState.WAITING_FOR_INVENTORY).event(LotEvent.DISPATCH)
				.and()
				.withExternal()
				.source(LotState.NEW).target(LotState.WAITING_FOR_INVENTORY).event(LotEvent.NO_PICKUP_REQUIRED)
				.and().withExternal().source(LotState.WAITING_FOR_CLEAR_FOR_PICKUP).target(LotState.WAITING_FOR_INVENTORY).event(LotEvent.ALTERNATE);
	}

	@Bean
	public StateMachineListener<LotState, LotEvent> listener() {
		return new StateMachineListenerAdapter<LotState, LotEvent>() {
			@Override
			public void stateChanged(State<LotState, LotEvent> from, State<LotState, LotEvent> to) {
				System.out.println("State change to " + to.getId());
			}
		};
	}
}
