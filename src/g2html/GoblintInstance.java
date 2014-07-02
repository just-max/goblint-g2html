package g2html;

import java.io.IOException;
import java.lang.reflect.Field;

public class GoblintInstance implements Runnable {
	Process goblintProcess = null;
	private final int instNr;
	String params;

	public GoblintInstance(int instNr, String params) {
		System.out.printf("Creating Goblint instance %d : %s\n",instNr,params);
		this.instNr = instNr;
		this.params = params;
		new Thread(this).start();
	}

	public static int getUnixPID(Process process) throws Exception
	{
		System.out.println(process.getClass().getName());
		if (process.getClass().getName().equals("java.lang.UNIXProcess"))
		{
			Class cl = process.getClass();
			Field field = cl.getDeclaredField("pid");
			field.setAccessible(true);
			Object pidObject = field.get(process);
			return (Integer) pidObject;
		} else
		{
			throw new IllegalArgumentException("Needs to be a UNIXProcess");
		}
	}

	public static int signal(String nr,Process process){
		try {
			int pid = getUnixPID(process);
			String command = "kill -" + nr + " " + pid;
			System.out.printf("Running: %s\n",command);
			return Runtime.getRuntime().exec(command).waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void goblintNext(){
		signal("SIGUSR2", goblintProcess);
	}

	public void goblintStop(){
		signal("SIGTSTP", goblintProcess);
	}

	public void goblintContinue(){
		signal("SIGUSR1", goblintProcess);
	}

	public void goblintExit(){
		signal("SIGTERM", goblintProcess);
	}

	@Override
	public void run() {
		StringBuilder command = new StringBuilder("./goblint --sets interact.out ");
		command.append(instNr);
		command.append(" --set interact.enabled true");
		command.append(" --set interact.paused true ");
		command.append(params);
		System.out.printf("Command: %s\n",command.toString());
		try {
			goblintProcess = Runtime.getRuntime().exec(command.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			goblintProcess.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
