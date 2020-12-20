package top.nextnet;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TrainTest {

	@Test
	public void shiftSchedule() throws DelayException {
		Train t1 = new Train("t1");
		Stop s1 = new Stop("s1");
		Stop s2 = new Stop("s2");
		Stop s3 = new Stop("s3");

		t1.addInitialDeparture(s1, 0L);
		t1.addInitialArrival(s2, 10L);
		t1.addInitialDeparture(s2, 15L);
		t1.addInitialArrival(s3, 20L);

		assertThat(t1.getDepartureSchedule(s1), is(equalTo(0L)));
		assertThat(t1.getArrivalSchedule(s2), is(equalTo(10L)));
		assertThat(t1.getDepartureSchedule(s2), is(equalTo(15L)));
		assertThat(t1.getArrivalSchedule(s3), is(equalTo(20L)));

		t1.delayAllStopsBeforeArrivalAt(s2, 5L);

		assertThat(t1.getDepartureSchedule(s1), is(equalTo(0L)));
		assertThat(t1.getArrivalSchedule(s2), is(equalTo(15L)));
		assertThat(t1.getDepartureSchedule(s2), is(equalTo(20L)));
		assertThat(t1.getArrivalSchedule(s3), is(equalTo(25L)));

		t1.delayAllStopsBeforeArrivalAt(s2, 5L);

		assertThat(t1.getDepartureSchedule(s1), is(equalTo(0L)));
		assertThat(t1.getArrivalSchedule(s2), is(equalTo(20L)));
		assertThat(t1.getDepartureSchedule(s2), is(equalTo(25L)));
		assertThat(t1.getArrivalSchedule(s3), is(equalTo(30L)));
		

		


	}

	@Test
	public void shiftScheduleBeforeDeparture() throws DelayException {
		Train t1 = new Train("t1");
		Stop s1 = new Stop("s1");
		Stop s2 = new Stop("s2");
		Stop s3 = new Stop("s3");

		t1.addInitialDeparture(s1, 0L);
		t1.addInitialArrival(s2, 10L);
		t1.addInitialDeparture(s2, 15L);
		t1.addInitialArrival(s3, 20L);

		assertThat(t1.getDepartureSchedule(s1), is(equalTo(0L)));
		assertThat(t1.getArrivalSchedule(s2), is(equalTo(10L)));
		assertThat(t1.getDepartureSchedule(s2), is(equalTo(15L)));
		assertThat(t1.getArrivalSchedule(s3), is(equalTo(20L)));

		t1.delayAllStopsFromDeparture(s1, 5L);

		assertThat(t1.getDepartureSchedule(s1), is(equalTo(5L)));
		assertThat(t1.getArrivalSchedule(s2), is(equalTo(15L)));
		assertThat(t1.getDepartureSchedule(s2), is(equalTo(20L)));
		assertThat(t1.getArrivalSchedule(s3), is(equalTo(25L)));

		t1.delayAllStopsFromDeparture(s2, 10L);
		
		assertThat(t1.getDepartureSchedule(s1), is(equalTo(5L)));
		assertThat(t1.getArrivalSchedule(s2), is(equalTo(15L)));
		assertThat(t1.getDepartureSchedule(s2), is(equalTo(30L)));
		assertThat(t1.getArrivalSchedule(s3), is(equalTo(35L)));
		
		
	}

}
