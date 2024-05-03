package es.um.redes.nanoFiles.util;

public class FileUtils {
	
	public static FileInfo[] fromStringToFiles(String files) {
		String[] fileStrArray = files.split("@");
		FileInfo[] fileInfoArray = new FileInfo[fileStrArray.length];
		for (int i = 0; i < fileStrArray.length; i++) {
			String[] parts = fileStrArray[i].split(";");
			String hash = parts[0];
			String name = parts[1];
			long size = Long.parseLong(parts[2]);
			String path = parts[3];
			fileInfoArray[i] = new FileInfo(hash, name, size, path);
		}
		return fileInfoArray;
	}

	public static String arrayToString(FileInfo[] files) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < files.length; i++) {
			sb.append(files[i].fileHash).append(";").append(files[i].fileName).append(";").append(files[i].fileSize)
					.append(";").append(files[i].filePath);
			if (i < files.length - 1) {
				sb.append("@");
			}
		}
		return sb.toString();
	}

	
}
