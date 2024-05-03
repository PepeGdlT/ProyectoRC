 package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: Añadir aquí todas las constantes que definen los diferentes tipos de
	 * mensajes del protocolo de comunicación con el directorio.
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_LOGIN = "login";
	
	//	BOLETIN 4 EJ 8
	public static final String OPERATION_FAIL_LOGIN	 = "loginFail";
	public static final String OPERATION_LOGIN_OK = "loginOk";
	
	public static final String OPERATION_LOGOUT = "logout";
	public static final String OPERATION_LOGOUT_OK = "logoutOk";
	public static final String OPERATION_FAIL_LOGOUT = "logoutFail";
	
	public static final String OPERATION_USERLIST = "userlist";
	public static final String OPERATION_USERLIST_OK = "userlistOk";
	public static final String OPERATION_FAIL_USERLIST = "userlistFail";
	
	public static final String OPERATION_REGISTER_PORT = "register_server_port";
	public static final String OPERATION_REGISTER_PORT_OK = "register_server_portOk";
	public static final String OPERATION_FAIL_REGISTER_PORT = "register_server_portFail";
	
	public static final String OPERATION_UNREGISTER_PORT = "unresgister_server_port";
	public static final String OPERATION_UNREGISTER_PORT_OK = "unresgister_server_portOk";
	public static final String OPERATION_FAIL_UNREGISTER_PORT = "unresgister_server_portFail";
	
	public static final String OPERATION_LOOKUP_USERNAME = "lookup_username";
	public static final String OPERATION_LOOKUP_USERNAME_OK = "lookup_usernameOk";
	public static final String OPERATION_FAIL_LOOKUP_USERNAME = "lookup_usernameFail";
	
	public static final String OPERATION_PUBLISH = "publish";
	public static final String OPERATION_PUBLISH_OK = "publishOk";
	public static final String OPERATION_FAIL_PUBLISH = "publishFail";
	
	public static final String OPERATION_FILELIST = "filelist";
	public static final String OPERATION_FILELIST_OK = "filelistOK";
	public static final String OPERATION_FAIL_FILELIST = "filelistFail";

	public static final String OPERATION_SEARCH = "search";
	public static final String OPERATION_SEARCH_OK = "searchOK";
	public static final String OPERATION_SEARCH_FAIL = "searchFail";
	

}
