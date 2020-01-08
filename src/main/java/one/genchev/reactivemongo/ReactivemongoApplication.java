package one.genchev.reactivemongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
//@EnableTransactionManagement
//@ComponentScan({"one.genchev.reactivemongo"})
//@EntityScan("one.genchev.reactivemongo")
public class ReactivemongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactivemongoApplication.class, args);
	}

}


@Component
@RequiredArgsConstructor
@Log4j2
class ReactiveInitializer {

	private final FruitRepository fruitRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		var fruitFlux = Flux.just("Apple ", "Orange ", "Grape ", "Banana ", "Strawberry ")
				.map(name -> new Fruit(null, name))
				.flatMap(this.fruitRepository::save);

		fruitRepository
				.deleteAll()
				.thenMany(fruitFlux)
				.thenMany(this.fruitRepository.findAll())
				.subscribe(log::info);
	}
}

//@Configuration
//@EnableR2dbcRepositories
//class R2dbcConfig extends AbstractR2dbcConfiguration {
//
//	@Override
//	public ConnectionFactory connectionFactory() {
//		return new PostgresqlConnectionFactory(
//				PostgresqlConnectionConfiguration.builder()
//						.username("fruit")
//						.password("fruit")
//						.host("localhost")
//						.database("fruit")
//						.build()
//		);
//	}
//}

interface FruitRepository extends ReactiveCrudRepository<Fruit, String> {

//	@Tailable
//	Flux<Fruit> findByName(String name);

}

//@Component
//class IntervalMessageProducer {
//	Flux<GreetingResponse> produce(GreetingRequest name) {
//		return Flux.fromStream(Stream.generate(() -> "Hello " + name.getName() + " @ " + Instant.now()))
//				.map(GreetingResponse::new)
//				.delayElements(Duration.ofSeconds(1));
//	}
//}

@RestController
@RequiredArgsConstructor
class FruitRestController {

	private final FruitRepository fruitRepository;
//	private final IntervalMessageProducer intervalMessageProducer;

	@GetMapping("/fruit")
	Publisher<Fruit> fruitPublisher() {
		return fruitRepository.findAll();
	}

//	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/sse/{n}")
//	Publisher<GreetingResponse > stringPublisher(@PathVariable String n) {
//		return intervalMessageProducer.produce(new GreetingRequest(n));
//	}

	@GetMapping("/basic")
	List<String> basicPublisher() {
		return Arrays.asList("1", "2", "3", "4", "5");
	}

	@GetMapping("/flux")
	Publisher<String> fluxPublisher() {
		return Flux.just("Apple ", "Orange ", "Grape ", "Banana ", "Strawberry ").delayElements(Duration.ofSeconds(2));
	}
}

@Data
//@Document
@AllArgsConstructor
@NoArgsConstructor
class Fruit {
//	@Id
	private String id;
	private String name;
}

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//class GreetingRequest {
//	private String name;
//}
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//class GreetingResponse {
//	private String greeting;
//}



