package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;
import es.um.redes.nanoFiles.util.FileUtils;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Valor inválido de la clave de sesión, antes de ser obtenida del directorio al
	 * loguearse
	 */
	public static final int INVALID_SESSION_KEY = -1;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;

	private int sessionKey = INVALID_SESSION_KEY;
	private boolean successfulResponseStatus;
	private String errorDescription;

	public DirectoryConnector(String address) throws IOException {
		/*
		 * TODO: Convertir el nombre de host 'address' a InetAddress y guardar la
		 * dirección de socket (address:DIRECTORY_PORT) del directorio en el atributo
		 * directoryAddress, para poder enviar datagramas a dicho destino.
		 */
		/*
		 * TODO: Crea el socket UDP en cualquier puerto para enviar datagramas al
		 * directorio
		 */
		this.directoryAddress = new InetSocketAddress(InetAddress.getByName(address), DIRECTORY_PORT);
		this.socket = new DatagramSocket();


	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData) throws IOException{
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response [] = null;
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
		/*
		 * TODO: Enviar datos en un datagrama al directorio y recibir una respuesta. El
		 * array devuelto debe contener únicamente los datos recibidos, *NO* el búfer de
		 * recepción al completo.
		 */
		/*
		 * TODO: Una vez el envío y recepción asumiendo un canal confiable (sin
		 * pérdidas) esté terminado y probado, debe implementarse un mecanismo de
		 * retransmisión usando temporizador, en caso de que no se reciba respuesta en
		 * el plazo de TIMEOUT. En caso de salte el timeout, se debe reintentar como
		 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse al leer/escribir en el socket deben
		 * ser capturadas y tratadas en este método. Si se produce una excepción de
		 * entrada/salida (error del que no es posible recuperarse), se debe informar y
		 * terminar el programa.
		 */
		/*
		 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
		 * SocketTimeoutException es más concreta que IOException.
		 */
		DatagramPacket packetToServer = new DatagramPacket(requestData, requestData.length,this.directoryAddress);
		int numTotal = 0;
		boolean arrival = false;
		while(numTotal < MAX_NUMBER_OF_ATTEMPTS ){
			if(arrival == false) {
				numTotal += 1;
				try {
				
					this.socket.send(packetToServer);
					socket.setSoTimeout(TIMEOUT);
	
					//el buffer de recepcion es responseData
					DatagramPacket packetFromServer = new DatagramPacket(responseData, responseData.length,this.directoryAddress); //cuidado lo último puede estar mal
					// Tratamos de recibir la respuesta
					socket.receive(packetFromServer);
	
	
					String responseSt = new String(responseData, 0, packetFromServer.getLength());
					response = responseSt.getBytes();
					arrival = true;
	
				}
				catch(SocketTimeoutException e) {
					//e.printStackTrace();
					System.err.println( numTotal + ". TIMEOUT: Server could not be reached");
				}
			}else {
				
				numTotal = MAX_NUMBER_OF_ATTEMPTS;
				
			}
		}
		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;
	}
	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 */
	public boolean testSendAndReceive() {
		/*
		 * TODO: Probar el correcto funcionamiento de sendAndReceiveDatagrams. Se debe
		 * enviar un datagrama con la cadena "login" y comprobar que la respuesta
		 * recibida es "loginok". En tal caso, devuelve verdadero, falso si la respuesta
		 * no contiene los datos esperados.
		 */
		boolean success = false;
		

		byte[] LOGIN_BYTES = {108, 111, 103, 105, 110};
		byte[] LOGINOK_BYTES = {108, 111, 103, 105, 110, 111, 107};
		
		try {
			byte [] responseEsperada = sendAndReceiveDatagrams(LOGIN_BYTES);

			if(Arrays.equals(responseEsperada, LOGINOK_BYTES)) {
				success = true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}

	public InetSocketAddress getDirectoryAddress() {
		return directoryAddress;
	}

	public int getSessionKey() {
		return sessionKey;
	}

	/**
	 * Método para "iniciar sesión" en el directorio, comprobar que está operativo y
	 * obtener la clave de sesión asociada a este usuario.
	 * 
	 * @param nickname El nickname del usuario a registrar
	 * @return La clave de sesión asignada al usuario que acaba de loguearse, o -1
	 *         en caso de error
	 */
	public boolean logIntoDirectory(String nickname) {
		assert (sessionKey == INVALID_SESSION_KEY);
		boolean success = false;
		// TODO: 1.Crear el mensaje a enviar (objeto DirMessage) con atributos adecuados
		// (operation, etc.) NOTA: Usar como operaciones las constantes definidas en la clase
		// DirMessageOps
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_LOGIN);
		DirMen.setNickname(nickname);
		// TODO: 2.Convertir el objeto DirMessage a enviar a un string (método toString)
		String DirMenSt = DirMen.toString();
		// TODO: 3.Crear un datagrama con los bytes en que se codifica la cadena
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);

		// TODO: 4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams).
		// TODO: 5.Convertir respuesta recibida en un objeto DirMessage (método
		// DirMessage.fromString)
		// TODO: 6.Extraer datos del objeto DirMessage y procesarlos (p.ej., sessionKey)
		// TODO: 7.Devolver éxito/fracaso de la operación
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			
			
			
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
		
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			
			String sessionKeyResponse = ServerResponse.getSessionKey();
			String operationResponse = ServerResponse.getOperation();
			
			
			//System.out.println("DEBUG: " + ServerResponse.getSessionKey() + ServerResponse.getOperation());
			

			try {
				if(operationResponse.equals(DirMessageOps.OPERATION_LOGIN_OK)  && Integer.parseInt(sessionKeyResponse) >= 0 && Integer.parseInt(sessionKeyResponse) <= 1000) {
					this.sessionKey = Integer.parseInt(sessionKeyResponse);
					System.out.println("Login sucessful with sessionKey -> " + this.sessionKey);
					success = true;
					return success;
				}
				else if (operationResponse.equals(DirMessageOps.OPERATION_FAIL_LOGIN)  && sessionKeyResponse.equals(String.valueOf(-1))){
					System.err.println("Respuesta recibida fuera de formato, o usuario ya registrado");
					return success;
				}
			} catch (Exception e) {
				System.err.println("* Error: Invalid hostname");
			}

			
			
		} catch (IOException e) {
			System.err.println("* Error: en el envío o la respuesta de paquetes");
		}
		return success;
	}


	/**
	 * Método para obtener la lista de "nicknames" registrados en el directorio.
	 * Opcionalmente, la respuesta puede indicar para cada nickname si dicho peer
	 * está sirviendo ficheros en este instante.
	 * 
	 * @return La lista de nombres de usuario registrados, o null si el directorio
	 *         no pudo satisfacer nuestra solicitud
	 */
	public String[] getUserList() {
	    String[] userlist = null;
	    String[] servidores = null;

	    assert (sessionKey == INVALID_SESSION_KEY);

	    // Crear el mensaje a enviar (objeto DirMessage) con los atributos adecuados
	    DirMessage dirMessage = new DirMessage(DirMessageOps.OPERATION_USERLIST);
	    dirMessage.setSessionKey(String.valueOf(this.sessionKey));

	    // Convertir el objeto DirMessage a una cadena
	    String dirMessageStr = dirMessage.toString();

	    // Crear un datagrama con los bytes en que se codifica la cadena
	    DatagramPacket packetToServer = new DatagramPacket(dirMessageStr.getBytes(), dirMessageStr.length(), this.directoryAddress);

	    try {
	        // Enviar datagrama y recibir una respuesta
	        byte[] packetFromServer = sendAndReceiveDatagrams(packetToServer.getData());

	        // Convertir la respuesta recibida en un objeto DirMessage
	        String serverResponseStr = new String(packetFromServer, 0, packetFromServer.length);
	        DirMessage serverResponse = DirMessage.fromString(serverResponseStr);

	        // Extraer datos del objeto DirMessage
	        String operationResponse = serverResponse.getOperation();
	        userlist = serverResponse.getUsuarios();
	        servidores = serverResponse.getServidores();
	        //String sessionKeyResponse = serverResponse.getSessionKey();

	        // Verificar si la operación fue exitosa y si la clave de sesión es válida
	        if (operationResponse.equals(DirMessageOps.OPERATION_USERLIST_OK)) {
	            
	        	
	        	String userListString = String.join(", ", userlist);

	            // Agregar la lista de servidores si existe
	            if (servidores != null && servidores.length > 0) {
	                userListString += "\nServidores: " + String.join(", ", servidores);
	            }

	            // Convertir la lista de usuarios y servidores en un array
	            userlist = new String[]{userListString};

	            return userlist;
	        } else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_USERLIST)  /*&& sessionKeyResponse.equals(String.valueOf(-1))*/){
	            System.err.println("Userlist fallido");
	            return null;
	        }
	    } catch (IOException e) {
	        System.err.println("Error en el envío o la respuesta de paquetes");
	    }

	    return userlist;
	}




	/**
	 * Método para "cerrar sesión" en el directorio
	 * 
	 * @return Verdadero si el directorio eliminó a este usuario exitosamente
	 */
	public boolean logoutFromDirectory() {
		assert (sessionKey == INVALID_SESSION_KEY);
		boolean success = false;

		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_LOGOUT);
		DirMen.setSessionKey(String.valueOf(sessionKey));
		//System.out.println("Debug(SessionKey): " + DirMen.getSessionKey());
		String DirMenSt = DirMen.toString();
		//System.out.println("Debug(DirMenSt): " + DirMenSt);
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);


		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			
			
			
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
		
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			
			String operationResponse = ServerResponse.getOperation();
			
			
			if(operationResponse.equals(DirMessageOps.OPERATION_LOGOUT_OK)){
				System.out.println("Logout sucessful");
				success = true;
				return success;
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_LOGOUT)  /*&& sessionKeyResponse.equals(String.valueOf(-1))*/) {
				System.err.println("Respuesta recibida fuera de formato, o usuario no registrado");
				return success;
			}
			
			
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}
		return success;

	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado a
	 * este peer.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @return Verdadero si el directorio acepta que este peer se convierta en
	 *         servidor.
	 */
	public boolean registerServerPort(int serverPort) {
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		boolean success = false;

		//BOLETIN 8 EJER 4 HACER
		
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_REGISTER_PORT);
		
		DirMen.setSessionKey(String.valueOf(this.sessionKey));
		DirMen.setPuerto(String.valueOf(serverPort));
		
		String DirMenSt = DirMen.toString();
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);
		
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			
			
			
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
		
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			String operationResponse = ServerResponse.getOperation();
			
			
			if(operationResponse.equals(DirMessageOps.OPERATION_REGISTER_PORT_OK)){
				System.out.println("*Peer now acting like a directory.*");
				success = true;
				return success;
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_REGISTER_PORT)) {
				System.err.println("*Cannot convert this peer to a directory*");
				return success;
			}
			
			
			
			
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}
		return success;

	}

	
	public boolean unregisterServerPort() {
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		boolean success = false;

		//BOLETIN 8 EJER 4 HACER
		
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_PORT);
		
		DirMen.setSessionKey(String.valueOf(this.sessionKey));
		
		
		String DirMenSt = DirMen.toString();
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);
		
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			
			
			
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
		
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			String operationResponse = ServerResponse.getOperation();
			
			
			if(operationResponse.equals(DirMessageOps.OPERATION_UNREGISTER_PORT_OK)){
				System.out.println(" *Succesfully unregistered");
				success = true;
				return success;
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_UNREGISTER_PORT)) {
				System.err.println(" *Cannot unregister*");
				return success;
			}
			
			
			
			
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}
		return success;

	}
	
	/**
	 * Método para obtener del directorio la dirección de socket (IP:puerto)
	 * asociada a un determinado nickname.
	 * 
	 * @param nick El nickname del servidor de ficheros por el que se pregunta
	 * @return La dirección de socket del servidor en caso de que haya algún
	 *         servidor dado de alta en el directorio con ese nick, o null en caso
	 *         contrario.
	 */
	public InetSocketAddress lookupServerAddrByUsername(String nick) {
		InetSocketAddress serverAddr = null;
		
		
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_LOOKUP_USERNAME);
		DirMen.setSessionKey(String.valueOf(this.sessionKey));
		DirMen.setNickname(nick);
		String DirMenSt = DirMen.toString();
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			String operationResponse = ServerResponse.getOperation();
			String address = ServerResponse.getAddress();
			if(address == null) {
				//System.err.println( "* Nobody is registered with the nick '" + nick+ "'");
				return null;
			}
			int idx = address.indexOf(":");
			String port = address.substring(idx+1).trim();
			String ip = address.substring(1,idx).trim();
			if(operationResponse.equals(DirMessageOps.OPERATION_LOOKUP_USERNAME_OK)){
				serverAddr = new InetSocketAddress(InetAddress.getByName(ip),Integer.valueOf(port));
				
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_LOOKUP_USERNAME)) {
				System.err.println(" *User not found: " + nick);
				
			}
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}
		
		
		
		
		return serverAddr;
	}

	/**
	 * Método para publicar ficheros que este peer servidor de ficheros están
	 * compartiendo.
	 * 
	 * @param files La lista de ficheros que este peer está sirviendo.
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */
	public boolean publishLocalFiles(FileInfo[] files) {
		

		boolean success = false;

		//BOLETIN 8 EJER 4 HACER
		
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_PUBLISH);
		
		DirMen.setSessionKey(String.valueOf(this.sessionKey));
		String archivosST = FileUtils.arrayToString(files);
		DirMen.setArchivos(archivosST);
		
		String DirMenSt = DirMen.toString();
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);
		
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			String operationResponse = ServerResponse.getOperation();
			//String sk = ServerResponse.getSessionKey();
			
			if(operationResponse.equals(DirMessageOps.OPERATION_PUBLISH_OK)){
				System.out.println(" *Files have been published succesfully");
				success = true;
				return success;
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_PUBLISH) /*&& sk.equals(String.valueOf(-1))*/) {
				System.err.println(" *Files are already published*");
				return success;
			}
			
			
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}
		return success;
	}

	/**
	 * Método para obtener la lista de ficheros que los peers servidores han
	 * publicado al directorio. Para cada fichero se debe obtener un objeto FileInfo
	 * con nombre, tamaño y hash. Opcionalmente, puede incluirse para cada fichero,
	 * su lista de peers servidores que lo están compartiendo.
	 * 
	 * @return Los ficheros publicados al directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */
	public FileInfo[] getFileList() {
		FileInfo[] filelist = null;
		

		//BOLETIN 8 EJER 4 HACER
		
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_FILELIST);
		
		DirMen.setSessionKey(String.valueOf(this.sessionKey));
		
		
		String DirMenSt = DirMen.toString();
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);
		
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			
			
			
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
		
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			String operationResponse = ServerResponse.getOperation();
			
			String archivosST = ServerResponse.getArchivos();
			if(operationResponse.equals(DirMessageOps.OPERATION_FILELIST_OK) && archivosST != null){
				
				filelist = FileUtils.fromStringToFiles(archivosST);
				
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_FILELIST)) {
				filelist = null;
			}
			else {
				filelist = null;
			}
			
			
			
			
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}


		
		return filelist;
	}

	/**
	 * Método para obtener la lista de nicknames de los peers servidores que tienen
	 * un fichero identificado por su hash. Opcionalmente, puede aceptar también
	 * buscar por una subcadena del hash, en vez de por el hash completo.
	 * 
	 * @return La lista de nicknames de los servidores que han publicado al
	 *         directorio el fichero indicado. Si no hay ningún servidor, devuelve
	 *         una lista vacía.
	 */
	public String[] getServerNicknamesSharingThisFile(String fileHash) {
		String[] nicklist = null;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		DirMessage DirMen = new DirMessage(DirMessageOps.OPERATION_SEARCH);
		
		DirMen.setSessionKey(String.valueOf(this.sessionKey));
		DirMen.setFileHash(fileHash);
		
		String DirMenSt = DirMen.toString();
		DatagramPacket packetToServer = new DatagramPacket(DirMenSt.getBytes(), DirMenSt.length(), this.directoryAddress);
		
		try {
			byte[] PacketFromServer  = sendAndReceiveDatagrams(packetToServer.getData());
			
			
			
			String ServerResponseSt = new String(PacketFromServer, 0, PacketFromServer.length);
		
			DirMessage ServerResponse = DirMessage.fromString(ServerResponseSt);
			
			String operationResponse = ServerResponse.getOperation();
			
			nicklist = ServerResponse.getSearchUsers();
			if(operationResponse.equals(DirMessageOps.OPERATION_SEARCH_OK) && nicklist != null){
				
				return nicklist;
				
			}
			else if(operationResponse.equals(DirMessageOps.OPERATION_FAIL_FILELIST)) {
				nicklist = null;
			}

			
			
			
		} catch (IOException e) {
			System.err.println("Error en el envío o la respuesta de paquetes");
		}


		return nicklist;
	}

	public boolean isSuccessfulResponseStatus() {
		return successfulResponseStatus;
	}

	public void setSuccessfulResponseStatus(boolean successfulResponseStatus) {
		this.successfulResponseStatus = successfulResponseStatus;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	



}
