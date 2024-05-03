package es.um.redes.nanoFiles.logic;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Objects;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.client.NFConnector;
import es.um.redes.nanoFiles.tcp.server.NFServer;
import es.um.redes.nanoFiles.tcp.server.NFServerSimple;





public class NFControllerLogicP2P {
	/*
	 * TODO: Para bgserve, se necesita un atributo NFServer que actuará como
	 * servidor de ficheros en segundo plano de este peer
	 */

	private NFServer bgserver;
	private NFServerSimple serverSimple;
	private boolean serverStarted = false;


	public boolean isServerStarted() {
		return serverStarted;
	}

	public void setServerStarted(boolean serverStarted) {
		this.serverStarted = serverStarted;
	}

	protected NFControllerLogicP2P() {
	}

	/**
	 * Método para arrancar un servidor de ficheros en primer plano.
	 * 
	 */
	protected void foregroundServeFiles(NFControllerLogicDir controllerDir,NFControllerLogicP2P controllerPeer) {
		/*
		 * TODO: Crear objeto servidor NFServerSimple y ejecutarlo en primer plano.
		 */

		NFServerSimple serverSimple = null;
		int port = NFServerSimple.PORT; 


		while(!serverStarted) {
			try {
				serverSimple = new NFServerSimple(port,controllerDir);
				controllerDir.registerFileServer(port);
				serverSimple.run();
				serverStarted = true;
			} catch (Exception e) {
				port++;
				if(port > NFServerSimple.PORT + 64536) {
					System.err.println(" *Error: Unable to start the server");
					break;
				}
			}
		}



		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */





	}

	/**
	 * Método para ejecutar un servidor de ficheros en segundo plano. Debe arrancar
	 * el servidor en un nuevo hilo creado a tal efecto.
	 * 
	 * @return Verdadero si se ha arrancado en un nuevo hilo con el servidor de
	 *         ficheros, y está a la escucha en un puerto, falso en caso contrario.
	 * 
	 */
	protected boolean backgroundServeFiles(Integer port) {
		/*
		 * TODO: Comprobar que no existe ya un objeto NFServer previamente creado, en
		 * cuyo caso el servidor ya está en marcha. Si no lo está, crear objeto servidor
		 * NFServer y arrancarlo en segundo plano creando un nuevo hilo. Finalmente,
		 * comprobar que el servidor está escuchando en un puerto válido (>0) e imprimir
		 * mensaje informando sobre el puerto, y devolver verdadero.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */


		if(bgserver != null) {
			System.err.println(" *Server running");
			return false;
		}

		boolean result = false;
		boolean stop = false;
		int i = 0;
		while(!stop) {
			try {
				bgserver = new NFServer(port);

				stop = true;
			} catch (Exception e) {
				port++;
				i++;
				if(i == 1) {
					System.err.println(" *Puerto en uso. Buscando uno nuevo... ");
				}


				if(i > 200) {
					System.err.println(" *Unable to start the server");
					return false;
				}
			}

		}
		bgserver.startServer();



		if(bgserver.getPort() > 0) {
			System.out.println("El servidor está escuchando en el puerto " + bgserver.getPort());
			result  = true;
		}


		return result;
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param fserverAddr    La dirección del servidor al que se conectará
	 * @param targetFileHash El hash del fichero a descargar
	 * @param localFileName  El nombre con el que se guardará el fichero descargado
	 */
	protected boolean downloadFileFromSingleServer(InetSocketAddress fserverAddr, String targetFileHash, String localFileName) {


		boolean result = false;
		if (fserverAddr == null) {
			System.err.println("* Cannot start download - No server address provided");
			return false;
		}
		/*
		 * TODO: Crear un objeto NFConnector para establecer la conexión con el peer
		 * servidor de ficheros, y usarlo para descargar el fichero mediante su método
		 * "downloadFile". Se debe comprobar previamente si ya existe un fichero con el
		 * mismo nombre en esta máquina, en cuyo caso se informa y no se realiza la
		 * descarga. Si todo va bien, imprimir mensaje informando de que se ha
		 * completado la descarga.
		 */

		NFConnector nfConector = null;

		try {
			nfConector = new NFConnector(fserverAddr);
		} catch (IOException e) {
			e.printStackTrace();
			nfConector = null;
		}

		if(nfConector == null) {
			System.err.println(" *Error: Unable to connect to the server");
			return false;

		}


		File f =  new File(NanoFiles.sharedDirname + "/" + localFileName);
		if(f.exists()) {
			System.err.println( " *Error: The destination file already exist");
			return false;
		}

		try {

			result = nfConector.downloadFile(targetFileHash, f);
		} catch (IOException e) {
			System.err.println( " *Error: Unable to download file");
			result = false;
		}
		if(result) {
			System.out.println("The file has downloaded successfully");
		}else {
			System.err.println("The file has not been downloaded");
		}

		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */

		nfConector.disconnect();


		return result;
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param serverAddressList La lista de direcciones de los servidores a los que
	 *                          se conectará
	 * @param targetFileHash    Hash completo del fichero a descargar
	 * @param localFileName     Nombre con el que se guardará el fichero descargado
	 */
	public boolean downloadFileFromMultipleServers(LinkedList<InetSocketAddress> serverAddressList,
			String targetFileHash, String localFileName) {
		boolean downloaded = false;
		
		if (serverAddressList.isEmpty() || serverAddressList.stream().allMatch(Objects::isNull)) {
	        System.err.println("* Cannot start download - No valid server addresses provided");
	        return false;
	    }

		NFConnector nfConnector = null;
		try {
			for (InetSocketAddress serverAddr : serverAddressList) {
				nfConnector = new NFConnector(serverAddr);
				File file = new File(NanoFiles.sharedDirname + "/" + localFileName);
				if (file.exists()) {
					System.err.println("* Error: The destination file already exists");
					return false; 
				}

				downloaded = nfConnector.downloadFile(targetFileHash, file);
				if (downloaded) {
					System.out.println("The file has downloaded successfully");
					return true; 
				} else {
					System.err.println("The file has not been downloaded from " + serverAddr);
					continue; 
				}
			}
		} catch (IOException e) {
			System.err.println("* Error: Unable to connect to server " + nfConnector.getServerAddr() + ": " + e.getMessage());
		} finally {
			if (nfConnector != null) {
				nfConnector.disconnect();
			}
		}

		System.err.println("* Error: Unable to download the file from any server");
		return false;
	}



	/**
	 * Método para obtener el puerto de escucha de nuestro servidor de ficheros en
	 * segundo plano
	 * 
	 * @return El puerto en el que escucha el servidor, o 0 en caso de error.
	 */
	public int getServerPort() {

		/*
		 * TODO: Devolver el puerto de escucha de nuestro servidor de ficheros en
		 * segundo plano
		 */



		return bgserver.getPort();
	}

	/**
	 * Método para detener nuestro servidor de ficheros en segundo plano
	 * 
	 */
	public void stopBackgroundFileServer() {
		/*
		 * TODO: Enviar señal para detener nuestro servidor de ficheros en segundo plano
		 */
		if(bgserver== null) {
			System.err.println( " *Error: bgserver was not created");
		}else {
			bgserver.stopServer();
			bgserver = null;
		}



	}

	public NFServerSimple getServerSimple() {
		return serverSimple;
	}

	public void setServerSimple(NFServerSimple serverSimple) {
		this.serverSimple = serverSimple;
	}


}
