package testing;

import java.io.BufferedReader;

public class ChannelListener implements Runnable {

	private Process recPrcs;
	private BufferedReader reader;

	public ChannelListener(Process recPrcs, BufferedReader reader) {
		this.recPrcs=recPrcs;
		this.reader = reader;
	}

	/**
	 * Continuously runs and reads all incoming messages, passing messages to ME
	 */

	public void run() {
		String message;

		try {
			// As long as this reader is open, will take action the moment a
			// message arrives.
			while ((message = reader.readLine()) != null) {
				System.out.println("Node " + recPrcs.getID() + " received message: "
						+ message);

				// Tokenize our message to determine RicartAgrawala step

				String tokens[] = message.split(",");
				String messageType = tokens[0];

				if (messageType.equals("REQUEST")) {
					/*
					 * We are receiving request(j,k) where j is a seq# and k a
					 * node#. This call will decide to defer or ack with a
					 * reply.
					 */
					recPrcs.receiveRequest(Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]));
				} else if (messageType.equals("REPLY")) {
					/* Received a reply. We'll decrement our outstanding replies */
					recPrcs.receiveAcknowledgement();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
