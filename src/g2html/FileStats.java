package g2html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// data that we collect from the xml about source files
public class FileStats {
	// set of functions that we know are in the file
	private Set<String>                  functions;
	// Lines that are definitely unreachable
	private Set<Integer>                 liveLines;
	// mapping nodes to function names
	private Set<Integer>                 deadLines;
	// mapping nodes to function names
	private Map<String,String>           nodeToFunction;
	// map lines to nodes
	private Map<Integer,TreeSet<String>> lineData;
	// map lines to warning nodes
	private Map<Integer,TreeSet<String>> warnData;
	// the fine object (contains the path)
	private File                         cFile;

	// construct an empty database
	FileStats(){
		functions = new TreeSet<>();
		deadLines = new TreeSet<>();
		liveLines = new TreeSet<>();
		nodeToFunction = new TreeMap<>();
		lineData = new TreeMap<>();
		warnData = new TreeMap<>();
		cFile = null;
	}

	// set the file object
	void setCFile(String s){
		for (String incDir : Config.conf.getIncludes()){
			File candidate = new File(incDir, s);
			if (candidate.exists()) {
				cFile = candidate;
				return;
			}
		}
		cFile = new File(s);
	}

	// get the file object
	File getCFile(){
		return cFile;
	}

	// line contains dead nodes
	void addDead(int i){
		deadLines.add(i);
	}

	// line contains live nodes
	void addLive(int i){
		liveLines.add(i);
	}

	// add another function
	void addFunction(String fun){
		functions.add(fun);
	}

	// add mapping from a node to its function
	void addNodeToFun(String node, String fun){
		nodeToFunction.put(node, fun);
	}

	// add mapping from line to node
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

	// add mapping from line to node
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

	// returns the nodes for the line
	public Set<String> getLineData(int x) {
		return lineData.get(x);
	}

	// returns warning nodes for the line
	public Set<String> getWarnData(int x) {
		return warnData.get(x);
	}

	// returns function of a node
	public String getNodeFun(String id) {
		return nodeToFunction.get(id);
	}

	// checks if the line is unreachable
	public boolean isDead(int lineNr) {
		return deadLines.contains(lineNr);
	}

	// returns the functions of the file
	public Set<String> getFunctions() {
		return functions;
	}

	public boolean isLive(int lineNr) {
		return liveLines.contains(lineNr);
	}
}
