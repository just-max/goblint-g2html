package g2html;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

public class Result {
	private final static Logger LOGGER = Logger.getLogger(Result.class.getName());

	File resDir;
	File cfgDir;
	File filDir;
	File nodDir;
	File warDir;

	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	private void prepareDirectories(String resultDir) throws IOException {
		LOGGER.info("prepareDirectories(...)");
		resDir = new File(resultDir);
		deleteDirectory(resDir);
		if (!resDir.mkdir()) throw new IOException("prepareDirectories - resDir");
		
		cfgDir = new File(resDir,Config.cfgSubdir);
		if (!cfgDir.mkdir()) throw new IOException("prepareDirectories - cfgDir");
		
		filDir = new File(resDir,Config.sourceFilesSubdir);
		if (!filDir.mkdir()) throw new IOException("prepareDirectories - filDir");

		nodDir = new File(resDir,Config.nodesSubdir);
		if (!nodDir.mkdir()) throw new IOException("prepareDirectories - nodDir");
		
		warDir = new File(resDir,Config.warningsSubdir);
		if (!warDir.mkdir()) throw new IOException("prepareDirectories - warDir");
	}
	
	private static void copyFromJar(String f,File t) throws IOException{
		InputStream stream = Main.class.getResourceAsStream(f);
	    if (stream == null) {
	        throw new IOException("Cannot open resource: "+f);
	    }
	    OutputStream resStreamOut = null;
	    int readBytes;
	    byte[] buffer = new byte[4096];
	    try {
	        resStreamOut = new FileOutputStream(t);
	        while ((readBytes = stream.read(buffer)) > 0) {
	            resStreamOut.write(buffer, 0, readBytes);
	        }
	    } finally {
	        stream.close();
	        resStreamOut.close();
	    }
	}

	public static void copyFile( File from, File to ) throws IOException {

		if ( !to.exists() ) { to.createNewFile(); }

		try (
						FileChannel in = new FileInputStream( from ).getChannel();
						FileChannel out = new FileOutputStream( to ).getChannel() ) {

			out.transferFrom( in, 0, in.size() );
		}
	}
	public Result(String resDir) throws IOException {
		prepareDirectories(resDir);
		for (String f : Config.preparedFiles){
			try {
				/* try jar first ... */
				copyFromJar("/resources/" + f, new File(resDir, f));
			} catch (IOException e){
				/* .. no? This means that we are debugging in an IDE? */
				copyFile(new File("../resources/"+f),new File(resDir, f));
			}
		}		
	}

	public File getListingFile(String file) {
		File f = new File(filDir, file+".html");
		LOGGER.info("getListingFile("+file+") = "+f.getAbsolutePath());
		return f;
	}

	public File getSvgFile(String file, String fun) {
		File dir = new File(cfgDir,file) ;
		dir.mkdir();
		File f = new File(dir,fun+".svg");
		LOGGER.info("getSvgFile("+file+","+fun+") = "+f.getAbsolutePath());
		return f;
	}

	public File getNodeFile(String node) {
		File f = new File(nodDir,node+".xml");
		LOGGER.info("getNodeFile("+node+") = "+f.getAbsolutePath());
		return f;
	}

	public File getWarningFile(String war) {
		File f = new File(warDir,war+".xml");
		LOGGER.info("getWarningFile("+war+") = "+f.getAbsolutePath());
		return f;
	}

	public File getGlobalFile() {
		File f = new File(nodDir,"globals.xml");
		LOGGER.info("getGlobalFile() = "+f.getAbsolutePath());
		return f;
	}
	public File getReportFile() {
		File f = new File(resDir,"index.xml");
		LOGGER.info("getReportFile() = "+f.getAbsolutePath());
		return f;
	}
}