/*pa0 Simulator 
U59925901 WANG CHENGCHEN & U55653178 LEI YUE
*/ 

import java.io.*;
import java.util.*;
import java.lang.*;

class WriteTxt {
	public static void write(String[] args) throws IOException {
		File writename =new File ("output.txt");
		writename.createNewFile();
		FileWriter writer = new FileWriter(writename);
		writer.write("data");
		writer.flush();
		writer.close();
	}
}

public class simulator {
	long clock; // Define Simulation Clock
	static Random randomgenerator = new Random(System.currentTimeMillis());
        static double u= randomgenerator.nextDouble();
	static List<Source> sources = new ArrayList<Source>(); // Source List
	static double M;

	public static void main(String args[]) {
		//Simulation Interface
		//for the convenience of testing and just outputs one M value 
		System.out.println("Input a max value of \"M\" to simulate:0.4/0.6/0.8/1.0/1.2/1.4/1.6/1.8/2.0");
		Scanner readin=new Scanner(System.in);//get what users input from console
		String Mnumbertemp = readin.next();
		double Mnumber = Double.parseDouble(Mnumbertemp);
		System.out.println("Input packets(if use default, please input 100000):");
		String packetstemp=readin.next();
		int packets=Integer.parseInt(packetstemp);
		System.out.println("Input \"Start\" to start Simulation, Input\"Exit\" to quit");
		String tempread = readin.next();
		if (tempread.equals("Start")){
			for (M = 0.4; M <= Mnumber; M += 0.2) {
			init(); // create 11 sources
			// start simulation
			System.out.println("M=" + M + "\n");
			new FIFOSimulation().simulate(packets);
			System.out.println();
			new RRSimulation().simulate(packets);
			System.out.println();
			new DRRSimulation().simulate(packets, 65535*8); // set packets, set quantum=2^16 bytes
			System.out.println();
		}
		if (tempread.equals("Exit")){
			System.exit(0);
		}
		}
		
	}

	// generate a random number exponential distribution with the given mean value
	public static double expWithMean(double mean) {
		//ExponentialGenerator gen = new ExponentialGenerator(1 / mean, randomgenerator);
                double edis=(-mean)*Math.log(u);
		return edis;
	}

	// create 11 sources and set length
	public static void init() {
		sources.clear();
		for (int i = 0; i < 11; i++) {
			if (i < 4)
				sources.add(new Source(M, i, "telnet", 512));
			if (i >= 4 && i < 10)
				sources.add(new Source(M, i, "ftp", 8192));
			if (i >= 10)
				sources.add(new Source(5, i, "rogue", 5000));
		}
	}
}
/*FIFO Algorithm Simulation*/
class FIFOSimulation {
	List<Source> sources = new ArrayList<Source>(); // Source List
	List<Event> allEvents = new ArrayList<Event>(); // all the arrival events in the sources
	List<Event> events = new ArrayList<Event>(); // Event List
	List<Event> queue = new ArrayList<Event>(); // Queue
	long totalPackets = 0; // total packets been processed
        long ccpointer=0;
	double M;
	long clock; // simulation clock

	public FIFOSimulation() {
		// deep copy of all the sources
		for (Source s : simulator.sources) {
			sources.add(s.clone());
		}
	}

	public void simulate(long packets) {
		System.out.println("Start simulating FIFO...");
		init();
		Event firstEvent = schedule("Arrival");
		while (totalPackets < packets) {
			Event e = events.remove(0);
			routine(e);
		}
		for (Source s : sources) {
			s.calculate();
		}
	}

	// routine an event
	public void routine(Event e) {

		if (e.getType().equals("Arrival")) {
			queue.add(e); // add the arrival event to the queue
			schedule("Transmission"); // schedule a transmission event
		}
		if (e.getType().equals("Transmission")) {
			clock = e.getDequeueTime(); // record the dequeue time for the next arrival event
			Event e1 = queue.remove(0);
			schedule("Arrival");
			totalPackets++; // after routing a transmission event, a packet is completed processed
                        ccpointer++;
			// log the transmission events in its source for throughput calculation
			sources.get(e.getSourceID()).getLogs().add(e);
		}
	}

	// schedule an event, add either an arrival event or transmission event to the event list
	public Event schedule(String type) {
		Event e = null;
		if (type.equals("Arrival")) {
			e = allEvents.remove(0);

			// the next packet arrives later than the dequeue time of the previous packet
			// the queue is idle
			if (e.getArrivalTime() > clock) {
				e.setEnqueueTime(e.getArrivalTime());
			} else
				e.setEnqueueTime(clock); // the queue is busy

			events.add(e);
		}
		if (type.equals("Transmission")) {
			Event e1 = queue.get(0); // get current event in the queue

			// parameters for generating the corresponding transmission event
			int sourceID = e1.getSourceID();
			long length = e1.getLength();
			long arrivalTime = e1.getArrivalTime();
			long enqueTime = e1.getEnqueueTime();
			long dequeTime = enqueTime + length;

			// generate a transmission event and add it to the events list
			e = new Event("Transmission", sourceID, length, arrivalTime, dequeTime);
			e.setEnqueueTime(enqueTime);
			events.add(e);
		}
		return e;
	}

