package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	
	protected DataOutputStream dos = null;
	protected DataInputStream dis = null;




	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO Se crea el socket a partir de la dirección del servidor (IP, puerto). La
		 * creación exitosa del socket significa que la conexión TCP ha sido
		 * establecida.
		 */
		try {
			socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
		} catch (Exception e) {
			System.err.println(" *Error: Connection refused: connect");
		}
		try {
			System.out.println("Connected to... " + serverAddr.getAddress().toString() + ":" + serverAddr.getPort());
		} catch (NullPointerException e) {
			System.err.println(" *Error: serverAddr is null");
		}
		
		
		
		
		/*
		 * TODO Se crean los DataInputStream/DataOutputStream a partir de los streams de
		 * entrada/salida del socket creado. Se usarán para enviar (dos) y recibir (dis)
		 * datos del servidor.
		 */
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.err.println( " *Error: socket is null");
		}
	
		

	}

	/**
	 * Método para descargar un fichero a través del socket mediante el que estamos
	 * conectados con un peer servidor.
	 * 
	 * @param targetFileHashSubstr Subcadena del hash del fichero a descargar
	 * @param file                 El objeto File que referencia el nuevo fichero
	 *                             creado en el cual se escribirán los datos
	 *                             descargados del servidor
	 * @return Verdadero si la descarga se completa con éxito, falso en caso
	 *         contrario.
	 * @throws IOException Si se produce algún error al leer/escribir del socket.
	 */
	public boolean downloadFile(String targetFileHashSubstr, File file) throws IOException {
		
		boolean downloaded = false;
		/*
		 * TODO: Construir objetos PeerMessage que modelen mensajes con los valores
		 * adecuados en sus campos (atributos), según el protocolo diseñado, y enviarlos
		 * al servidor a través del "dos" del socket mediante el método
		 * writeMessageToOutputStream.
		 */
		PeerMessage messageToSend = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD, (byte) targetFileHashSubstr.length(), targetFileHashSubstr);
		messageToSend.writeMessageToOutputStream(dos);
		
		
		/*
		 * TODO: Recibir mensajes del servidor a través del "dis" del socket usando
		 * PeerMessage.readMessageFromInputStream, y actuar en función del tipo de
		 * mensaje recibido, extrayendo los valores necesarios de los atributos del
		 * objeto (valores de los campos del mensaje).
		 */
		
		PeerMessage infoArchivo = PeerMessage.readMessageFromInputStream(dis);
		
		byte opcode = infoArchivo.getOpcode();
		String fileHash = "";
		if (opcode == PeerMessageOps.OPCODE_DOWNLOAD) {
			fileHash = infoArchivo.getFileHash();
		}
		
		
		
		FileOutputStream archivo = null;
		archivo = new FileOutputStream(file);
		
		boolean recibiendoTrozos = true;
	
		while(recibiendoTrozos) {
			PeerMessage datosArchivo = PeerMessage.readMessageFromInputStream(dis);
			byte opcodeTrozo = datosArchivo.getOpcode();
			
			if(opcodeTrozo == PeerMessageOps.OPCODE_DOWNLOADING) {
				byte[] datos = datosArchivo.getDatos();
				archivo.write(datos);
				
			}
			
			else if(opcodeTrozo == PeerMessageOps.OPCODE_DOWNLOAD_OK) {
				recibiendoTrozos = false;
			}

			else if(opcode == PeerMessageOps.OPCODE_DOWNLOAD_FAIL) {
				System.err.println("* Cannot find the file you are looking for - segment failed in the proccess");
				recibiendoTrozos = false;
			}
		}
		archivo.close();
		
		
	
		/*
		 * TODO: Para escribir datos de un fichero recibidos en un mensaje, se puede
		 * crear un FileOutputStream a partir del parámetro "file" para escribir cada
		 * fragmento recibido (array de bytes) en el fichero mediante el método "write".
		 * Cerrar el FileOutputStream una vez se han escrito todos los fragmentos.
		 */
		
		
		
		
		/*
		 * NOTA: Hay que tener en cuenta que puede que la subcadena del hash pasada como
		 * parámetro no identifique unívocamente ningún fichero disponible en el
		 * servidor (porque no concuerde o porque haya más de un fichero coincidente con
		 * dicha subcadena)
		 */
		
		
		

		/*
		 * TODO: Finalmente, comprobar la integridad del fichero creado para comprobar
		 * que es idéntico al original, calculando el hash a partir de su contenido con
		 * FileDigest.computeFileChecksumString y comparándolo con el hash completo del
		 * fichero solicitado. Para ello, es necesario obtener del servidor el hash
		 * completo del fichero descargado, ya que quizás únicamente obtuvimos una
		 * subcadena del mismo como parámetro.
		 */
		
		
		String hashArchivoGenerado = FileDigest.computeFileChecksumString(file.getAbsolutePath());
		
		if(fileHash.equals(hashArchivoGenerado)) {
			downloaded = true;
		}

		return downloaded;
	}


	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println(" *Error: Unable to close the socket");
		}

	}
	
	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
