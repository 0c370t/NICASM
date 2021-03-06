package site.projectname.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;


/**
 * Used across the program to generate logs. Created logs are automagically stored in Logger.logs
 *
 * Used to log arbitrary information into a timestamped file. File can be located in the Logs directory
 * They are further sorted into directories according to the creator (given in the constructor)
 *
 * @author	Brian Donald
 * @version	1.0
 * @since	2015-5-24
 */
public class Logger {
	private PrintWriter writer;
	private Logger(){} 		//Prevents instantizing
	public static boolean debugGlobal = false;
	public boolean debug = false;
	public int indentLevel = 0;
	/**
	 * Map of all currently running logs.
	 */
	public static HashMap<String, Logger> logs = new HashMap<String,Logger>();
	private static DateFormat timeStamp = new SimpleDateFormat("[HH:mm:ss] - ");
	/**
	 * Checks log map for pre-existing log, if no log has been created, makes and returns new Logger
	 * @param	name	Name of log to create or get
	 * @param	debug	Debug state if new log is needed
	 * @return			Log with name and debug state set
	 */
	public static Logger getLog(String name, boolean debug){
		if(logs.containsKey(name) && logs.get(name).debug == debug){
			return logs.get(name);
		} else {
			return new Logger(name,debug);
		}
	}
	/**
	 * Checks log map for pre-existing log, if no log has been created, makes and returns new Logger
	 * @param	name	Name of log to create or get
	 * @return			Log with name and debug state set
	 */
	public static Logger getLog(String name){
		if(logs.containsKey(name)){
			return logs.get(name);
		} else {
			return new Logger(name);
		}
	}
	/**
	 * Checks log map for pre-existing log, if no log has been created, makes and returns new Logger
	 * @param	name	Name of log to create or get
	 * @param	format	Time-Stamp format if new log is required
	 * @param	debug	Debug state if new log is needed
	 * @return			Log with name and debug state set
	 */
	public static Logger getLog(String name, SimpleDateFormat format, boolean debug){
		if(logs.containsKey(name)){
			return logs.get(name);
		} else {
			return new Logger(name,format,debug);
		}
	}
	/**
	 * Returns a string of whitespace, used for formatting text
	 * @param	v 	Varying string
	 * @param	l	Target Length
	 * @return		String of space of l - v.length() length.
	 */
	public static String spacer(String v, int l){
		String out = "";
		for(int i=0;i<l-v.length();i++)
			out += " ";
		return out;
	}

	/**
	 * Starts the Logger, also including a given name of what started the log.
	 * Also adds itself to the logs map, allowing it to be accessed without needing to pass it within
	 * a single thread/package
	 *
	 * @param	creator Indentifies which class initialized the log
	 * @param	logName	Alternate naming convention for log titles
	 * @param	debug		Determines if Debugging is enabled. used to toggle verbose output
	 */
	public Logger(String creator, SimpleDateFormat logName, boolean debug) {
		DateFormat dateFormat = logName;
		File dir = new File("Logs");
		if(!dir.exists())
			try{dir.mkdir();System.out.println("Making Logs Directory!");}catch(Exception e){}
		File dir2 = new File(dir, creator);
		if(!dir2.exists())
			try{dir2.mkdir();System.out.println("Making " +creator+" Directory!");}catch(Exception e){}
		File log = new File(dir2, creator + dateFormat.format(new Date()) + ".log");
		try {
			log.createNewFile();
			writer = new PrintWriter(log);
			write("Log created by "+creator+".");
			spacer();
		} catch(Exception e){e.printStackTrace();}
		Logger.logs.put(creator, this);
		this.debug = debug;
		if(debug){
			spacer();
			write("Debug enabled!");
			spacer();
		}
	}
	/**
	 * Starts the Logger, also including a given name of what started the log.
	 * Also adds itself to the logs map, allowing it to be accessed
	 * @param	creator		Package, Class, or other catagory that created and uses the log
	 * @param	debug		Determines if Debugging is enabled. used to toggle verbose output
	 */
	public Logger(String creator, boolean debug){
		this(creator,new SimpleDateFormat("_MMMM-dd_HH.mm"),debug);
	}
	/**
	 * Starts the Logger, also including a given name of what started the log.
	 * Also adds itself to the logs map, allowing it to be accessed
	 *
	 * @param	creator		Package, Class, or other catagory that created and uses the log
	 * @param	logName	Alternate naming convention for log titles
	 */
	public Logger(String creator, SimpleDateFormat logName){
		this(creator, logName, false);
	}
	/**
	 * Starts the Logger, also including a given name of what started the log.
	 * Also adds itself to the logs map, allowing it to be accessed
	 * @param	creator 	Package, Class, or other catagory that created and uses the log
	 */
	public Logger(String creator) {
		this(creator,new SimpleDateFormat("_MMMM-dd_HH.mm"));
	}


