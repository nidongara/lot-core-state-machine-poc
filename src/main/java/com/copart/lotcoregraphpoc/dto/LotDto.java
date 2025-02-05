package com.copart.lotcoregraphpoc.dto;

import com.copart.lotcoregraphpoc.entities.Lot;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LotDto {
	Lot lot;
}
