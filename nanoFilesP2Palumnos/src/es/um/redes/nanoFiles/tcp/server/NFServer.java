package es.um.redes.nanoFiles.tcp.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link NFServerThread} cada vez que se conecte un cliente.
 */
public class NFServer implements Runnable {
	private static final int PORT = 10000;
	private ServerSocket serverSocket = null;
	private boolean stopServer = false;
	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;

	public NFServer() throws IOException {
		/*
		 * TODO: Crear un socket servidor y ligarlo a cualquier puerto disponible
		 */

		InetSocketAddress serverAdress = new InetSocketAddress(PORT);
		serverSocket = new ServerSocket();
		serverSocket.bind(serverAdress);
		

	}
	
	
	public NFServer(int port) throws IOException{
		InetSocketAddress serverAdress = new InetSocketAddress(port);
		serverSocket = new ServerSocket();
		serverSocket.bind(serverAdress);
	}

	/**
	 * Método que crea un socket servidor y ejecuta el hilo principal del servidor,
	 * esperando conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */	
		
		
		
		
	
		
		/*
		 * 
		 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */
		
		
		
		
		/*
		 * TODO: (Opcional) Crear un hilo nuevo de la clase NFServerThread, que llevará
		 * a cabo la comunicación con el cliente que se acaba de conectar, mientras este
		 * hilo vuelve a quedar a la escucha de conexiones de nuevos clientes (para
		 * soportar múltiples clientes). Si este hilo es el que se encarga de atender al
		 * cliente conectado, no podremos tener más de un cliente conectado a este
		 * servidor.
		 */

		if(serverSocket == null || !serverSocket.isBound()) {
			System.err.println(" *Error: Failed to run the file server");
			return;
		}
		
		Socket socket = null;
		while(!stopServer) {
			try {
				socket = serverSocket.accept();
				//System.out.println("\nNew client connected from "+ socket.getInetAddress().toString() + ":" + socket.getPort());
			} catch (IOException e) {
				socket = null;
				if(stopServer == false) {
					System.err.println( " *Error: Problem with local file");
				}
				
			}
			if (socket != null) {
				NFServerThread connectionThread = new NFServerThread(socket);
				connectionThread.start();
			}
		}
		
	}
	/**
	 * TODO: Añadir métodos a esta clase para: 1) Arrancar el servidor en un hilo
	 * nuevo que se ejecutará en segundo plano 2) Detener el servidor (stopserver)
	 * 3) Obtener el puerto de escucha del servidor etc.
	 */


	public void startServer() {
		new Thread(this).start();
	}
	
	public void stopServer() {
        stopServer = true; 
        try {
            serverSocket.close(); 
        } catch (IOException e) {
            System.err.println(" *Error while stopping the server: " + e.getMessage());
     
       }
    }
	public int getPort() {
		if(serverSocket != null) {
			return serverSocket.getLocalPort();
		}else {
			return 0;
		}
	}


	public static int getServersocketAcceptTimeoutMilisecs() {
		return SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS;
	}

}
