package one.genchev.reactivemongo;

import javafx.beans.binding.MapExpression;
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
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootApplication
public class ReactivemongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactivemongoApplication.class, args);
	}

}


@Component
@RequiredArgsConstructor
@Log4j2
class ReactiveMongo {

	private final ReservationRepository reservationRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		var reservationFlux = Flux.just("Apple ", "Orange ", "Grape ", "Banana ", "Strawberry ")
				.map(name -> new Reservation(null, name))
				.flatMap(this.reservationRepository::save);

		reservationRepository.deleteAll()
				.thenMany(reservationFlux)
				.thenMany(this.reservationRepository.findAll())
				.subscribe(log::info);
	}
}

interface ReservationRepository extends ReactiveCrudRepository<one.genchev.reactivemongo.Reservation, String> {

}

@Component
class IntervalMessageProducer {
	Flux<GreetingResponse> produce(GreetingRequest name) {
		return Flux.fromStream(Stream.generate(() -> "Hello " + name.getName() + " @ " + Instant.now()))
				.map(GreetingResponse::new)
				.delayElements(Duration.ofSeconds(1));
	}
}

@RestController
@RequiredArgsConstructor
class ReservationRestController {
	private final ReservationRepository reservationRepository;
	private final IntervalMessageProducer intervalMessageProducer;

	@GetMapping("/reservations")
	Publisher<Reservation> reservationPublisher() {
		return reservationRepository.findAll();
	}

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/sse/{n}")
	Publisher<GreetingResponse > stringPublisher(@PathVariable String n) {
		return intervalMessageProducer.produce(new GreetingRequest(n));
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingRequest {
	private String name;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingResponse {
	private String greeting;
}



@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
class Reservation {
	@Id
	private String id;
	private String name;
}