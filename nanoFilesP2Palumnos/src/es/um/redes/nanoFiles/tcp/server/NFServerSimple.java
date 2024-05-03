package es.um.redes.nanoFiles.tcp.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import es.um.redes.nanoFiles.logic.NFControllerLogicDir;

public class NFServerSimple {

	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	public static final int PORT = 10000;
	private ServerSocket serverSocket = null;
	private boolean stopServer = false;

	private NFControllerLogicDir controllerDir;
	
	
	
	
	public boolean isStopServer() {
		return stopServer;
	}

	public void setStopServer(boolean stopServer) {
		this.stopServer = stopServer;
	}

	public NFServerSimple() throws IOException {
		/*
		 * TODO: Crear una direción de socket a partir del puerto especificado
		 */
		
		InetSocketAddress serverAdress = new InetSocketAddress(PORT);

		/*
		 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */
		serverSocket = new ServerSocket();
		serverSocket.bind(serverAdress);
		serverSocket.setReuseAddress(true);
	}
	
	public NFServerSimple(int port) throws IOException {
		/*
		 * TODO: Crear una direción de socket a partir del puerto especificado
		 */
		InetSocketAddress serverAdress = new InetSocketAddress(port);

		/*
		 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */
		serverSocket = new ServerSocket();
		serverSocket.bind(serverAdress);
		serverSocket.setReuseAddress(true);
	}
	
	

	public NFServerSimple(int port2, NFControllerLogicDir controllerDir) throws IOException {
		/*
		 * TODO: Crear una direción de socket a partir del puerto especificado
		 */
		InetSocketAddress serverAdress = new InetSocketAddress(port2);

		/*
		 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */
		serverSocket = new ServerSocket();
		serverSocket.bind(serverAdress);
		serverSocket.setReuseAddress(true);
		

		this.setControllerDir(controllerDir);
		
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * 
	 */
	public void run() {
	    /*
	     * TODO: Comprobar que el socket servidor está creado y ligado
	     */
	    if(serverSocket == null) {
	        System.err.println(" *Error: Failed to run the file server");
	        return;
	    } else {
	        System.out.println("NFServerSimple server running on " + serverSocket.getLocalSocketAddress());
	        System.out.println("Enter 'fgstop' to stop the server(Not implemented)))");
	    }
	    
	    /*
	     * TODO: Usar el socket servidor para esperar conexiones de otros peers que
	     * soliciten descargar ficheros
	     */
	    
	    Socket clientSocket = null;
	    stopServer = false;
	    //int intervalMillis = 1000; // 1 segundo
	    //Scanner reader = new Scanner(System.in);
	    
	    while(!stopServer) {
	        try {         
	        	/*if (System.in.available() > 0) {
	                String input = reader.nextLine();
	                if (input.equals(NFCommands.commandToString(NFCommands.COM_STOP_FGSERVE))) {
	                    controllerDir.unregisterFileServer();
	                    NFController.currentState2 = NFController.NOT_SERVER;
	                    stopServer = true;
	                    break;
	                }
	            }*/
	        	//serverSocket.setSoTimeout(intervalMillis);
	            clientSocket = serverSocket.accept();
	        } catch (java.net.SocketTimeoutException e) {
	            // Se alcanzó el tiempo de espera sin conexiones ni entrada del usuario, continuar esperando
	        } catch (IOException e) {
	            System.err.println(" *Error: Problem accepting connection");
	            e.printStackTrace();
	            clientSocket = null;
	        }
	        
	        /*
	         * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
	         * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
	         * hay que pasarle el socket devuelto por accept
	         */
	        if(clientSocket != null)
	        {
	            NFServerComm.serveFilesToClient(clientSocket);
	        }
	        
	    }
	    
	    System.out.println("NFServerSimple stopped. Returning to the nanoFiles shell...");
	}

	public static int getServersocketAcceptTimeoutMilisecs() {
		return SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS;
	}

	public NFControllerLogicDir getControllerDir() {
		return controllerDir;
	}

	public void setControllerDir(NFControllerLogicDir controllerDir) {
		this.controllerDir = controllerDir;
	}
	

	
	
}
