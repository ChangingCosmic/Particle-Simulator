/**
 * Represents a collision between a particle and another particle, or a particle
 * and a wall.
 */
public class Event implements Comparable<Event> {
	double _timeOfEvent;
	double _timeEventCreated;
	Particle particle1;
	Particle particle2;
	Wall wall;

	/**
	 * @param timeOfEvent      the time when the collision will take place
	 * @param timeEventCreated the time when the event was first instantiated and
	 *                         added to the queue
	 */
	public Event(double timeOfEvent, double timeEventCreated) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
	}

	/**
	 * @param timeOfEvent      the time when the collision will take place
	 * @param timeEventCreated the time when the event was first instantiated and
	 *                         added to the queue
	 * @param p1               particle 1
	 * @param p2               particle 2
	 */
	public Event(double timeOfEvent, double timeEventCreated, Particle p1, Particle p2) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
		particle1 = p1;
		particle2 = p2;
	}

	/**
	 * particle-wall collision constructor
	 * 
	 * @param timeOfEvent
	 * @param timeEventCreated
	 * @param p1               particle 1
	 **/
	public Event(double timeOfEvent, double timeEventCreated, Particle p, Wall w) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
		particle1 = p;
		wall = w;
	}

	@Override
	/**
	 * Compares two Events based on their event times. Since you are implementing a
	 * maximum heap, this method assumes that the event with the smaller event time
	 * should receive higher priority.
	 */
	public int compareTo(Event e) {
		if (_timeOfEvent < e._timeOfEvent) {
			return +1;
		} else if (_timeOfEvent == e._timeOfEvent) {
			return 0;
		} else {
			return -1;
		}
	}
}