	/**
	 * Prints an exception to the log
	 * @param	e		Exception to be printed
	 */
	public void writeError(Exception e) {
		writer.println(timeStamp.format(new Date()) + "ERROR!");
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String error = sw.toString();
		error = timeStamp.format(new Date())+error;
		error = error.replace("\n","\n"+timeStamp.format(new Date()));
		writer.println(error);
		if(this.debug)
			System.out.println(error);
		writer.flush();
	}
	/**
	 * Writes to the log, then flushes it to the file.
	 * @param	in	String to be written
	 */
	public void write(String in) {
		String time = timeStamp.format(new Date());
		writer.println(time + in);
		if(this.debug)
			System.out.println(time+in);
		writer.flush();
	}
	/**
	 * Writes to the log, then flushes it to the file, without a time stamp.
	 * @param	in 	String to be written
	 */
	public void writeNoStamp(String in) {
		writer.println(in);
		if(this.debug)
			System.out.println(in);
		writer.flush();
	}
	/**
	 * Writes a spacer into the log, useful for organizing the log.
	 */
	public void spacer() {
		writer.println("=--------------------------------------------------=");
		if(this.debug)
			System.out.println("=--------------------------------------------------=");
		writer.flush();
	}
	/**
	 * Uses printf to output formatted text
	 * @param	in		String for printf
	 * @param	args	Arguments for printf
	 */
	public void printf(String in, Object... args){
		writer.print(timeStamp.format(new Date()));
		writer.printf(in, args);
		if(this.debug)
			System.out.printf(in,args);
		writer.flush();
	}
	/**
	 * Used for verbose output, only written if debug is enabled for the log
	 * @param	in 		String to print
	 */
	public void debug(String in){
		if(this.debug){
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			String className = stackTraceElements[2].getClassName();
			String methodName = stackTraceElements[2].getMethodName();
			int lineNum = stackTraceElements[2].getLineNumber();
			if(className.startsWith("site.projectname"))
				className = className.split("[.]")[className.split("[.]").length-1];
			String indent = "--";
			for(int i=0;i<indentLevel;i++)
				indent+="--";
			indent=indent+Logger.spacer(indent,4*(indentLevel) + 4);
			//indent+="\t";

			printf(timeStamp.format(new Date())+"DEBUG:\t%-40s|-|%s%s\n",(className+"."+methodName+"():"+lineNum),indent,in);
		}
	}
	public void debug(String in,int indentMod){
		if(this.debug){
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			String className = stackTraceElements[2].getClassName();
			String methodName = stackTraceElements[2].getMethodName();
			int lineNum = stackTraceElements[2].getLineNumber();
			if(className.startsWith("site.projectname"))
				className = className.split("[.]")[className.split("[.]").length-1];
			String indent = "--";
			for(int i=0;i<indentLevel+indentMod;i++)
				indent+="--";
			indent=indent+Logger.spacer(indent,4*(indentLevel+indentMod) + 4);
			//indent+="\t";

			printf(timeStamp.format(new Date())+"DEBUG:\t%-40s|-|%s%s\n",(className+"."+methodName+"():"+lineNum),indent,in);
		}
	}
	public void indent(){
		this.indentLevel++;
	}
	public void unindent(){
		this.indentLevel--;
	}
	/**
	 * Used for verbose output, writes spacer if debug is enabled
	 */
	public void debugSpacer(){
		if(this.debug){
			debug("=-----------------------------------------------------------=",-1*indentLevel);
			debug("=|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||=",-1*indentLevel);
			debug("=-----------------------------------------------------------=",-1*indentLevel);
		}
	}
}
