package es.um.redes.nanoFiles.udp.message;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	private static final String FIELDNAME_NICKNAME = "nickname";
	private static final String FIELDNAME_SESSIONKEY = "sessionkey";
	private static final String FIELDNAME_USERS = "usuarios";
	private static final String FIELDNAME_SERVERS = "servidores";
	private static final String FIELDNAME_ADDRESS = "direccion";
	private static final String FIELDNAME_PORT = "port";
	private static final String FIELDNAME_LOOKUP_NICK = "lookup_nick";
	private static final String FIELDNAME_PUBLISH_FILES = "files";
	private static final String FIELDNAME_SEARCH_USERS = "search_users";
	private static final String FIELDNAME_FILEHASH = "filehash";

	/*
	 * TODO: Definir de manera simbólica los nombres de todos los campos que pueden
	 * aparecer en los mensajes de este protocolo (formato campo:valor)
	 */

	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/*
	 * TODO: Crear un atributo correspondiente a cada uno de los campos de los
	 * diferentes mensajes de este protocolo.
	 */
	private String sessionKey;
	private String nickname;
	private String[] usuarios = new String[0];
	private String[] servidores = new String[0];
	private String address;
	private String puerto;
	private String archivos;
	private String fileHash;
	private String[] searchUsers = new String[0];
	
	
	
	public String[] getSearchUsers() {
		return searchUsers;
	}

	public void setSearchUsers(String[] searchUsers) {
		this.searchUsers = searchUsers;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
	

	
	public String getArchivos() {
		return archivos;
	}

	public void setArchivos(String archivos) {
		this.archivos = archivos;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		assert (operation.equals(DirMessageOps.OPERATION_REGISTER_PORT));
		this.address = address;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getPort() {
		assert (address != null);
		int idx = address.indexOf(":");
		String port = address.substring(idx + 1).trim();
		return port;

	}

	public String getIP() {
		assert (address != null);
		int idx = address.indexOf(":");
		String ip = address.substring(0, idx).trim();
		return ip;

	}

	public String[] getUsuarios() {
		return this.usuarios;
	}

	public String[] getServidores() {
		return this.servidores;
	}

	public static String toStringArray(String[] array) {
		String response = "";
		for (int i = 0; i < array.length; i++) {

			if (i != (array.length - 1)) {
				response += array[i] + ", ";
			} else {
				response += array[i];
			}

		}
		return response;
	}

	public static String[] fromStringToArray(String users) {
		return users.split(", ");
	}

	public void setUsuarios(String[] usuarios) {
		this.usuarios = usuarios;
	}

	public void setServidores(String[] servidores) {
		this.servidores = servidores;

	}

	public DirMessage(String op) {
		operation = op;
	}

	/*
	 * TODO: Crear diferentes constructores adecuados para construir mensajes de
	 * diferentes tipos con sus correspondientes argumentos (campos del mensaje)
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		assert (operation.equals(DirMessageOps.OPERATION_LOGIN));
		this.sessionKey = sessionKey;
	}

	public String getOperation() {
		return operation;
	}

	public void setNickname(String nick) {

		assert (operation.equals(DirMessageOps.OPERATION_LOGIN));

		nickname = nick;
	}

	public String getNickname() {

		return nickname;
	}

	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: Usar un bucle para parsear el mensaje línea a línea, extrayendo para
		 * cada línea el nombre del campo y el valor, usando el delimitador DELIMITER, y
		 * guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;

		for (String line : lines) {
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION: {
				assert (m == null);
				m = new DirMessage(value);
				break;
			}
			// BOLETIN 4
			case FIELDNAME_NICKNAME: {
				assert (m == null);
				m.setNickname(value);
				break;
			}
			case FIELDNAME_SESSIONKEY: {
				assert (m == null);
				m.setSessionKey(value);
				break;
			}
			case FIELDNAME_USERS: {
				assert (m == null);

				m.setUsuarios(fromStringToArray(value));
				break;
			}
			case FIELDNAME_ADDRESS: {
				assert (m == null);
				m.setAddress(value);
				break;
			}
			case FIELDNAME_PORT: {
				assert (m == null);
				m.setPuerto(value);
				break;
			}
			case FIELDNAME_LOOKUP_NICK: {
				assert (m == null);
				m.setNickname(value);
				break;
			}
			case FIELDNAME_SERVERS: {
				assert (m == null);
				m.setServidores(fromStringToArray(value));
				break;
			}
			case FIELDNAME_PUBLISH_FILES: {
				assert (m == null);
				if (value.contains(";")) {
					m.setArchivos(value);
				}

				break;
			}
			case FIELDNAME_SEARCH_USERS: {
				assert (m == null);
				m.setSearchUsers(fromStringToArray(value));
				
				break;
			}
			case FIELDNAME_FILEHASH: {
				assert (m == null);
				m.setFileHash(value);  
				
				break;
			}
			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			}
		}

		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * TODO: En función del tipo de mensaje, crear una cadena con el tipo y
		 * concatenar el resto de campos necesarios usando los valores de los atributos
		 * del objeto.
		 */
		// BOLETIN 4
		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN: {
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_LOGIN_OK: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_FAIL_LOGIN: {
			break;
		}
		case DirMessageOps.OPERATION_LOGOUT: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_LOGOUT_OK: {
			break;
		}
		case DirMessageOps.OPERATION_FAIL_LOGOUT: {
			break;
		}
		case DirMessageOps.OPERATION_USERLIST: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_USERLIST_OK: {
			sb.append(FIELDNAME_USERS + DELIMITER + toStringArray(usuarios) + END_LINE);
			if (servidores != null) {
				sb.append(FIELDNAME_SERVERS + DELIMITER + toStringArray(servidores) + END_LINE);
			}

			break;
		}
		case DirMessageOps.OPERATION_FAIL_USERLIST: {
			break;
		}
		case DirMessageOps.OPERATION_REGISTER_PORT: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			sb.append(FIELDNAME_PORT + DELIMITER + puerto + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_REGISTER_PORT_OK: {
			break;
		}
		case DirMessageOps.OPERATION_FAIL_REGISTER_PORT: {
			break;
		}
		case DirMessageOps.OPERATION_UNREGISTER_PORT: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			break;
		}
		
		case DirMessageOps.OPERATION_UNREGISTER_PORT_OK: {
			break;
		}
		
		case DirMessageOps.OPERATION_FAIL_UNREGISTER_PORT: {
			break;
		}
		case DirMessageOps.OPERATION_LOOKUP_USERNAME: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			sb.append(FIELDNAME_LOOKUP_NICK + DELIMITER + nickname + END_LINE);

			break;
		}
		case DirMessageOps.OPERATION_LOOKUP_USERNAME_OK: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			sb.append(FIELDNAME_ADDRESS + DELIMITER + address + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_FAIL_LOOKUP_USERNAME: {
			break;
		}
		case DirMessageOps.OPERATION_PUBLISH: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			sb.append(FIELDNAME_PUBLISH_FILES + DELIMITER + archivos + END_LINE);

			break;
		}
		case DirMessageOps.OPERATION_PUBLISH_OK: {
			break;
		}
		case DirMessageOps.OPERATION_FAIL_PUBLISH: {
			break;
		}
		
		case DirMessageOps.OPERATION_FILELIST: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_FILELIST_OK: {
			sb.append(FIELDNAME_PUBLISH_FILES + DELIMITER + archivos + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_FAIL_FILELIST: {
			break;
		}
		case DirMessageOps.OPERATION_SEARCH: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			sb.append(FIELDNAME_FILEHASH + DELIMITER + fileHash + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_SEARCH_OK: {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE);
			sb.append(FIELDNAME_SEARCH_USERS + DELIMITER + toStringArray(searchUsers) + END_LINE);
			break;
		}
		case DirMessageOps.OPERATION_SEARCH_FAIL: {
			break;
		}
		default:
			break;

		}
		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}



}

