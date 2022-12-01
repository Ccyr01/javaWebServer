//Christian Cyr
//11/11/2022


// import java.util.*;
import java.io.*;
import java.net.*;


public class ProgramProject2 extends Thread {
	public ServerSocket serverSocket;
	public int port = 8080;

	public ProgramProject2() throws IOException {
// Creating a webserver with ServerSocket on port 8080
		this.serverSocket = new ServerSocket(port);
	}

	public void run() {
		try {
			// isBound returns binding state of the serverSocket
			while (serverSocket.isBound() && !serverSocket.isClosed()) {
				// Server waiting for connection until accept() detects one
				// client is the socket returned because it connected with server
				Socket client = serverSocket.accept();
				System.out.println("Got message: " + client.toString());
				ThreadManager threadHandler = new ThreadManager(client);
				threadHandler.start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// finally might be needed

	}

	public static void main(String[] args) throws Exception {
		try {
			ProgramProject2 serverListenerThread = new ProgramProject2();
			serverListenerThread.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class ThreadManager extends Thread {
	private Socket client;

	public ThreadManager(Socket client) {
		this.client = client;
	}


	@Override
	public void run() {
		InputStream is = null;
		try {
			// input stream reads from the client socket
			InputStreamReader input = new InputStreamReader(client.getInputStream());
			// input stream wrapped in buffered reader
			BufferedReader br = new BufferedReader(input);
			// read first request from client
			StringBuilder sb = new StringBuilder();
			String s = "";

			String simpleHTML = "<html><head><title>HTTP SERVER</title></head><body><h1>Here's some basic HTML in my h1</h1></body></html>";
			String redirectHTML = "<html><head><title>301 SERVER</title></head><body><h1>301</h1></body></html>";
			String notAllowedHTML = "<h1>not allowed</h1>";
			String forbiddenHTML = "<h1>Forbidden</h1>";
			String badRequestHTML = "<h1>Bad Request</h1>";

			
			String goodResponse = "HTTP/1.1 200 OK\r\n"
					 +simpleHTML.getBytes().length+"\r\n"
					+ "\r\n" + simpleHTML + "\r\n";
			String redirectResponse = ("HTTP/1.1 301 Moved Permanently\r\n"+"Location: https://google.com\r\n")+
					 +redirectHTML.getBytes().length+"\r\n"
					+ "\r\n" + redirectHTML + "\r\n";
			String notAllowedResponse = "HTTP/1.1 405 Method Not Allowed\r\n"
					 +notAllowedHTML.getBytes().length+"\r\n"
					+ "\r\n" + notAllowedHTML + "\r\n";
			String forbiddenResponse = "HTTP/1.1 403 Forbidden\r\n"
					 +forbiddenHTML.getBytes().length+"\r\n"
					+ "\r\n" + forbiddenHTML + "\r\n";
			String badRequestResponse = "HTTP/1.1 400 Bad Request\r\n"
					 +badRequestHTML.getBytes().length+"\r\n"
					+ "\r\n" + badRequestHTML + "\r\n";
			

//			while there is a request 
			String line;
			line = br.readLine();
			while (!line.isEmpty()) {
				// sb.append(line+"\r\n");
				s += line + "\r\n";
				line = br.readLine();
			}

// 			firstLine gets the first element after split
			String firstLine = s.split(" ")[0];
//			second gets the second element from requests
			String second = s.split(" ")[1];

			
			
//			HANDLE GET REQUESTS
			if (firstLine.equals("GET")) {
//            	System.out.println("Success");
				if(second.equals("/")||second.equals("/index.html")) {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(goodResponse.getBytes());
					clientOutput.flush();
				}
				else if(second.equals("/google")) {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(redirectResponse.getBytes());
					clientOutput.flush();
				}
				else if(second.equals("/multiply")) {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(notAllowedResponse.getBytes());
					clientOutput.flush();
				}
				else if(second.equals("/favicon.ico")) {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(("HTTP/1.1 404 Not Found").getBytes());
					clientOutput.write(("Content Type: text/html\r\n").getBytes());
					clientOutput.write(("\r\n").getBytes());
					clientOutput.write(("<h1>Not Found</h1>").getBytes());
					clientOutput.write(("\r\n\r\n").getBytes());
					clientOutput.flush();
					
				}
				else {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(notAllowedResponse.getBytes());
					clientOutput.flush();
					
				}

			}
			
//			HANDLE POST REQUESTS

			else if(firstLine.equals("POST")) {
				if(second.equals("/multiply")) {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(badRequestResponse.getBytes());
					clientOutput.flush();
				}
				else {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(notAllowedResponse.getBytes());
					clientOutput.flush();
	//				System.out.println(s);
				}
				
			}
			
//			HANDLE DELETE REQUESTS

			else if(firstLine.equals("DELETE")) {
				if(second.equals("/google")) {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(notAllowedResponse.getBytes());
					clientOutput.flush();
				}
				else {
					OutputStream clientOutput = client.getOutputStream();
					clientOutput.write(forbiddenResponse.getBytes());
					clientOutput.flush();
				}
				
			}
			
//			HANDLE OPTIONS REQUESTS

			else if(firstLine.equals("OPTIONS")) {
				OutputStream clientOutput = client.getOutputStream();
				clientOutput.write(notAllowedResponse.getBytes());
				clientOutput.flush();
			}
			
//			HANDLE ALL OTHER REQUESTS
			
			else {
				OutputStream clientOutput = client.getOutputStream();
				clientOutput.write(notAllowedResponse.getBytes());
				clientOutput.flush();
				
			}


			client.close();
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}