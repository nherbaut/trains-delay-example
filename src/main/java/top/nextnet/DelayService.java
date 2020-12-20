package top.nextnet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * THis class computes the delays applicable for trains
 * 
 * @author nherbaut
 *
 */
public class DelayService {

	Set<Train> trains;

	/**
	 * 
	 * @param trains some trains to handle
	 */
	public DelayService(Train... trains) {
		this.trains = new HashSet<Train>(Arrays.asList(trains));

	}

	/**
	 * for a give stop, return a list of trains in correspondance according to their
	 * initial schedule
	 * 
	 * @param stop
	 * @param schedule
	 * @return
	 */
	private Collection<Train> getConnectionTrains(Train train, Stop stop, Long schedule) {
		Collection<Train> res = new HashSet<>();
		for (Train correspondingTrain : this.trains) {
			if (correspondingTrain.equals(train)) {
				// we are not corresponding with ourself
				continue;
			}
			if (correspondingTrain.departFrom(stop)) {
				if (correspondingTrain.getInitialDepartureSchedule(stop) > schedule) {
					res.add(correspondingTrain);
				} else {
					// this trains leaves before the other one arrives, so don't add it
				}
			}

		}
		return res;
	}

	/**
	 * modify the trains schedule to support the delay
	 * 
	 * @param delay
	 */
	public void handleDelay(Delay delay) {
		Queue<Delay> delays = new LinkedList<>();
		delays.add(delay);
		while (!delays.isEmpty()) {

			Delay nextDelay = delays.poll();

			delays.addAll(this.handleSingleDelay(nextDelay));

			condenseRegisteredDelays(delays);

		}
	}

	/**
	 * if there are 2 or more delays for the same stop for the same train, keep only
	 * the largest
	 * 
	 * @param delays a collection of delays
	 */
	private static void condenseRegisteredDelays(Collection<Delay> delays) {
		Map<Train, Delay> trainDelayMap = new HashMap<>();
		for (Delay d : delays) {
			if (trainDelayMap.containsKey(d.getTrain())) {
				if (trainDelayMap.get(d.getTrain()).getDelta() < d.getDelta()) {
					trainDelayMap.put(d.getTrain(), d);
				}
			} else {
				trainDelayMap.put(d.getTrain(), d);
			}
		}
		delays.clear();
		delays.addAll(trainDelayMap.values());
	}

	/**
	 * handle a single delay on a single train
	 * 
	 * @param delay
	 * @return the list of delays needed to support connecting trains
	 */
	public Collection<Delay> handleSingleDelay(Delay delay) {

		Collection<Delay> resultingDelays = new HashSet<>();
		Collection<DelayNotice> notices = Collections.emptySet();
		// loop on all the trains to know which one the delay impacts
		for (Train train : this.trains) {
			// that's the one
			if (train.equals(delay.getTrain())) {
				try {
					// delay happens during the trip (affects arrival) or due to a delay required
					// for train connection.
					switch (delay.type) {
					case ARRIVAL:
						// from this arrival delay every schedule
						notices = train.delayAllStopsBeforeArrivalAt(delay.getStop(), delay.getDelta());
						break;
					case DEPARTURE:
						// from this departure, delay every schedule
						notices = train.delayAllStopsFromDeparture(delay.getStop(), delay.getDelta());
						break;
					}
				} catch (DelayException de) {
					// we reached a point where we can't delay the train anymore, no notice is
					// generated
				}
				break;
			}
		}

		// compute the real delays from the notices generated by the delay passed in
		// parameter

		for (DelayNotice notice : notices) {
			// for all co
			for (Train connectionTrain : getConnectionTrains(notice.getTrain(), notice.getStop(),
					notice.getInitialSchedule())) {
				// there may be a delay due to this reschedule
				Optional<Delay> optionalDelay = getRequiredDelay(connectionTrain, notice.getStop(),
						notice.getRealSchedule());
				if (optionalDelay.isPresent()) {
					// a new delay is required
					resultingDelays.add(optionalDelay.get());
				} else {
					// no delay is required, passengers can still make it on time.
				}
			}
		}
		return resultingDelays;

	}

	/**
	 * THis method computes the delays required by a new departure schedule for a
	 * train
	 * 
	 * @param train       the delayed train
	 * @param stop        the departure stop where the delay occured
	 * @param newSchedule the new schedule needded by the train to accomodate the
	 *                    delay
	 * @return a list of delay that should be applied to other trains.
	 */
	private Optional<Delay> getRequiredDelay(Train train, Stop stop, Long newSchedule) {

		// if passengers can't make it
		if (train.getDepartureSchedule(stop) < newSchedule) {
			// delay the departure of the connection train.
			Delay newDelay = new Delay(train, stop, newSchedule - train.getDepartureSchedule(stop) + 5,
					DelayType.DEPARTURE);
			return Optional.of(newDelay);
		}

		// no delay required
		return Optional.empty();

	}

}
