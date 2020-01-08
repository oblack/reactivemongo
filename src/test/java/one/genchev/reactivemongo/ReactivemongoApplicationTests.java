package one.genchev.reactivemongo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ReactivemongoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testFlatMap() {

		List<List<String>> myList = asList(
				asList("a"),
				asList("b")
		);

		System.out.println(myList
			.stream()
			.flatMap(Collection::stream)
			.collect(Collectors.toList()));

//				Stream.of("a", "b")
//				.map(String::toUpperCase)
//				.collect(Collectors.toList());

//		assertEquals(asList("A", "B"), myList);
	}

}
