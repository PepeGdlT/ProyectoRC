package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PeerMessage {




	private byte opcode;

	/*
	 * TODO: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */

	private byte hashLength = -1;
	private short logintudDatos = -1;
	private byte[] datos;
	private String fileHash = null;
	private long param_variable_1 = -1;
	private long param_variable_2 = -1;
	private long filesize = -1;


	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	//formato control
	public PeerMessage(byte op) {
		opcode = op;
	}

	//formato operaciones
	public PeerMessage(byte opcode, long param_1, long param_2) {
		this.opcode = opcode;
		this.setParam_variable_1(param_1);
		this.setParam_variable_2(param_2);

	}

	//formato TIPO-LONGITUD-VALOR
	public PeerMessage(byte _opcode, byte _hashLength, String _fileHash) {

		this.opcode = _opcode;
		this.hashLength = _hashLength;
		this.fileHash = new String(_fileHash);
	}
	//ENVIO DE TROZOS DE FICHERO
	public PeerMessage(byte _opcode, short _longdatos, byte[] datos) {

		this.opcode = _opcode;
		this.logintudDatos = _longdatos;
		this.datos = datos;
	}
	public PeerMessage(byte _opcode, long filesize, String _fileHash) {

		this.opcode = _opcode;
		this.setFilesize(filesize);
		this.fileHash = new String(_fileHash);
	}
	

	/*
	 * TODO: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	public byte getOpcode() {
		return opcode;
	}

	
	public short getLogintudDatos() {
		return logintudDatos;
	}

	public void setLogintudDatos(short logintudDatos) {
		this.logintudDatos = logintudDatos;
	}

	public byte[] getDatos() {
		return datos;
	}

	public void setDatos(byte[] datos) {
		this.datos = datos;
	}

	public byte getHashLength() {
		return hashLength;
	}

	public void setHashLength(byte hashLength) {
		assert( opcode == PeerMessageOps.OPCODE_DOWNLOAD);
		this.hashLength = hashLength;
	}

	public String getFileHash() {
		if(fileHash==null) {
			return null;
		}
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		
		this.fileHash = fileHash;
	}

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();

		switch (opcode) {
		case PeerMessageOps.OPCODE_DOWNLOAD: {
			byte hashLength = dis.readByte();
			byte[] hashValue = new byte[hashLength];
			dis.readFully(hashValue);
			message = new PeerMessage(opcode, hashLength, new String(hashValue));
			break;
		}

		case PeerMessageOps.OPCODE_DOWNLOADING: {
			short longitud = dis.readShort();
			byte[] datosB = new byte[longitud];
			dis.readFully(datosB);
			message = new PeerMessage(opcode, longitud, datosB);
			break;
		}
		case PeerMessageOps.OPCODE_DOWNLOAD_OK:{
			message = new PeerMessage(opcode);
			break;
		}
		case PeerMessageOps.OPCODE_DOWNLOAD_FAIL: {
			message = new PeerMessage(opcode);
			break;
		}


		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO: Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */


		//case para cada PeerMessageOps
		dos.write(opcode);
		switch (opcode) {
		case PeerMessageOps.OPCODE_DOWNLOAD: {
			assert(hashLength > 0 && fileHash.length() == hashLength);
			dos.writeByte(hashLength);
			byte[] hashValue = fileHash.getBytes();
			dos.write(hashValue);

			break;
		}

		case PeerMessageOps.OPCODE_DOWNLOADING: {
			dos.writeShort(logintudDatos);
			byte[] datosB = datos;
			dos.write(datosB);
			break;
		}
		case PeerMessageOps.OPCODE_DOWNLOAD_OK: {	
			break;
		}

		case PeerMessageOps.OPCODE_DOWNLOAD_FAIL: {
			break;
		}




		default:
			throw new IllegalArgumentException("Unexpected value: " + opcode);
		}

	}

	@Override
	public String toString() {
		String devolver = null;
		if(hashLength != -1) {
			devolver =  "PeerMessage [opcode=" + opcode + ", hashLength=" + hashLength + "fileHash=" + fileHash + "]";
		}
		else if(logintudDatos != -1) {
			devolver =  "PeerMessage [opcode=" + opcode + ", logintudDatos=" + logintudDatos + ", datos=" + datos + "]";
		}
		return devolver;
		
	}

	public long getParam_variable_1() {
		return param_variable_1;
	}

	public void setParam_variable_1(long param_variable_1) {
		this.param_variable_1 = param_variable_1;
	}

	public long getParam_variable_2() {
		return param_variable_2;
	}

	public void setParam_variable_2(long param_variable_2) {
		this.param_variable_2 = param_variable_2;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}








}
