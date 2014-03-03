package g2html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class FileStats {

	private Set<String>                  functions;
	private Set<Integer>                 deadLines;
	private Map<String,String>           nodeToFunction;
	private Map<Integer,TreeSet<String>> lineData;
	private Map<Integer,TreeSet<String>> warnData;
	private File                         cFile;
	
	FileStats(){
		functions = new TreeSet<String>();
		deadLines = new TreeSet<Integer>();
		nodeToFunction = new TreeMap<String,String>();
		lineData = new TreeMap<Integer,TreeSet<String>>();
		warnData = new TreeMap<Integer,TreeSet<String>>();
		cFile = null;
	}

	void setCFile(String s){
		cFile = new File(s);
	}

	File getCFile(){
		return cFile;
	}

	void addDead(int i){
		deadLines.add(i);
	}

	void addFunction(String fun){
		functions.add(fun);
	}

	void addNodeToFun(String node, String fun){
		nodeToFunction.put(node, fun);
	}
	
	void addLineData(String node, int nr){
		TreeSet<String> ns = lineData.get(nr);
		if (ns==null) {
			ns = new TreeSet<String>();
			ns.add(node);
			lineData.put(nr, ns);
		} else {
			ns.add(node);
		}
	}
	
	void addWarning(String node, int nr){
		TreeSet<String> ns = warnData.get(nr);
		if (ns==null) {
			ns = new TreeSet<String>();
			ns.add(node);
			warnData.put(nr, ns);
		} else {
			ns.add(node);
		}
	}
	
	void printJson(File f) throws IOException{
		FileWriter os = new FileWriter(f); 
		os.write("{\n");
		printDead(os);
		os.write(",\n");
		printNodeToFun(os);
		os.write(",\n");
		printData(os,"data",lineData);
		os.write(",\n");
		printData(os,"warnings",warnData);
		os.write("}\n");
		os.close();
	}

	private void printData(FileWriter os, String name, Map<Integer,TreeSet<String>> mp) throws IOException {
		os.write("\""+name+"\" : {\n  ");
		boolean first = true;
		for (Integer i : mp.keySet()) {
			if (!first)
				os.write(", ");
			first = false;
			os.write("\""+i+"\" : [");
			boolean first1 = true;
			for (String n : mp.get(i)) {
				if (!first1)
					os.write(", ");
				first1 = false;
				os.write("\""+n+"\"");
			}
			os.write("]\n");
		}
		os.write("}");
	}

	private void printNodeToFun(FileWriter os) throws IOException {
		os.write("\"functions\" : {\n");
		boolean first = true;
		for (String s : nodeToFunction.keySet()) {
			if (!first)
				os.write(",\n");
			first = false;
			os.write("\""+s+"\" : \""+nodeToFunction.get(s)+"\"");
		}
		os.write("}");
	}

	private void printDead(FileWriter os) throws IOException {
		os.write("\"dead\" : [ ");
		boolean first = true;
		for (int i : deadLines) {
			if (!first)
				os.write(", ");
			first = false;
			os.write(i+"");
		}
		os.write("]");
	}

	public Set<String> getFunctions() {
		return functions;
	}
}
