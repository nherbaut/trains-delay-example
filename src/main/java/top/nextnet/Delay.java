package top.nextnet;

public class Delay {
	final Train train;
	final Stop stop;
	final Long delta;
	final DelayType type;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.type.name()).append(" of " ).append(train.getId() + "  at " + stop + " is deplayed of " + delta + " time units");
		return sb.toString();
	}

	public Delay(Train train, Stop stop, Long delta) {
		this(train, stop, delta, DelayType.ARRIVAL);

	}

	public Delay(Train train, Stop arrivalStop, Long delta, DelayType type) {
		this.train = train;
		this.stop = arrivalStop;
		this.delta = delta;
		this.type = type;
	}

	public Train getTrain() {
		return train;
	}

	public Stop getStop() {
		return stop;
	}

	public Long getDelta() {
		return delta;
	}

}
