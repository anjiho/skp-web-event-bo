package kr.co.syrup.adreport.framework.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	//static String path = "/Users/anjiho/Downloads/";
	//static String path = "/home/ec2-user/bird-admin-logs";
	public static boolean fileDel(String path, String fileNm){
		File f = new File(path+fileNm);

		if(f.exists()){
			boolean result = f.delete();
			return result;
		}else{
			return false;
		}
	}

	/**
	 * 파일 압축하기
	 * @param targetUrl : 압축 할 파일이 있는 폴더 경로(C:/tar/get)
	 * @param crtFileName : 생성 할 압축 파일 이름(file.zip)
	 * @return
	 * @throws Exception
	 */
	public static boolean setFileZip(String targetUrl, String crtFileName) throws Exception {
		boolean isYn = false;

		String szInputFile1 = targetUrl;
		String szGZipTemp = targetUrl + "/" + crtFileName;

		File inFile1 = new File(szInputFile1);
		inFile1.mkdirs();

		File inDirectory ;

		if (inFile1.isDirectory()){
			inDirectory = new File(szInputFile1);
		}
		else{
			inDirectory = new File(inFile1.getParent());
		}

		File[] inFile = inDirectory.listFiles();
		FileInputStream fis = null;

		File gzipFile = new File(szGZipTemp);
		FileOutputStream fos = new FileOutputStream(gzipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze = null;

		byte[] buffer = new byte[1024 * 8];
		int nRead;

		try {
			for ( int i=0; i < inFile.length ; i++){
				fis = new FileInputStream( inFile[i]);
				ze = new ZipEntry(inFile[i].getName());

				//System.out.println( "압축할 파일명 : " + inFile[i].getName() + ", 파일사이즈 : " + fis.available());
				zos.putNextEntry(ze);

				zos.setLevel(9);
				buffer = new byte[1024 * 8];
				nRead = 0;

				while ((nRead = fis.read(buffer)) != -1){
					zos.write(buffer, 0, nRead);
				}

				fis.close();
				zos.closeEntry();
			}

			zos.close();
			fos.close();

			isYn = true;
		}
		catch (IOException e){
			e.printStackTrace();
			isYn = false;
		}
		finally {
			try {fis.close();}catch(Exception e){}
			try {zos.close();}catch(Exception e){}
			try {fos.close();}catch(Exception e){}
		}

		return isYn;
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
	     InputStream is = new FileInputStream(file);
	     long length = file.length();

	     if (length > Integer.MAX_VALUE) {
	         System.out.println("File is too large");
	     }

	     byte[] bytes = new byte[(int)length];

	     int offset = 0;
	     int numRead = 0;
	     while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	         offset += numRead;
	     }

	     if (offset < bytes.length) {
	         throw new IOException("Could not completely read file "+file.getName());
	     }

	     is.close();
	     return bytes;
	}

	//파일을 생성하는 메소드
	public static void fileMake(String makeFileName) {
		File f1 = new File(makeFileName);

		try {
			f1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//파일을 삭제하는 메소드
	public static void fileDelete(String deleteFileName) {
		File file = new File(deleteFileName);

		if(file.exists() == true){
			file.delete();
		}
	}

    //파일 이동
	public static void fileMove(String oldFilePath, String newFilePath) {
		try {
			Path oldFile = Paths.get(oldFilePath);
			Path newFile = Paths.get(newFilePath);// 이동
			Files.move(oldFile, newFile, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static String concatPath(String... paths) {
		String separator = File.separator;
		StringBuilder buffer = new StringBuilder();
		for(String path : paths){
			if(path == null) continue;
			path = path.trim();
			if(!path.startsWith(separator) && !path.startsWith("https")){
				path = separator + path;
			}
			if(path.endsWith(separator)){
				int length = path.length();
				path = path.substring(0, length-1);
			}
			buffer.append(path);
		}
		return buffer.toString();
	}

	public static String removeFileExtension(String fileName) {
		String extensionFileName = null;
		int idx = fileName.lastIndexOf(".");
		extensionFileName = fileName.substring(0, idx);
		return extensionFileName;
	}

	/**
	 * 엑셀파일을 읽어서 Workbook 객체에 리턴
	 * XLS와 XLSX 확장자를 비교한다.
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static Workbook getWorkbook(String filePath) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		Workbook wb = null;

		if (filePath.toUpperCase().endsWith(".XLS")) {
			wb = new HSSFWorkbook(fis);
		} else if (filePath.toUpperCase().endsWith(".XLSX")) {
			wb = new XSSFWorkbook(fis);
		}
		return wb;
	}
/*
	public static String writeToFile(String filename, byte[] pData) throws IOException{
		try{
			File lOutFile = new File(fileUploadRoot + filename);
			FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
			lFileOutputStream.write(pData);
			lFileOutputStream.close();

			File file = new File(fileDownloadRoot + filename);
			FileInputStream input = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile("file",	file.getName(), "png", IOUtils.toByteArray(input));

			return filename;
			// return multipartFile;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/

	/**
	 * 원본 파일 복사
	 * @param srcFilePath
	 * @param targetFilePath
	 */
	public static void copyFileReplaceExisting(Path srcFilePath, Path targetFilePath) {
		if (srcFilePath != null && targetFilePath != null) {
			try {
				Files.copy(srcFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("파일복사 에러" + e.getMessage());
			}
		}
	}

	public static String sampleDownload(String filename, byte[] pData) throws IOException{
		try{
			File lOutFile = new File("http://3.35.98.180/barcode_1200904115904.png");
			FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
			lFileOutputStream.write(pData);
			lFileOutputStream.close();

			return filename;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String args[]) throws Exception {
		System.out.print(concatPath("https://", "playardev.syrup.co.kr", "web-event", "main.html?eventId=000278"));
	}
}
