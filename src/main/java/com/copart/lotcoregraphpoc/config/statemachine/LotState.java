package com.copart.lotcoregraphpoc.config.statemachine;

public enum LotState {
	NEW,
	WAITING_FOR_CLEAR_CHARGES,
	WAITING_FOR_CLEAR_FOR_PICKUP,
	WAITING_FOR_DISPATCH,
	WAITING_FOR_INVENTORY,
	CLOSED
}
