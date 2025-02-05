package com.copart.lotcoregraphpoc.config.statemachine;

public enum LotEvent {
	NO_PICKUP_REQUIRED,
	PICKUP_REQUIRED,
	ALTERNATE,
	CLEAR_CHARGES,
	CLEAR_FOR_PICKUP,
	DISPATCH,
	READY_FOR_INVENTORY
}
