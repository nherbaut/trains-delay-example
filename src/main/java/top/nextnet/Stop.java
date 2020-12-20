package top.nextnet;

public class Stop {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stopName == null) ? 0 : stopName.hashCode());
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
		Stop other = (Stop) obj;
		if (stopName == null) {
			if (other.stopName != null)
				return false;
		} else if (!stopName.equals(other.stopName))
			return false;
		return true;
	}

	final String stopName;
	
	public Stop(String stopName) {
		this.stopName=stopName;
	}

	@Override
	public String toString() {
		return "Stop [stopName=" + stopName + "]";
	}
	
}
