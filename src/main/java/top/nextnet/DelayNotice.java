package top.nextnet;

public class DelayNotice {
	@Override
	public String toString() {
		return "DelayNotice [train=" + train + ", stop=" + stop + ", initialSchedule=" + initialSchedule
				+ ", realSchedule=" + realSchedule + "]";
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}

	public Long getInitialSchedule() {
		return initialSchedule;
	}

	public void setInitialSchedule(Long initialSchedule) {
		this.initialSchedule = initialSchedule;
	}

	public Long getRealSchedule() {
		return realSchedule;
	}

	public void setRealSchedule(Long realSchedule) {
		this.realSchedule = realSchedule;
	}

	public DelayNotice(Train train, Stop stop, Long initialSchedule, Long realSchedule) {
		super();
		this.train = train;
		this.stop = stop;
		this.initialSchedule = initialSchedule;
		this.realSchedule = realSchedule;
	}

	Train train;
	Stop stop;
	Long initialSchedule;
	Long realSchedule;

}
