package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class Driver {
	
	private final String CONFIG_FILE="conf";
	
	private int prcsID;
	private int prcsCount;
	
	private String[] ipAddrs;
	private int[] ports;
	
	public static void main(String[] args) {
		new Driver().test();
	}
	
	public Driver() {
		ReadFromConfigurationFile();
	}
	
	private void ReadFromConfigurationFile() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
			prcsID=Integer.parseInt(in.readLine());
			prcsCount=Integer.parseInt(in.readLine());
			for(int i=0;i<prcsCount;i++){
				String[] tokens=in.readLine().split(" ");
				ipAddrs[i]=tokens[0];
				ports[i]=Integer.parseInt(tokens[1]);
			}
			in.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void test() {
		int numberOfWrites=0;
		int writeLimit = 5; // number of times to try requesting Critical Section: CS
		
		//Create Connections
		Process prcs= new ConnectionBuilder(prcsID,prcsCount).build(ipAddrs, ports);
		
		while(numberOfWrites < writeLimit)
		{
			try{
				System.out.println("Requesting critical section...");
				requestCS(prcs);
				numberOfWrites++;
				Random num = new Random();
				Thread.sleep(num.nextInt(500));
			}
			catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	public String getTimeStamp(){
		return new Timestamp(new Date().getTime()).toString();
	}
	
	/**
	* Interface method between Driver and RicartAgrawala
	 * @param prcs 
	*/
	public void requestCS(Process prcs)
	{

		prcs.requestCriticalSection();
		
		//After invocation returns, we can safely call CS
		writeCriticalSection(prcs.getID());
		
		//Once we are done with CS, release CS
		prcs.releaseCriticalSection();
	}



	private boolean writeCriticalSection(int id) {
		System.out.println("Node " + id + " entered critical section");
		try
		{
			BufferedWriter criticalSection = new BufferedWriter(new FileWriter("CriticalSectionOutput.txt", true));
			
			criticalSection.write(getTimeStamp()+": "+id + " started critical section access");
			criticalSection.newLine();
			Thread.sleep(100);
			criticalSection.write(getTimeStamp()+": "+id + " ended critical section access");
			criticalSection.newLine();
			criticalSection.newLine();
			criticalSection.flush(); //flush stream
			criticalSection.close(); //close write
		} 
		catch(Exception e){ System.out.println("Oh No! Something Has Gone Horribly Wrong");}
		return true;
		
	}

	
	
	

}