	// create 11 sources, each source is assigned parameters for
	// generating events
	public void init() {
		// merge all the arrival events, ordering by their arrival time
		for (int j = 0; j < 11; j++) {
			allEvents.addAll(sources.get(j).getEvents());
		}
		Collections.sort(allEvents);
	}

	// print all events in the events list
	public void printEvents() {
		for (Event e : events) {
			System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
					+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		}
		System.out.println();
	}

	// print all the elements in a given events list
	public void printList(List<Event> l) {
		for (Event e : l) {
			System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
					+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		}
		System.out.println();
	}

	// print a single event
	public void printEvent(Event e) {
		System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
				+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		System.out.println();
	}
}
/*Round Robin Algorithm Simulation==========Still has something wrong with events list*/
class RRSimulation {
	List<Source> sources = new ArrayList<Source>(); // Source List
	List<Event> events = new ArrayList<Event>(); // Event List
	List<Event> queue = new ArrayList<Event>(); // Queue
	long totalPackets = 0; // total packets been processed
        long ccpointer=0;
	double M;
	long clock=0; // simulation clock
	int p = 0; // source pointer

	public RRSimulation() {
		for (Source s : simulator.sources) {
			sources.add(s.clone());
		}
	}

	public void simulate(long packets) {
		System.out.println("Start simulating RR...");
		Event firstEvent = schedule("Arrival");

		while (totalPackets < packets) {
			Event e = events.remove(0);
			routine(e);

		}
		for (Source s : sources) {
			s.calculate();
		}
	}

	// routine an event
	public void routine(Event e) {

		if (e.getType().equals("Arrival")) {
			queue.add(e); // add the arrival event to the queue
			schedule("Transmission"); // schedule a transmission event
		}
		if (e.getType().equals("Transmission")) {
			clock = e.getDequeueTime(); // record the dequeue time for the next arrival event
			Event e1 = queue.remove(0);
			schedule("Arrival");
			totalPackets++; // after routing a transmission event, a packet is completed processed
                        ccpointer++;
			// log the transmission events in its source for throughput calculation
			sources.get(e.getSourceID()).getLogs().add(e);
		}
	}

	// schedule an event, add either an arrival event or transmission event to the event list
	public Event schedule(String type) {
		Event e = null;
		if (type.equals("Arrival")) {
			// serve in turn one packet for each source
			e = sources.get(p % 11).getEvents().remove(0);

			// the next packet arrives later than the dequeue time of the previous packet
			// the queue is idle
			if (e.getArrivalTime() > clock) {
				e.setEnqueueTime(e.getArrivalTime());
		//		System.out.println("AAAAfuck"+clock);
			} else{
		//		System.out.println("AAAAr"+clock);
				e.setEnqueueTime(clock); // the queue is busy
			}

			events.add(e);
			p++;
		}
		if (type.equals("Transmission")) {
			Event e1 = queue.get(0); // get current event in the queue
			// parameters for generating the corresponding transmission event
			int sourceID = e1.getSourceID();
			long length = e1.getLength();
			long arrivalTime = e1.getArrivalTime();
			long enqueTime = e1.getEnqueueTime();
			long dequeTime = enqueTime + length;

			// generate a transmission event and add it to the events list
			e = new Event("Transmission", sourceID, length, arrivalTime, dequeTime);
			e.setEnqueueTime(enqueTime);
			events.add(e);
		//	System.out.println(enqueTime);
		//	System.out.println(dequeTime);
		}
		return e;
	}

	// create 11 sources, each source is assigned parameters for
	// generating events
	public void init() {
		// initialization for simulation with next value M
		events.clear();
		queue.clear();
		totalPackets = 0;
        ccpointer=0;
		clock = 0;
		p = 0;

		// clear all the logs
		for (int i = 0; i < 11; i++) {
			sources.get(i).getLogs().clear();
		}
	}

	// print all events in the events list
	public void printEvents() {
		for (Event e : events) {
			System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
					+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		}
		System.out.println();
	}

	// print all the elements in a given events list
	public void printList(List<Event> l) {
		for (Event e : l) {
			System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
					+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		}
		System.out.println();
	}

