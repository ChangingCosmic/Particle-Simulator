import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;
import java.util.List;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private List<Particle> _particles;
	private double _duration;
	private int _width;

	private List<Wall> wallList = new ArrayList<>();

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator(String filename) throws IOException {
		_events = new HeapImpl<>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		Wall leftWall = new Wall("leftWall");
		wallList.add(leftWall);

		Wall rightWall = new Wall("rightWall");
		wallList.add(rightWall);

		Wall topWall = new Wall("topWall");
		wallList.add(topWall);

		Wall bottomWall = new Wall("bottomWall");
		wallList.add(bottomWall);

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations DO NOT
	 * MODIFY THIS METHOD
	 */
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent(double timeOfEvent) {
			super(timeOfEvent, 0);
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their
	 * current velocities.
	 */
	private void updateAllParticles(double delta, int width) {
		for (Particle p : _particles) {
			p.update(delta, _width);
		}
	}

	/**
	 * enques new events for particleOne
	 * 
	 * @param particleOne   main particle we are checking for events
	 * @param particleOther particle we are making sure it is not the same as the
	 *                      particle in the loop
	 * @param lastTime      current time of the function/last time an event happened
	 */
	public void enqueNewEvents(Particle particleOne, Particle particleOther, double lastTime) {
		for (int i = 0; i < _particles.size(); i++) {
			Particle p = _particles.get(i);

			// makes sure that particleOne is not equal to p and particleOne has a collision
			// with p
			// and particleOther is not equal to p
			if (!particleOne.equals(p) && (particleOne.getCollisionTime(p) != Double.POSITIVE_INFINITY)
					&& !particleOther.equals(p)) {

				Event e = new Event(particleOne.getCollisionTime(p), lastTime, particleOne, _particles.get(i));

				_events.add(e);
			}
		}

		// checks particleOne to see if there is a wall collision
		for (int i = 0; i < wallList.size(); i++) {
			if (particleOne.getWallCollisionTime(_width, 0) != Double.POSITIVE_INFINITY) {
				Event e = new Event(particleOne.getWallCollisionTime(_width, lastTime), lastTime, particleOne,
						wallList.get(i));

				_events.add(e);
			}
		}
	}

	/**
	 * Executes the actual simulation.
	 */
	private void simulate(boolean show) {
		double lastTime = 0; // last time an event happened

		// Create initial events, i.e., all the possible collisions between all the
		// particles and each other,
		// and all the particles and the walls.
		for (Particle p : _particles) {

			// checks against other particles
			for (int i = 0; i < _particles.size(); i++) {
				// makes sure main particle is not equal to loop _particles.get(i) and
				// main particle has a collision with _particles.get(i) and
				// main particle is inside the border
				if (!p.equals(_particles.get(i)) && (p.getCollisionTime(_particles.get(i)) != Double.POSITIVE_INFINITY)
						&& p.getX() < _width && p.getX() > 0 && p.getY() < _width && p.getY() > 0) {

					Event e = new Event(p.getCollisionTime(_particles.get(i)), lastTime, p, _particles.get(i));

					_events.add(e);
				}
			}

			// checks the particle p against the wall list
			for (int i = 0; i < wallList.size(); i++) {
				if (p.getWallCollisionTime(_width, 0) != Double.POSITIVE_INFINITY) {
					Event e = new Event(p.getWallCollisionTime(_width, lastTime), lastTime, p, wallList.get(i));

					_events.add(e);
				}
			}
		}

		_events.add(new TerminationEvent(_duration));
		while (_events.size() > 1) {
			Event event = _events.removeFirst();

			// how much time has passed between events
			double delta = event._timeOfEvent - lastTime;

			// if delta passes duration OR the event's time of event is bigger than the
			// duration, then stop loop
			if (delta > _duration || event._timeOfEvent > _duration) {
				System.out.println("delta > duration");
				break;
			}

			// if the event is a instance of the termination event, then stop loop
			if (event instanceof TerminationEvent) {
				updateAllParticles(delta, _width);
				break;
			}

			// Check if event still valid; if not, then skip this event
			if (lastTime > event._timeOfEvent) {
				continue;
			}

			try {
				// System.out.println("sleep!! sleep!!!! sleep for " + delta);
				Thread.sleep((long) delta);
			} catch (InterruptedException ie) {
				// System.out.println("interruption exception");
				System.out.println("delta " + delta);
			}

			// Update positions of all particles
			updateAllParticles(delta, _width);

			// Update the velocity of the particle(s) involved in the collision
			// then enques more events for the particle(s) involved

			if (event.particle2 instanceof Particle) {
				// update both particles' velocities
				event.particle1.updateAfterCollision(lastTime, event.particle2);

				// makes new events for both particles
				this.enqueNewEvents(event.particle2, event.particle1, lastTime);
			} else {
				// update only particle1's velocity
				event.particle1.updateAfterWallCollision(lastTime, _width, event.wall);

				// makes new events for only particle 1
				this.enqueNewEvents(event.particle1, event.particle2, lastTime);
			}

			// Update the time of our simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}

		// Print out the final state of the simulation
		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
		//System.out.println("last time: " + lastTime);
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java ParticalSimulator <filename>");
			System.exit(1);
		}

		ParticleSimulator simulator;

		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}
}
