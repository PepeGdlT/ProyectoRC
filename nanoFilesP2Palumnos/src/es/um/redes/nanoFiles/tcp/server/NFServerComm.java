package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {

	private final static int MAX_SHORT = 32767;

	public static void serveFilesToClient(Socket socket) {
		/*
		 * TODO: Crear dis/dos a partir del socket
		 */

		DataOutputStream dos = null;
		DataInputStream dis = null;

		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * TODO: Mientras el cliente esté conectado, leer mensajes de socket,
		 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
		 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
		 */

		PeerMessage messageFromClient = null;
		if (socket != null) {

			try {
				messageFromClient = PeerMessage.readMessageFromInputStream(dis);
				byte opcode = messageFromClient.getOpcode();
				String hashSubString = "";

				if (opcode == PeerMessageOps.OPCODE_DOWNLOAD) {
					hashSubString = messageFromClient.getFileHash();
				}

				// obtenemos todos los archivos de la base de datos
				FileInfo archivos[] = NanoFiles.db.getFiles();
				// luego obtenemos los archivos que presenten coincidencias con el trozo de hash
				FileInfo matchingFiles[] = FileInfo.lookupHashSubstring(archivos, hashSubString);

				PeerMessage infoToClient = null;

				// si hay mas de un archivo para el que coincida la subcadena, devolvemos un
				// error
				if (matchingFiles.length != 1) {
					infoToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FAIL);
					try {
						infoToClient.writeMessageToOutputStream(dos);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				else {
					// sacamos los campos necesarios del archivo
					
					String fileHash = matchingFiles[0].fileHash;
					long fileSize = matchingFiles[0].fileSize;

					infoToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD, (byte) fileHash.length(), fileHash);
					try {
						infoToClient.writeMessageToOutputStream(dos);
					} catch (IOException e) {
						e.printStackTrace();
					}




					// calculamos en cuantos trozos debemos enviar ese fichero
					int trozosAEnviar = 0;
					int ultimoTrozo = (int) (fileSize % MAX_SHORT);
					if (fileSize < MAX_SHORT) {
						trozosAEnviar = 1;

					} else {
						trozosAEnviar = (int) Math.ceil((double) fileSize / MAX_SHORT);

					}




					// creamos el fichero que buscamos
					File file = new File(NanoFiles.db.lookupFilePath(fileHash));
					// abrimos el flujo de lectura hacia el fichero solicitado
					RandomAccessFile archivo = null;
					try {
						archivo = new RandomAccessFile(file, "r");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					// inicializamos la posicion donde empezará a leer el archivo
					long pos = 0;



					// bucle de envio de trozos
					while (trozosAEnviar > 0) {
						if (trozosAEnviar == 1) {
							byte[] datosArchivo = new byte[ultimoTrozo];
							try {
								archivo.seek(pos);
								archivo.readFully(datosArchivo);
							} catch (IOException e) {
								e.printStackTrace();
							}

							PeerMessage fileToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOADING, (short) ultimoTrozo,
									datosArchivo);
							try {
								fileToClient.writeMessageToOutputStream(dos);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						else {
							byte[] datosArchivo = new byte[MAX_SHORT];

							try {
								archivo.seek(pos);
								archivo.readFully(datosArchivo);
								pos += MAX_SHORT;

							} catch (IOException e) {
								e.printStackTrace();
							}

							PeerMessage fileToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOADING, (short) MAX_SHORT,
									datosArchivo);
							try {
								fileToClient.writeMessageToOutputStream(dos);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						trozosAEnviar--;

					}


					//cerramos el flujo de datos
					try {
						archivo.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					//mandamos el mensaje de confirmacion
					PeerMessage confirmacion = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_OK);
					try {
						confirmacion.writeMessageToOutputStream(dos);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				System.err.println("Cannot receive the message from the client");

			}

		}

		/*
		 * TODO: Para servir un fichero, hay que localizarlo a partir de su hash (o
		 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
		 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
		 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
		 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
		 * devuelve la ruta al fichero a partir de su hash completo.
		 */

		// extraemos el codigo de operacion y los datos de ese mensaje

	}

}



