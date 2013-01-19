package testing;

import java.io.*;

/**
 * An Implementation of Ricart and Agrawala's Algorithm.
 * 
 * The implementation is under some assumptions: 1. Manage only one critical
 * section 2. Use Logical Time Stamp 3. Thread safety hasn't been tested yet
 * 
 * @author XinghuangXu
 * 
 */
public class Process {

	public boolean isRequiredSection; // true := the Critical Section (CS) is
										// being requested or is already in the
										// critical region

	public int ackQue;

	public int globalTimeStamp; // Will be updated only at receiving requests

	public int localTimeStamp;

	public int prcsID;
	public int getID() {
		return prcsID;
	}

	public PrintWriter[] writers; // write out to other processes

	public int prcsCount; // Total number of processes need to access the
							// critical section (CS)

	public boolean[] reqQue;

	public Process(int prcsID, int prcsCount, PrintWriter[] writers) {
		isRequiredSection = false;

		// ackQue = channelCount;

		globalTimeStamp = 0;
		this.localTimeStamp = globalTimeStamp;

		this.prcsCount = prcsCount;
		this.writers=writers;

		// process id is also used for priority (low process # == higher
		// priority in RicartAgrawala scheme)
		// process numbers are [1,channelCount]; since we're starting at 1 check
		// for errors trying to access node '0'.
		this.prcsID = prcsID;

		reqQue = new boolean[prcsCount];
	}

	/**
	 * Asking for the critical session(CS)
	 * 
	 * @return true when all the other processes acknowledge the action
	 *         Invocation (begun in driver module with request CS)
	 */
	public boolean requestCriticalSection() {

		isRequiredSection = true;
		localTimeStamp = globalTimeStamp + 1;

		ackQue = 1;

		for (int i = 1; i <= prcsCount; i++) {
			if (i != prcsID) {
				sendRequest(localTimeStamp, prcsID, i);
			}
		}

		while (ackQue < prcsCount) {
			try {
				Thread.sleep(5);

			} catch (Exception e) {

			}
			/* wait until we have replies from all other processes */
		}

		// We return when ready to enter CS
		return true;

	}

	/**
	 * Finish using the critical section, release it.
	 */
	public synchronized void releaseCriticalSection() {
		isRequiredSection = false;

		// Clear the requesting queue
		for (int i = 0; i < prcsCount; i++) {
			if (reqQue[i]) {
				reqQue[i] = false;
				sendAcknowledgement(i + 1);
			}
		}
	}

	/**
	 * Receiving Request
	 * 
	 * @param reqTimeStamp
	 *            The incoming message's time stamp
	 * @param reqPrcsID
	 *            The incoming message's process id
	 * 
	 */
	public synchronized void receiveRequest(int reqTimeStamp, int reqPrcsID) {
		System.out.println("Received request from node " + reqPrcsID);
		boolean bDefer = false;

		globalTimeStamp = Math.max(globalTimeStamp, reqTimeStamp);

		// seqNum == timeStamp if they have the same timeStamp the lower node
		// number has the higher priority
		bDefer = isRequiredSection
				&& ((reqTimeStamp > localTimeStamp) || (reqTimeStamp == localTimeStamp && reqPrcsID > prcsID));
		if (bDefer) {
			System.out.println("Deferred sending message to " + reqPrcsID);
			reqQue[reqPrcsID - 1] = true;
		} else {
			System.out.println("Sent reply message to " + reqPrcsID);
			sendAcknowledgement(reqPrcsID);
		}

	}

	
	/** Receiving Replies */
	public void receiveAcknowledgement() {
		ackQue++;
	}

	
	public void sendAcknowledgement(int ackPrcsID) {
		System.out.println("Sending REPLY to node " + ackPrcsID);
		writers[ackPrcsID - 1].println("REPLY," + ackPrcsID);
	}

	
	public void sendRequest(int timeStamp, int prcsID, int ackPrcsID) {
		System.out.println("Sending REQUEST to node " + (((ackPrcsID))));
		writers[ackPrcsID - 1].println("REQUEST," + timeStamp + "," + prcsID);
	}

	

}
