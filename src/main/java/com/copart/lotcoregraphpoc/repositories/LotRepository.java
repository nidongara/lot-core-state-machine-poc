package com.copart.lotcoregraphpoc.repositories;

import com.copart.lotcoregraphpoc.entities.Lot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends CrudRepository<Lot, Long> {
}
