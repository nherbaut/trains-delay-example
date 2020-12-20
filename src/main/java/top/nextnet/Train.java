package top.nextnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Train {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrivals == null) ? 0 : arrivals.hashCode());
		result = prime * result + ((departures == null) ? 0 : departures.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Train other = (Train) obj;
		if (arrivals == null) {
			if (other.arrivals != null)
				return false;
		} else if (!arrivals.equals(other.arrivals))
			return false;
		if (departures == null) {
			if (other.departures != null)
				return false;
		} else if (!departures.equals(other.departures))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Train(String string) {
		this.id = string;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.id).append(":\n\t");
		Set<Stop> departures = this.departures.keySet();
		for (Stop stop : departures) {
			if (this.arrivals.containsKey(stop)) {
				sb.append("\tarrives ").append(stop).append(" at ").append(this.arrivals.get(stop)).append(" then ");
			}
			sb.append("leaves ").append(stop).append(" at ").append(this.departures.get(stop)).append("\n");
		}

		// final destination
		Set<Stop> arrivals = new HashSet<Stop>(this.arrivals.keySet());
		arrivals.removeAll(this.departures.keySet());
		Stop finalStop = arrivals.iterator().next();
		sb.append("\tarrives ").append(finalStop).append(" at ").append(this.arrivals.get(finalStop));
		return sb.toString();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long geArrivalSchedule(Stop stop) {
		return arrivals.get(stop);
	}

	public void setArrivals(Map<Stop, Long> arrivals) {
		this.arrivals = arrivals;
	}

	public Long getDepartureSchedule(Stop stop) {
		return departures.get(stop);
	}

	public void setDepartures(Map<Stop, Long> departures) {
		this.departures = departures;
	}

	String id;
	private Map<Stop, Long> arrivals = new LinkedHashMap<>();
	private Map<Stop, Long> departures = new LinkedHashMap<>();

	private Map<Stop, Long> initialArrivals = new LinkedHashMap<>();
	private Map<Stop, Long> initialDepartures = new LinkedHashMap<>();

	void addInitialArrival(Stop stop, Long l) {
		initialArrivals.put(stop, l);
		arrivals.put(stop, l);
	}

	void addInitialDeparture(Stop stop, Long l) {
		initialDepartures.put(stop, l);
		departures.put(stop, l);
	}

	void delayDeparture(Stop stop, Long newSchedule) throws DelayException {
		if (this.initialDepartures.get(stop) + 30 > newSchedule) {
			this.departures.put(stop, newSchedule);
		} else {
			throw new DelayException();
		}
	}

	void delayArrival(Stop stop, Long newSchedule) throws DelayException {
		if (this.initialArrivals.get(stop) + 30 > newSchedule) {
			this.arrivals.put(stop, newSchedule);
		} else {
			throw new DelayException();
		}
	}

	boolean arrivesAt(Stop stop) {
		return this.arrivals.containsKey(stop);
	}

	Long getArrivalSchedule(Stop stop) {
		return this.arrivals.get(stop);
	}

	boolean departFrom(Stop stop) {
		return this.departures.containsKey(stop);
	}

	public Long getInitialDepartureSchedule(Stop stop) {
		if (!initialDepartures.containsKey(stop)) {
			throw new RuntimeException("train " + this.id + " is not departing from " + stop.stopName);
		}
		return initialDepartures.get(stop);
	}

	public Long getArrivalInitialSchedule(Stop stop) {
		if (!initialArrivals.containsKey(stop)) {
			throw new RuntimeException("train " + this.id + " is not arriving to" + stop.stopName);
		}
		return initialArrivals.get(stop);
	}

	/**
	 * Reschedule the train according to the delay
	 * 
	 * @param stop  the departure stop from where the delay has to be implemented
	 * @param delta the time difference applied
	 * @return a collection of notice to inform the rest of the trains that there's
	 *         a reschedule.
	 * @throws DelayException in case delay is impossible
	 */
	public Collection<DelayNotice> delayAllStopsFromDeparture(Stop stop, long delta) throws DelayException {

		Collection<DelayNotice> notices = new HashSet<>();

		boolean delayAll = false;
		for (Segment pair : this.getSegments()) {
			if (pair.getDeparture().equals(stop)) {
				delayAll = true;

			}
			if (delayAll) {
				this.delayDeparture(pair.getDeparture(), this.getDepartureSchedule(pair.getDeparture()) + delta);
				this.delayArrival(pair.getArrival(), this.getArrivalSchedule(pair.getArrival()) + delta);
				notices.add(new DelayNotice(this, pair.getArrival(), this.getArrivalInitialSchedule(pair.getArrival()),
						this.getArrivalSchedule(pair.getArrival())));
			}
		}

		return notices;

	}

	/**
	 * a pair depature,arrival
	 * 
	 * @author nherbaut
	 *
	 */
	private class Segment {
		@Override
		public String toString() {
			return "Segment [departure=" + departure + ", arrival=" + arrival + "]";
		}

		public void setArrival(Stop arrival) {
			this.arrival = arrival;
		}

		public Stop getDeparture() {
			return departure;
		}

		public Stop getArrival() {
			return arrival;
		}

		private Stop departure;
		private Stop arrival;

		public Segment(Stop departure, Stop arrival) {
			this.departure = departure;
			this.arrival = arrival;
		}

	}

	/**
	 * get the sorted collection of segment for this train
	 * 
	 * @return
	 */
	private Collection<Segment> getSegments() {
		List<Segment> segments = new ArrayList<>();
		for (Stop stop : this.departures.keySet()) {
			segments.add(new Segment(stop, null));
		}
		List<Stop> arrivalStops = new ArrayList<Stop>(this.arrivals.keySet());
		for (int i = 0; i < this.arrivals.size(); i++) {
			segments.get(i).setArrival(arrivalStops.get(i));
		}

		return segments;
	}

	/**
	 * Something went wrong with the train en route, we delay the arrival and all
	 * the schedule after
	 * 
	 * @param stop  arrival from which everything will be delayed
	 * @param delta the time difference to be applied
	 * @return a collection of notice to inform the rest of the trains that there's
	 *         a reschedule.
	 * @throws DelayException
	 */
	public Collection<DelayNotice> delayAllStopsBeforeArrivalAt(Stop stop, long delta) throws DelayException {
		Collection<DelayNotice> notices = new HashSet<>();
		boolean delayAll = false;
		for (Segment pair : this.getSegments()) {
			if (delayAll) {
				this.delayDeparture(pair.getDeparture(), this.getDepartureSchedule(pair.getDeparture()) + delta);

				this.delayArrival(pair.getArrival(), this.getArrivalSchedule(pair.getArrival()) + delta);
				notices.add(new DelayNotice(this, pair.getArrival(), this.getArrivalInitialSchedule(pair.getArrival()),
						this.getArrivalSchedule(pair.getArrival())));
			}

			if (pair.getArrival().equals(stop)) {
				this.delayArrival(pair.getArrival(), this.getArrivalSchedule(pair.getArrival()) + delta);
				notices.add(new DelayNotice(this, pair.getArrival(), this.getArrivalInitialSchedule(pair.getArrival()),
						this.getArrivalSchedule(pair.getArrival())));
				delayAll = true;

			}

		}

		return notices;

	}

}
