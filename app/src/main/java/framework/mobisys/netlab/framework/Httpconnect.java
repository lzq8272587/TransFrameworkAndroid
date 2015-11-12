import java.io.InputStream;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class httpconnect {

	public static void main(String[] args) {
		String linkString = args[0];
		int blockSize = Integer.parseInt(args[1]);
		String sPath = args[2];
		int isDownload = Integer.parseInt(args[3]);
		int nStartPos = 0;
		int timeout = 15000;
		int NUMLIMIT_FAIL=5;
//		long nEndPos = getFileSize(linkString);
		long nEndPos = 30000000;
		byte[] b = new byte[blockSize];  
		URL url;
		try {
			url = new URL(linkString);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setConnectTimeout(timeout);
			httpConnection.setReadTimeout(timeout);
			httpConnection.setRequestMethod(isDownload==1?"GET":"POST");
			String sProperty = "bytes="  + nStartPos +  "-" +nEndPos;   //sProperty设置的是开始下载的位置，即断点。若nStartPos为0则完整下载整个文件
			httpConnection.setRequestProperty("RANGE" , sProperty);     
			//当然也可以这么写：httpConnection.setRequestProperty("RANGE","bytes=50-20070"); 
			InputStream input = httpConnection.getInputStream();
			RandomAccessFile oSavedFile = new RandomAccessFile(sPath, "rw");
//			 读取网络文件,写入指定的文件中
			int nRead,failedCnt=1;
			System.out.println("Connect successfully\nBegin downloading...");
			while (nStartPos < nEndPos&&failedCnt<=NUMLIMIT_FAIL) {
				try {
					nRead = input.read(b, 0, blockSize);
					if(nRead>0){
						oSavedFile.write(b, 0, nRead);
						nStartPos += nRead;
					}
					else {
						break;
					}
					System.out.println(nStartPos*100/nEndPos+"%");
				} catch (Exception e) {
					System.out.println("Network error, try to reconnect ("+failedCnt+")");
					failedCnt++;
					
					httpConnection.disconnect();
					httpConnection = (HttpURLConnection) url.openConnection();
					httpConnection.setConnectTimeout(timeout);
					httpConnection.setReadTimeout(timeout);
					sProperty = "bytes="  + nStartPos +  "-" +nEndPos;   //sProperty设置的是开始下载的位置，即断点。若nStartPos为0则完整下载整个文件
					httpConnection.setRequestProperty("RANGE" , sProperty);     
					//当然也可以这么写：httpConnection.setRequestProperty("RANGE","bytes=50-20070"); 
					input = httpConnection.getInputStream();
				}
			}
			if(failedCnt>NUMLIMIT_FAIL){
				System.out.println("Download failed.");
			}
			else {
				System.out.println("Download successfully!");
			}
			httpConnection.disconnect();
			oSavedFile.close();
		} catch (Exception e1) {
			e1.printStackTrace();
//			System.out.println("Connection failed.");
		}
		System.out.println("Ends.");
	}
	
}
