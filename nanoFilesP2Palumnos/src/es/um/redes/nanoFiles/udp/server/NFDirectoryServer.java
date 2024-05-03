package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;
import es.um.redes.nanoFiles.util.FileUtils;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/**
	 * Estructura para guardar los nicks de usuarios registrados, y clave de sesión
	 * 
	 */
	private HashMap<String, Integer> nicks;
	/**
	 * Estructura para guardar las claves de sesión y sus nicks de usuario asociados
	 * 
	 */
	private HashMap<Integer, String> sessionKeys;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */

	private HashMap<String, InetSocketAddress> nicksAddress;
	private HashMap<String, Boolean> nickServidor;
	private HashMap<String,String> nicksArchivos;

	/**
	 * Generador de claves de sesión aleatorias (sessionKeys)
	 */
	Random random = new Random();
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín UDP) Inicializar el atributo socket: Crear un socket UDP
		 * ligado al puerto especificado por el argumento directoryPort en la máquina
		 * local,
		 */
		/*
		 * TODO: (Boletín UDP) Inicializar el resto de atributos de esta clase
		 * (estructuras de datos que mantiene el servidor: nicks, sessionKeys, etc.)
		 */

		// ---------------NUEVO----------------
		this.socket = new DatagramSocket(DIRECTORY_PORT);
		this.nicks = new HashMap<String, Integer>();
		this.sessionKeys = new HashMap<Integer, String>();
		this.nicksAddress = new HashMap<String, InetSocketAddress>();
		this.nickServidor = new HashMap<String, Boolean>();
		this.nicksArchivos = new HashMap<String, String>();
		// -------------------------------------

		if (NanoFiles.testMode) {
			if (socket == null || nicks == null || sessionKeys == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public void run() throws IOException {
		byte[] receptionBuffer = null;
		InetSocketAddress clientAddr = null;
		int dataLength = -1;
		/*
		 * TODO: (Boletín UDP) Crear un búfer para recibir datagramas y un datagrama
		 * asociado al búfer
		 */

		receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio

			// TODO: (Boletín UDP) Recibimos a través del socket un datagrama

			// TODO: (Boletín UDP) Establecemos dataLength con longitud del datagrama
			// recibido

			// TODO: (Boletín UDP) Establecemos 'clientAddr' con la dirección del cliente,
			// obtenida del
			// datagrama recibido

			// ------------NUEVO--------------------
			DatagramPacket packetFromClient = new DatagramPacket(receptionBuffer, receptionBuffer.length);

			socket.receive(packetFromClient);

			dataLength = packetFromClient.getLength();

			clientAddr = (InetSocketAddress) packetFromClient.getSocketAddress();

			// ---------------------------------------

			if (NanoFiles.testMode) {
				if (receptionBuffer == null || clientAddr == null || dataLength < 0) {
					System.err.println("NFDirectoryServer.run: code not yet fully functional.\n"
							+ "Check that all TODOs have been correctly addressed!");
					System.exit(-1);
				}
			}
			System.out.println("Directory received datagram from " + clientAddr + " of size " + dataLength + " bytes");

			// Analizamos la solicitud y la procesamos
			if (dataLength > 0) {
				String messageFromClient = null;
				/*
				 * TODO: (Boletín UDP) Construir una cadena a partir de los datos recibidos en
				 * el buffer de recepción
				 */
				messageFromClient = new String(receptionBuffer, 0, packetFromClient.getLength());

				DirMessage DirMenFromClient = DirMessage.fromString(messageFromClient);

				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println("Directory DISCARDED datagram from " + clientAddr);
					continue;
				}

				if (NanoFiles.testMode) { // En modo de prueba (mensajes en "crudo", boletín UDP)
					System.out.println("[testMode] Contents interpreted as " + dataLength + "-byte String: \""
							+ messageFromClient + "\"");
					/*
					 * TODO: (Boletín UDP) Comprobar que se ha recibido un datagrama con la cadena
					 * "login" y en ese caso enviar como respuesta un mensaje al cliente con la
					 * cadena "loginok". Si el mensaje recibido no es "login", se informa del error
					 * y no se envía ninguna respuesta.
					 */

					// ------------------NUEVO-----------------------

					if (!DirMenFromClient.getOperation().equals(DirMessageOps.OPERATION_LOGIN)) {
						System.err.println("Se esperaba un 'login' >> " + DirMenFromClient.toString());

					} else {
						String messageToClient = new String(DirMessageOps.OPERATION_LOGIN_OK);
						byte[] dataToClient = messageToClient.getBytes();
						DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length,
								clientAddr);
						socket.send(packetToClient);
					}

					// ---------------------------------------------

				} else { // Servidor funcionando en modo producción (mensajes bien formados)

					// Vemos si el mensaje debe ser ignorado por la probabilidad de descarte
					// double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}

					/*
					 * TODO: Construir String partir de los datos recibidos en el datagrama. A
					 * continuación, imprimir por pantalla dicha cadena a modo de depuración.
					 * Después, usar la cadena para construir un objeto DirMessage que contenga en
					 * sus atributos los valores del mensaje (fromString).
					 */

					System.out.println("MessageFromClient: \n" + DirMenFromClient.toString());

					DirMessage mensajeRecibir = DirMessage.fromString(messageFromClient);

					/*
					 * TODO: Llamar a buildResponseFromRequest para construir, a partir del objeto
					 * DirMessage con los valores del mensaje de petición recibido, un nuevo objeto
					 * DirMessage con el mensaje de respuesta a enviar. Los atributos del objeto
					 * DirMessage de respuesta deben haber sido establecidos con los valores
					 * adecuados para los diferentes campos del mensaje (operation, etc.)
					 */

					DirMessage mensajeEnviar = buildResponseFromRequest(mensajeRecibir, clientAddr);

					/*
					 * TODO: Convertir en string el objeto DirMessage con el mensaje de respuesta a
					 * enviar, extraer los bytes en que se codifica el string (getBytes), y
					 * finalmente enviarlos en un datagrama
					 */

					String mensajeENV = mensajeEnviar.toString();
					byte[] mensajeEnvB = mensajeENV.getBytes();
					DatagramPacket packetToServer = new DatagramPacket(mensajeEnvB, mensajeEnvB.length, clientAddr);

					try {
						this.socket.send(packetToServer);
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
					}

				}
			} else {
				System.err.println("Directory ignores EMPTY datagram from " + clientAddr);
			}

		}
	}

	private DirMessage buildResponseFromRequest(DirMessage msg, InetSocketAddress clientAddr) {
		/*
		 * TODO: Construir un DirMessage con la respuesta en función del tipo de mensaje
		 * recibido, leyendo/modificando según sea necesario los atributos de esta clase
		 * (el "estado" guardado en el directorio: nicks, sessionKeys, servers,
		 * files...)
		 */

		String operation = msg.getOperation();
		String username = msg.getNickname();
		String sessionKeyMSG = msg.getSessionKey();
		DirMessage response = null;

		// System.out.println("DEBUG(operationReceived): " + operation);
		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN: {

			if (!this.nicks.containsKey(username)) {
				int sessionKeyNewUser = random.nextInt(1000);
				this.nicks.put(username, sessionKeyNewUser);
				this.nickServidor.put(username, false);
				this.sessionKeys.put(sessionKeyNewUser, username);
				response = new DirMessage(DirMessageOps.OPERATION_LOGIN_OK);
				response.setNickname(username);
				response.setSessionKey(String.valueOf(sessionKeyNewUser));

				System.out.println("Respuesta generada correctamente, operacion: " + DirMessageOps.OPERATION_LOGIN
						+ ", con nickname: " + username + ", y sessionkey: " + sessionKeyNewUser);
			} else {
				// si el mapa nicks ya contiene la entrada le creamos el objeto DirMessage, que
				// tenga como operacion login_failed
				System.err.println("La respuesta no ha podido ser generada");
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_LOGIN);
				response.setNickname(username);
				response.setSessionKey(String.valueOf(-1));

			}

			break;
		}
		case DirMessageOps.OPERATION_LOGOUT: {
			if (this.sessionKeys.containsKey(Integer.parseInt(sessionKeyMSG))) {

				String user = this.sessionKeys.get(Integer.parseInt(sessionKeyMSG));
				System.out.println("El usuario: " + user + " ha cerrado sesión.");

				response = new DirMessage(DirMessageOps.OPERATION_LOGOUT_OK);

				this.sessionKeys.remove(Integer.parseInt(sessionKeyMSG));
				this.nicks.remove(user);

			} else {
				// si el mapa sessionKeys no tiene registrado esa sessionKey
				System.err.println("No hay nigún usuario con ese nombre");
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_LOGOUT);
			}
			break;
		}

		case DirMessageOps.OPERATION_USERLIST: {
			assert (!this.nicks.isEmpty());
			assert (this.sessionKeys.containsKey(Integer.parseInt(sessionKeyMSG)));

			response = new DirMessage(DirMessageOps.OPERATION_USERLIST_OK);
			Set<String> nombres = this.nicks.keySet();

			List<String> usuarios = new ArrayList<>();
			List<String> servidores = new ArrayList<>();

			for (String nombre : nombres) {
				if (this.nickServidor.containsKey(nombre) && this.nickServidor.get(nombre)) {
					servidores.add(nombre);
					usuarios.add(nombre);
				} else {
					usuarios.add(nombre);
				}
			}

			response.setUsuarios(usuarios.toArray(new String[usuarios.size()]));
			response.setServidores(servidores.toArray(new String[servidores.size()]));

			break;
		}

		case DirMessageOps.OPERATION_REGISTER_PORT: {
			String port = msg.getPuerto();
			String user = this.sessionKeys.get(Integer.valueOf(sessionKeyMSG));
			if (nicksAddress.containsKey(user)) {
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_REGISTER_PORT);

			} else {

				nicksAddress.put(user, new InetSocketAddress(clientAddr.getAddress(), Integer.valueOf(port)));
				nickServidor.put(user, true);
				response = new DirMessage(DirMessageOps.OPERATION_REGISTER_PORT_OK);
			}
			break;

		}
		case DirMessageOps.OPERATION_UNREGISTER_PORT: {

			String user = this.sessionKeys.get(Integer.parseInt(sessionKeyMSG));
			if (nicksAddress.containsKey(user)) {
				response = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_PORT_OK);
				nicksAddress.remove(user);
				nickServidor.put(user, false);
				nicksArchivos.remove(sessionKeyMSG);
				//response.setSessionKey(String.valueOf(sessionKeyMSG));

			} else {
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_UNREGISTER_PORT);
				//response.setSessionKey(String.valueOf(-1));
			}
			break;

		}

		case DirMessageOps.OPERATION_LOOKUP_USERNAME: {
			assert (this.nicksAddress != null);
			if (this.nicksAddress.containsKey(username)) {
				response = new DirMessage(DirMessageOps.OPERATION_LOOKUP_USERNAME_OK);
				//response.setSessionKey(sessionKeyMSG);
				response.setAddress(String.valueOf(this.nicksAddress.get(username)));
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_LOOKUP_USERNAME);
			}

			break;
		}
		case DirMessageOps.OPERATION_PUBLISH: {
			String archivosST = msg.getArchivos();
			if (!this.nicksArchivos.containsKey(sessionKeyMSG)) {
				response = new DirMessage(DirMessageOps.OPERATION_PUBLISH_OK);
				//response.setSessionKey(sessionKeyMSG);

				nicksArchivos.put(sessionKeyMSG, archivosST);
				System.out.println(nicksArchivos);
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_PUBLISH);
				//response.setSessionKey(String.valueOf(-1));
			}

			break;
		}
		case DirMessageOps.OPERATION_FILELIST: {
			if (this.nicksArchivos == null) {
				response = new DirMessage(DirMessageOps.OPERATION_FAIL_FILELIST);
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_FILELIST_OK);

				StringBuilder archivosST = new StringBuilder();
				int i = 0;
				for(String arc : this.nicksArchivos.values()) {

					archivosST.append(arc);
					if(i < this.nicksArchivos.size() -1) {
						archivosST.append("@"); 
					}
					i++;
				}
				System.out.println(archivosST.toString());
				response.setArchivos(archivosST.toString());

			}
			break;
		}		
		case DirMessageOps.OPERATION_SEARCH: {
			if (this.nicksArchivos == null) {
				response = new DirMessage(DirMessageOps.OPERATION_SEARCH_FAIL);
			}else {
				String fileHash =  msg.getFileHash();
				
				response = new DirMessage(DirMessageOps.OPERATION_SEARCH_OK);

				List<String> personas = new ArrayList<>();


				for(Map.Entry<String, String> entrada: this.nicksArchivos.entrySet()) {
					String key = entrada.getKey();
					String usuario = this.sessionKeys.get(Integer.parseInt(key));
					String archivos = entrada.getValue();

					FileInfo[] files = FileUtils.fromStringToFiles(archivos);

					if (FileInfo.lookupHashSubstring(files, fileHash).length > 0) {
						personas.add(usuario);
					}
				}
				response.setSessionKey(sessionKeyMSG);
				response.setSearchUsers(personas.toArray(new String[personas.size()]));
			}


			break;
		}

		default:
			System.out.println("Unexpected message operation: \"" + operation + "\"");
		}
		return response;
	}
}
