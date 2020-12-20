package top.nextnet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class DelayServiceTest {

	@Test
	void testDelay() {

		Train tA = new Train("A");
		Train tB = new Train("B");
		Train tC = new Train("C");
		Stop city1 = new Stop("city1");
		Stop city2 = new Stop("city2");
		Stop city3 = new Stop("city3");
		Stop city4 = new Stop("city4");
		Stop city5 = new Stop("city5");

		tA.addInitialDeparture(city1, 0L);
		tA.addInitialArrival(city2, 10L);
		tA.addInitialDeparture(city2, 15L);
		tA.addInitialArrival(city3, 25L);

		tB.addInitialDeparture(city2, 15L);
		tB.addInitialArrival(city4, 30L);
		tB.addInitialDeparture(city4, 50L);
		tB.addInitialArrival(city5, 55L);

		tC.addInitialDeparture(city3, 30L);
		tC.addInitialArrival(city4, 45L);

		System.out.println(tA);
		System.out.println(tB);
		System.out.println(tC);
		System.out.println("**********\n\n");

		DelayService service = new DelayService(tA, tB, tC);
		service.handleDelay(new Delay(tA, city2, 10L));
		service.handleDelay(new Delay(tC, city4, 10L));
		service.handleDelay(new Delay(tA, city3, 10L));

		assertThat(tA.getDepartureSchedule(city2), is(equalTo(25L)));
		assertThat("Train A connected to Train C in city 3",
				tA.getArrivalSchedule(city3) < tC.getDepartureSchedule(city3));
		assertThat("Train A connected to Train B in city 2",
				tA.getArrivalSchedule(city2) < tB.getDepartureSchedule(city2));
		assertThat("Train B connected to Train C in city 4",
				tB.getDepartureSchedule(city4) > tC.getArrivalSchedule(city4));

		System.out.println(tA);
		System.out.println(tB);
		System.out.println(tC);

	}

}