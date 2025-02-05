package com.copart.lotcoregraphpoc.controllers;

import java.util.Collection;
import java.util.HashMap;

import com.copart.lotcoregraphpoc.config.statemachine.LotEvent;
import com.copart.lotcoregraphpoc.config.statemachine.LotState;
import com.copart.lotcoregraphpoc.dto.LotDto;
import com.copart.lotcoregraphpoc.entities.Lot;
import com.copart.lotcoregraphpoc.exceptions.ResourceNotFoundException;
import com.copart.lotcoregraphpoc.repositories.LotRepository;
import com.copart.lotcoregraphpoc.service.LotStateMachineService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

	@Autowired
	LotRepository lotRepository;

	@Autowired
	LotStateMachineService lotStateMachineService;

	@GetMapping(value = "/lot/graphviz/{lotNumber}", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String execute(@PathVariable Long lotNumber) {
		return lotStateMachineService.graphviz(lotNumber);
	}

	@SneakyThrows
	@GetMapping(path = "/lot/{lotNumber}/state", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LotState> initiate(@PathVariable Long lotNumber) {
		try {
			Lot lot = lotRepository.findById(lotNumber)
					.orElseThrow(() -> new ResourceNotFoundException("Lot not found"));
			StateMachine<LotState, LotEvent> stateMachine = lotStateMachineService.getNewOrRestoredStateMachine(lot);
			return ResponseEntity.ok(stateMachine.getState().getId());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@SneakyThrows
	@PostMapping(path = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LotDto> initiate(@RequestBody Lot lot) {
		lot = lotRepository.save(lot);
		if(!lot.isPickupRequired()){
			LotState state = lotStateMachineService.sendEvent(lot.getId(), LotEvent.NO_PICKUP_REQUIRED);
			lot.setState(state);

		} else {
			LotState state = lotStateMachineService.sendEvent(lot.getId(), LotEvent.PICKUP_REQUIRED);
			lot.setState(state);
		}
		lotRepository.save(lot);

		return ResponseEntity.ok(LotDto.builder().lot(lot).build());
	}

	@SneakyThrows
	@PostMapping(path = "/clearCharges/{lotNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LotState> clearCharges(@PathVariable Long lotNumber) {
		LotState state = lotStateMachineService.sendEvent(lotNumber, LotEvent.CLEAR_CHARGES);
		return ResponseEntity.ok(state);
	}

	@SneakyThrows
	@PostMapping(path = "/clearForPickup/{lotNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LotState> clearForPickup(@PathVariable Long lotNumber) {
		LotState state = lotStateMachineService.sendEvent(lotNumber, LotEvent.CLEAR_FOR_PICKUP);
		return ResponseEntity.ok(state);
	}



//	@GetMapping(path = "/perform/{action}/{lotNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<LotState> performAction(@PathVariable String action,@PathVariable Long lotNumber){
//		LotState lotState = lotStateRepository.findById(lotNumber).orElseThrow(() -> new ResourceNotFoundException("Lot not found with id " + lotNumber));
//		return ResponseEntity.ok(lotState);
//	}

}
