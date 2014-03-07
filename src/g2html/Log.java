package g2html;


import java.io.PrintStream;

public class Log {

	static PrintStream printf(String format, Object... args){
		if (Config.conf.verbose())
			return System.out.printf(format,args);
		else
			return null;
	}

	static void println(String s){
		if (Config.conf.verbose())
			System.out.println(s);
	}
}
