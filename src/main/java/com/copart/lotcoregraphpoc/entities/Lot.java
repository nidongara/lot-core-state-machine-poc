package com.copart.lotcoregraphpoc.entities;

import java.io.Serializable;

import com.copart.lotcoregraphpoc.config.statemachine.LotState;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
public class Lot implements Serializable {
	Long id; // Lot Number
	String type; // STANDARD, OFFSITE, TITLE_ONLY
	String year;
	String make;
	String model;
	boolean isPickupRequired;
	LotState state = LotState.NEW;
}
