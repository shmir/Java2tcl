/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public interface TclShell {
	
	public abstract void init() throws Exception;
	
	/**
	 * Launch Tcl shell.
	 */
	public abstract void launch() throws Exception;
	
	/**
	 * Return command output
	 * 
	 * @return Command output
	 */
	public abstract String getResults();
	
	/**
	 * Execute raw Tcl command.
	 * 
	 * @param command
	 *        The command to execute
	 * @return flag indicating weather command succedded or failed
	 * @throws IOException
	 */
	public abstract boolean command(String command) throws Exception;
	
	public abstract void executeCommand(ShellCommand command);
	
	public abstract void exit();
	
	public abstract void close();
	
	public abstract void source(InputStream stream) throws IOException;
	
	public void source(InputStream stream, Properties p) throws IOException;
	
	public void source(InputStream stream, HashMap<String[], String> entryMap) throws IOException;
	
	/**
	 * @return Returns the timeout.
	 */
	public abstract long getTimeout();
	
	/**
	 * @param timeout
	 *        The timeout to set.
	 */
	public abstract void setTimeout(long timeout);
	
	public abstract String getLogFile();
	
	public abstract void setLogFile(String logFile);
	
	public abstract String getPureTclLogFile();
	
	public abstract void setPureTclLogFile(String logFile);
	
	public abstract boolean isPrintCommand();
	
	public abstract void setPrintCommand(boolean printCommand);
	
	public abstract boolean isPrintReturn();
	
	public abstract void setPrintReturn(boolean printReturn);
	
	public void setWindowTitle(String title);
	
}