	// print a single event
	public void printEvent(Event e) {
		System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
				+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		System.out.println();
	}
}
/*Deficit Round Robin Algorithm Simulation*/
class DRRSimulation {
	List<Source> sources = new ArrayList<Source>(); // Source List
	List<Event> events = new ArrayList<Event>(); // Event List
	List<Event> queue = new ArrayList<Event>(); // Queue
	long totalPackets = 0; // total packets been processed
        long ccpointer=0;
	double M;
	long clock; // simulation clock
	int p = 0; // source pointer
	long quantum = 0; // quantum for DRR algorithm

	public DRRSimulation() {

	}

	public void simulate(long packets, long q) {
		this.quantum = q;
		init();
		System.out.println("Start simulating DRR...");
		Event firstEvent = schedule("Arrival");
		while (totalPackets < packets) {
			Event e = events.remove(0);
			routine(e);

		}
		for (Source s : sources) {
			s.calculate();
		}
	}

	// routine an event
	public void routine(Event e) {

		if (e.getType().equals("Arrival")) {
			queue.add(e); // add the arrival event to the queue
			schedule("Transmission"); // schedule a transmission event
		}
		if (e.getType().equals("Transmission")) {
			clock = e.getDequeueTime(); // record the dequeue time for the next arrival event
			Event e1 = queue.remove(0);
			schedule("Arrival");
			totalPackets++; // after routing a transmission event, a packet is completed processed
                        ccpointer++;
			// log the transmission events in its source for throughput calculation
			sources.get(e.getSourceID()).getLogs().add(e);

		}
	}

	// schedule an event, add either an arrival event or transmission event to the event list
	public Event schedule(String type) {
		Event e = null;
		if (type.equals("Arrival")) {
			// take an packet from a source(which is indicated by a source pointer p)
			Source s = sources.get(p % 11);
			long q = s.getQuantum();
			long l = s.getEvents().get(0).getLength();
			if (l <= q) { // length of the packet is smaller than remaining quantum, process this packet
				e = s.getEvents().remove(0);

				// the next packet arrives later than the dequeue time of the previous packet
				// the queue is idle
				if (e.getArrivalTime() > clock) {
					e.setEnqueueTime(e.getArrivalTime());
				} else
					e.setEnqueueTime(clock); // the queue is busy

				events.add(e);
				s.setQuantum(q - l);
			} else { // length of the packet is larger than quantum

				// plus a constant quantum to the remaining quantum(given by the program)
				// it can be used for the next round to pick packets from this source
				s.setQuantum(s.getQuantum() + quantum);
				p++; // point to the next source
				schedule("Arrival"); // get a packet from the next source
			}
		}
		if (type.equals("Transmission")) {
			Event e1 = queue.get(0); // get current event in the queue

			// parameters for generating the corresponding transmission event
			int sourceID = e1.getSourceID();
			long length = e1.getLength();
			long arrivalTime = e1.getArrivalTime();
			long enqueTime = e1.getEnqueueTime();
			long dequeTime = enqueTime + length;

			// generate a transmission event and add it to the events list
			e = new Event("Transmission", sourceID, length, arrivalTime, dequeTime);
			e.setEnqueueTime(enqueTime);
			events.add(e);
		}
		return e;
	}

	// create 11 sources, each source is assigned parameters for
	// generating events
	public void init() {
		for (Source s : simulator.sources) {
			sources.add(s.clone());
		}
		for (Source s1 : sources) {
			s1.setQuantum(quantum);
		}
		// initialization for simulation with next value M
		events.clear();
		queue.clear();
		totalPackets = 0;
                ccpointer=0;
		clock = 0;
		p = 0;

		// clear all the logs
		for (int i = 0; i < 11; i++) {
			sources.get(i).getLogs().clear();
		}
	}

	// print all events in the events list
	public void printEvents() {
		for (Event e : events) {
			System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
					+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		}
		System.out.println();
	}

	// print all the elements in a given events list
	public void printList(List<Event> l) {
		for (Event e : l) {
			System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
					+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		}
		System.out.println();
	}

	// print a single event
	public void printEvent(Event e) {
		System.out.println(e.getType() + " " + e.getSourceID() + " " + e.getLength() + " "
				+ e.getArrivalTime() + " " + e.getEnqueueTime() + " " + e.getDequeueTime());
		System.out.println();
	}
}

// representing a source with a initial events generated
class Source implements Cloneable {
	private int id;
	private String type; // telnet, ftp, rogue
	private long averageSize; // average packet size
	private double M; // total offered load
	private double l; // mean interarrival time
	private long quantum; // quantum for DRR
	private List<Event> events = new ArrayList<Event>(); // initial events generated with exponential distribution
	private List<Event> logs = new ArrayList<Event>(); // logs of events that are processed
	private static Random randomgenerator = new Random(System.currentTimeMillis()); // a random number generator
        static double u= randomgenerator.nextDouble();
	public Source() {

	}

