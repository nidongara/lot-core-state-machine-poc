package com.copart.lotcoregraphpoc.config;

import com.copart.lotcoregraphpoc.config.statemachine.LotEvent;
import com.copart.lotcoregraphpoc.config.statemachine.LotState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.data.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.persist.StateMachinePersister;

@Configuration
public class RedisStateMachineConfig {
	@Bean
	public StateMachinePersist<LotState, LotEvent, String> stateMachinePersist(RedisConnectionFactory redisConnectionFactory) {
		RedisStateMachineContextRepository<LotState, LotEvent> repository =
				new RedisStateMachineContextRepository<LotState, LotEvent>(redisConnectionFactory);
		return new RepositoryStateMachinePersist<LotState, LotEvent>(repository);
	}

	@Bean
	public StateMachinePersister<LotState, LotEvent, String> stateMachinePersister
			(StateMachinePersist<LotState, LotEvent, String> stateMachinePersist) {
		return new DefaultStateMachinePersister<>(stateMachinePersist);
	}
}
