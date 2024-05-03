package es.um.redes.nanoFiles.tcp.message;

import java.util.Map;
import java.util.TreeMap;

public class PeerMessageOps {

	public static final byte OPCODE_INVALID_CODE = 0;
	
	
	//boletin 6
	public static final byte OPCODE_DOWNLOAD = 1; //para download, FORMATO TIPO-LONG-VALOR, cmapo LONG como byte
	public static final byte OPCODE_DOWNLOADING = 2; // para download_ok FORMATO TIPO-LONG-VALOR, campo LONG un short
	public static final byte OPCODE_DOWNLOAD_OK = 3; 
	public static final byte OPCODE_DOWNLOAD_FAIL = 4; //para download_fail, FORMATO CONTROL


	/**
	 * TODO: Definir constantes con nuevos opcodes de mensajes
	 * definidos, añadirlos al array "valid_opcodes" y añadir su
	 * representación textual a "valid_operations_str" en el mismo orden
	 */
	private static final Byte[] _valid_opcodes = {
			OPCODE_INVALID_CODE, OPCODE_DOWNLOAD, OPCODE_DOWNLOADING, OPCODE_DOWNLOAD_OK, OPCODE_DOWNLOAD_FAIL, 
			
			};
	private static final String[] _valid_operations_str = {
			"INVALID_OPCODE", "DOWNLOAD", "DOWNLOADING", "DOWNLOAND_ACCEPTED", "DOWNLOAD_FAIL", 

			};

	private static Map<String, Byte> _operation_to_opcode;
	private static Map<Byte, String> _opcode_to_operation;

	static {
		_operation_to_opcode = new TreeMap<>();
		_opcode_to_operation = new TreeMap<>();
		for (int i = 0; i < _valid_operations_str.length; ++i) {
			_operation_to_opcode.put(_valid_operations_str[i].toLowerCase(), _valid_opcodes[i]);
			_opcode_to_operation.put(_valid_opcodes[i], _valid_operations_str[i]);
		}
	}
	/**
	 * Transforma una cadena en el opcode correspondiente
	 */
	protected static byte operationToOpcode(String opStr) {
		return _operation_to_opcode.getOrDefault(opStr.toLowerCase(), OPCODE_INVALID_CODE);
	}

	/**
	 * Transforma un opcode en la cadena correspondiente
	 */
	public static String opcodeToOperation(byte opcode) {
		return _opcode_to_operation.getOrDefault(opcode, null);
	}
}