	public Source(double M, int id, String t, long s) {
		this.M = M;
		this.id = id;
		this.type = t;
		this.averageSize = s;
		l = 10 * averageSize / M;
		init();
	}
//Use marker annotation
	@Override
	public Source clone() { // deep copy of a source
		Source s = new Source();
		s.id = this.id;
		s.type = this.type;
		s.averageSize = this.averageSize;
		s.M = this.M;
		s.l = this.l;
		s.quantum = this.quantum;

		List<Event> eventCopy = new ArrayList<Event>(this.events.size());
		for (Event e : this.events) {
			eventCopy.add(e.clone());
		}
		s.events = eventCopy;

		List<Event> logCopy = new ArrayList<Event>(this.logs.size());
		for (Event e : this.logs) {
			logCopy.add(e.clone());
		}
		s.logs = logCopy;

		s.randomgenerator = this.randomgenerator;
		return s;
	}

	public int getID() {
		return id;
	}

	public String getType() {
		return type;
	}

	public long getAverageSize() {
		return averageSize;
	}

	public List<Event> getEvents() {
		return events;
	}

	public List<Event> getLogs() {
		return logs;
	}

	public double getInterTime() {
		return l;
	}

	public long getQuantum() {
		return quantum;
	}

	public void setQuantum(long q) {
		quantum = q;
	}

	// generate 100000 packets for each source
	private void init() {

		long t = 0; // arrival time
		for (int i = 1; i <= 100000; i++) {
			events.add(new Event("Arrival", id, (long) expWithMean(averageSize), t += expWithMean(l), 0));
		}
		Collections.sort(events);
	}

// generate a random number exponential distribution with the given mean value
	public static double expWithMean(double mean) {
                double edis=(-mean)*Math.log(u);
		return edis;
	}

	// calculate throughput and average latency of a source
	public void calculate() {
		if (logs.size() != 0) {
			long totalBits = 0; // total bits transmitted
			long totalLatency = 0; // total latency of all packets
			long firstArrivalTime = logs.get(0).getArrivalTime(); // arrival time of the first packet
			long lastTransTime = logs.get(logs.size() - 1).getDequeueTime(); // time of which last bit of its last packet is transmitted

			for (Event e : logs) {
				totalBits += e.getLength();
				long latency = e.getEnqueueTime() - e.getArrivalTime();
				totalLatency += latency;
			}
			double throughput = (double) totalBits / (lastTransTime - firstArrivalTime);
			double averageLatency = (double) totalLatency / logs.size();
			System.out.println("T: " + type + id + "="
					+ String.format("%.5f", throughput) + "bps");
			System.out.println("D: " + type + id
					+ "=" + String.format("%.5f", (M / 10 - throughput)) + "bps");
			System.out.println("L: " + type + id + "="
					+ String.format("%.1f", averageLatency) + "s");

		}
	}

}

// representing either an arrival event or a transmission event
class Event implements Comparable<Event> {
	private int sourceID;
	private String type; // Arrival, Transmission
	private long length; // packet length
	private long arrivalTime; // arrival time of a packet
	private long enqueueTime; // the enqueued time
	private long dequeueTime; // the dequeued time

	// private Source source;

	public Event() {

	}

	public Event(String type, int sourceID, long length, long arrivalTime, long dequeueTime) {
		this.type = type;
		this.sourceID = sourceID;
		this.length = length;
		this.arrivalTime = arrivalTime;
		this.dequeueTime = dequeueTime;
	}
	//Use marker annotation
	@Override
	protected Event clone() {
		Event eventCopy = new Event();

		eventCopy.sourceID = this.sourceID;
		eventCopy.type = this.type;
		eventCopy.length = this.length;
		eventCopy.arrivalTime = this.arrivalTime;
		eventCopy.enqueueTime = this.enqueueTime;
		eventCopy.dequeueTime = this.dequeueTime;

		return eventCopy;
	}

	// sort Event with ascendent arrival time instead of just add them at the end of the queue
	//Use marker annotation
	@Override
	public int compareTo(Event e) {
		int compareT = new Long(this.arrivalTime).compareTo(e.getArrivalTime());
		if (compareT == 0) {
			return (this.length == e.getLength() ? 0 : (this.length > e.getLength() ? 1 : -1));
		}
		return compareT;
	}
	public int getSourceID() {
		return sourceID;
	}
	public String getType() {
		return type;
	}
	public long getLength() {
		return length;
	}
	public long getArrivalTime() {
		return arrivalTime;
	}
	public long getEnqueueTime() {
		return enqueueTime;
	}
	public void setEnqueueTime(long t) {
		enqueueTime = t;
	}
	public long getDequeueTime() {
		return dequeueTime;
	}
	public void setDequeueTime(long t) {
		dequeueTime = t;
	}
}
