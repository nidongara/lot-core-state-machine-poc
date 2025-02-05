package com.copart.lotcoregraphpoc.service;

import java.util.Collection;

import com.copart.lotcoregraphpoc.config.statemachine.LotEvent;
import com.copart.lotcoregraphpoc.config.statemachine.LotState;
import com.copart.lotcoregraphpoc.entities.Lot;
import com.copart.lotcoregraphpoc.exceptions.ResourceNotFoundException;
import com.copart.lotcoregraphpoc.repositories.LotRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

@Service
public class LotStateMachineService {
	@Autowired
	LotRepository lotRepository;

	@Autowired
	StateMachineFactory<LotState, LotEvent> lotStateMachineFactory;

	@Autowired
	StateMachinePersister<LotState, LotEvent, String> stateMachinePersister;

	public LotState sendEvent(Long lotNumber, LotEvent event) throws Exception {
		Lot lot = lotRepository.findById(lotNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Lot not found"));

		StateMachine<LotState, LotEvent> stateMachine = getNewOrRestoredStateMachine(lot);
		stateMachine.sendEvent(event);

		// Update and save the order state
		lot.setState(stateMachine.getState().getId());
		lotRepository.save(lot);
		stateMachinePersister.persist(stateMachine, lotNumber.toString());

		return lot.getState();
	}


	@SneakyThrows
	public String graphviz(Long lotNumber) {
		Lot lot = lotRepository.findById(lotNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Lot not found"));

		StateMachine<LotState, LotEvent> stateMachine = getNewOrRestoredStateMachine(lot);
		Collection<Transition<LotState, LotEvent>> transitions = stateMachine.getTransitions();

		StringBuilder dotBuilder = new StringBuilder();
		dotBuilder.append("digraph LotStateMachine {\n");
//		dotBuilder.append("  rankdir=LR;\n"); // Left to Right graph

		for (Transition<LotState, LotEvent> transition : transitions) {
			if (transition.getSource() != null && transition.getTarget() != null) {
				dotBuilder.append("  ")
						.append(transition.getSource().getId())
						.append(" -> ")
						.append(transition.getTarget().getId())
						.append(" [label=\"")
						.append(transition.getTrigger().getEvent())
						.append("\", color=\"green\"];\n");
			}
		}

		dotBuilder.append("}");
		return dotBuilder.toString();

	}

	public StateMachine<LotState, LotEvent> getNewOrRestoredStateMachine(Lot lot) throws Exception {
		StateMachine<LotState, LotEvent> stateMachine = lotStateMachineFactory.getStateMachine();
		stateMachinePersister.restore(stateMachine, lot.getId().toString());
		return stateMachine;
	}

}
