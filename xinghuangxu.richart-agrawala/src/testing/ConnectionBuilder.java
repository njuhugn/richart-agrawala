package testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Use to establish peer-to-peer connections For simplicity process id start
 * from 0
 * 
 * @author XinghuangXu
 * 
 */
public class ConnectionBuilder {

	private PrintWriter[] writers; // Writers:=out

	private BufferedReader[] readers; // Readers:=in

	private int prcsCount;
	private int prcsID;

	private Process prcs; // For simplicity process id start from 0

	ServerSocket[] servSockets;
	Socket[] clnSockets;

	public ConnectionBuilder(int prcsID, int prcsCount) {
		// Initialize all the fields
		this.prcsID = prcsID;
		this.prcsCount = prcsCount;
		servSockets = new ServerSocket[prcsCount];
		clnSockets = new Socket[prcsCount];
		writers = new PrintWriter[prcsCount];
		readers = new BufferedReader[prcsCount];
		prcs = new Process(prcsID, prcsCount, writers);

	}

	public Process build(String[] ipAddrs, int[] ports) {
		// Build Connection
		try {
			for (int i = 0; i < prcsCount; i++) {

				if (i < prcsID) {
					clnSockets[i] = new Socket(ipAddrs[i], ports[i]);
				} else if (i > prcsID) {
					servSockets[i] = new ServerSocket(ports[i]);
				}
			}
			// Waiting to get the client Sockets
			for (int i = prcsID + 1; i < prcsCount; i++) {
				clnSockets[i] = servSockets[i].accept();
			}

			// Iterate through all the client sockets
			for (int i = 0; i < prcsCount; i++) {
				if (i != prcsID) {
					writers[i] = new PrintWriter(
							clnSockets[i].getOutputStream(), true);
					readers[i] = new BufferedReader(new InputStreamReader(
							clnSockets[i].getInputStream()));
					new Thread(new ChannelListener(prcs, readers[i])).start();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return prcs;
	}

}
