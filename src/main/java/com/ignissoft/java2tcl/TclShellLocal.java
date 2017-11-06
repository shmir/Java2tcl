/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.utils.FileUtils;

public class TclShellLocal extends TclShellImpl {
	
	private static final String ENTER = "\r\n";
	
	private String shellName;
	
	private File dir;
	
	private Process p;
	
	private InputStream out;
	
	private InputStream err;
	
	private OutputStream in;
	
	private long errTimeout = 10000;
	
	private StringBuffer buffer;
	
	/**
	 * Class constructor
	 * 
	 * @param shell
	 *        File holding pointer to Tcl interpreter.
	 */
	public TclShellLocal(File shell) {
		this(shell.getPath(), shell.getParentFile());
	}
	
	/**
	 * Class constructor with
	 * 
	 * @param shellName
	 * @param dir
	 */
	public TclShellLocal(String shellName, File dir) {
		super();
		this.shellName = shellName;
		this.dir = dir;
		Runtime.getRuntime().addShutdownHook(new CloseThread(this));
	}
	
	/**
	 * Launch Tcl shell.
	 */
	@Override
	public void launch() throws IOException {
		p = Runtime.getRuntime().exec(new String[] { shellName }, null, dir);
		in = p.getOutputStream();
		err = p.getErrorStream();
		out = new BufferedInputStream(p.getInputStream(), 8192 * 4);
	}
	
	/**
	 * Return command output
	 * 
	 * @return Command output
	 */
	@Override
	public String getResults() {
		return buffer.toString();
	}
	
	/**
	 * Execute raw Tcl command.
	 * 
	 * @param command
	 *        The command to execute
	 * @return flag indicating weather command succedded or failed
	 * @throws IOException
	 */
	@Override
	public synchronized boolean command(String command) throws IOException {
		
		boolean errFound = false;
		buffer = new StringBuffer();
		String cmd = command + ENTER + "puts " + getPrompt() + ENTER;
		// Write command to standard input
		in.write(cmd.getBytes());
		in.flush();
		
		// Get command execution time
		long startTime = System.currentTimeMillis();
		
		while (true) {
			
			if (errFound) {
				if (System.currentTimeMillis() - startTime > errTimeout) {
					exit();
					throw new IOException("Timeout on TCL std error. Max timeout = "
							+ getErrTimeout()
							+ ", waiting - "
							+ (System.currentTimeMillis() - startTime));
				}
			} else {
				if (System.currentTimeMillis() - startTime > getTimeout()) {
					exit();
					throw new IOException("Timeout on TCL std out. Max timeout = "
							+ getTimeout()
							+ ", waiting - "
							+ (System.currentTimeMillis() - startTime));
				}
			}
			
			int avail = err.available();
			while (avail > 0) {
				errFound = true;
				char c = (char) err.read();
				System.err.print(c);
				buffer.append(c);
				avail--;
			}
			if (promptFound()) {
				return errFound;
			}
			
			avail = out.available();
			while (avail > 0) {
				char c = (char) out.read();
				if (isPrintCommand()) {
					// There is no point to print the return value without the command.
					if (isPrintReturn()) {
						System.out.print(c);
					}
				}
				if (getLogFile() != null) {
					logFileS.append(c);
				}
				buffer.append(c);
				avail--;
			}
			if (promptFound()) {
				return errFound;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			
		}
		
	}
	
	@Override
	public synchronized void executeCommand(ShellCommand command) {
		
		StringBuffer cmd = new StringBuffer();
		String logStr = System.getProperty("line.separator") + command.getCommand() + " " + command.getParmetersAsString();
		if (isPrintCommand()) {
			System.out.print(logStr);
		}
		if (getPureTclLogFile() != null) {
			pureTclLogFileS.append(logStr);
		}
		if (getLogFile() != null) {
			logFileS.append(logStr);
		}
		
		cmd.append("set errorCode NONE");
		cmd.append(ENTER);
		cmd.append("set errorInfo {}");
		cmd.append(ENTER);
		cmd.append("set stdOut [catch { ");
		cmd.append(command.getCommand() + " " + command.getParmetersAsString());
		cmd.append(" } returnValue]" + ENTER);
		cmd.append("puts \"\nstdout: <$stdOut>\"");
		cmd.append(ENTER);
		cmd.append("puts \"return value: <$returnValue>\"");
		cmd.append(ENTER);
		cmd.append("puts \"errorCode: <$errorCode>\"");
		cmd.append(ENTER);
		// cmd.append("puts \"errorInfo: <$errorInfo>\"");
		// cmd.append(ENTER);
		
		try {
			command(cmd.toString());
		} catch (IOException e) {
			command.setFail(true);
			command.setErrorString(e.getMessage());
			return;
		}
		
		String scriptOutput = getResults();
		
		Pattern p = Pattern.compile("stdout: <(.*)>" + System.getProperty("line.separator") + "return value:", Pattern.DOTALL);
		Matcher m = p.matcher(scriptOutput);
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars Stdout from: " + scriptOutput);
			return;
		}
		command.setStdOut(m.group(1));
		
		p = Pattern.compile("return value: <(.*)>" + System.getProperty("line.separator") + "errorCode:", Pattern.DOTALL);
		m = p.matcher(scriptOutput);
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars return value from: " + scriptOutput);
			return;
		}
		command.setReturnValue(m.group(1));
		
		p = Pattern.compile("errorCode: <(.*)>" + System.getProperty("line.separator") + "---DONE---", Pattern.DOTALL);
		m = p.matcher(scriptOutput);
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars error code from: " + scriptOutput);
			return;
		}
		command.setErrorCode(m.group(1));
		
		/*
		 * Seems like in many cases an error message is thrown but the error code is
		 * clean so it is better to ignore the error message.
		 * if (!m.find()){
		 * command.setFail(true);
		 * command.setErrorString("Unable to pars error string from: " + scriptOutput);
		 * return;
		 * }
		 * command.setErrorString(m.group(1));
		 */
		
		if (!command.getStdOut().equalsIgnoreCase("0")) {
			command.setFail(true);
			command.setErrorString(command.getReturnValue());
		}
		
	}
	
	@Override
	public void exit() {
		if (p != null) {
			try {
				/*
				 * First write log files then exit. There are scenarios where at this
				 * point the 'in' OutputStream is corrupted and flushing it results in
				 * exception so it is important to write the log files before we
				 * attempt to write it.
				 */
				if (getLogFile() != null) {
					FileUtils.write(getLogFile(), logFileS.toString());
				}
				if (getPureTclLogFile() != null) {
					FileUtils.write(getPureTclLogFile(), pureTclLogFileS.toString());
				}
				if (in != null) {
					in.write(("exit" + ENTER).getBytes());
					in.flush();
				}
			} catch (IOException ignore) {
			
			}
			p.destroy();
			p = null;
		}
	}
	
	private boolean promptFound() {
		return (buffer.toString().indexOf(getPrompt()) >= 0);
	}
	
	/**
	 * Empty close implementation. <br>
	 * Due to racing conditions, we mustn't let the system object manager close Tcl
	 * but the object who uses it is responsible to call exit() explicitly when it is
	 * done using it.
	 */
	@Override
	public synchronized void close() {}
	
	@Override
	public void source(InputStream stream) throws IOException {
		source(stream, (Properties) null);
	}
	
	@Override
	public void source(InputStream stream, Properties p) throws IOException {
		File tempFile = File.createTempFile("tmp.source", ".tcl");
		if (p == null) {
			FileWriter fw = new FileWriter(tempFile);
			StringBuffer source = new StringBuffer();
			while (stream.available() > 0) {
				char c = (char) stream.read();
				if (c < 0) {
					break;
				}
				fw.write(c);
				source.append(c);
			}
			fw.flush();
			fw.close();
		} else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			FileWriter fw = new FileWriter(tempFile);
			String line = null;
			while ((line = reader.readLine()) != null) {
				Enumeration<Object> en = p.keys();
				while (en.hasMoreElements()) {
					String regExp = (String) en.nextElement();
					Matcher m = Pattern.compile(regExp).matcher(line);
					if (m.find()) {
						line = line.substring(0, m.start(1)) + p.getProperty(regExp) + line.substring(m.end(1), line.length());
					}
				}
				fw.write(line);
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		}
		
		ShellCommand cmd = new ShellCommand("source", tempFile.getPath().replace('\\', '/'));
		executeCommand(cmd);
		if (cmd.isFail()) {
			throw new IOException(cmd.getErrorString().split("[\\n\\r]")[0]);
		}
		tempFile.delete();
	}
	
	@Override
	public void source(InputStream stream, HashMap<String[], String> entryMap) throws IOException {
		
		File tempFile = File.createTempFile("tmp.source", ".tcl");
		if (entryMap == null) {
			FileWriter fw = new FileWriter(tempFile);
			StringBuffer source = new StringBuffer();
			while (stream.available() > 0) {
				char c = (char) stream.read();
				if (c < 0) {
					break;
				}
				fw.write(c);
				source.append(c);
			}
			fw.flush();
			fw.close();
		} else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			FileWriter fw = new FileWriter(tempFile);
			Boolean find = false;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("]") != -1)
					System.out.println("*************");
				find = true;
				for (String[] key : entryMap.keySet()) {
					
					if (entryMap.get(key).equals("modify")) {
						// String regExp = key[0] + "," + key[1] + "," + key[2] + "\\s+\\{(.*)\\}";
						String regExp = key[0] + "," + key[1] + "\\s+\\{(.*)\\}";
						Matcher m = Pattern.compile(regExp).matcher(line);
						if (m.find()) {
							System.out.println("find text: " + line);
							line = line.substring(0, m.start(1)) + key[2] + line.substring(m.end(1), line.length());
							entryMap.put(key, "done");
							continue;
						}
					} else if (entryMap.get(key).equals("append")) {
						// String regExp = key[0] +","+ key[1];
						String regExp = key[0];
						Matcher m = Pattern.compile(regExp).matcher(line);
						if (m.find()) {
							System.out.println("find text: " + line);
							line = line + "\n" + key[0] + key[1] + "\t\t{" + key[2] + "}";
							entryMap.put(key, "done");
							continue;
						}
					} else if (entryMap.get(key).equals("delete")) {
						// String regExp = key[0]+"," + key[1] + "," + key[2] + "\\s+\\{" + key[3] + "}";
						String regExp = key[0] + "," + key[1] + "\\s+\\{" + key[2] + "}";
						Matcher m = Pattern.compile(regExp).matcher(line);
						if (m.find()) {
							System.out.println("find text: " + line);
							line = "";
							find = false;
							entryMap.put(key, "done");
							continue;
						}
					}
				}
				if (find) {
					fw.write(line);
					fw.write("\n");
				}
				
			}
			fw.flush();
			fw.close();
		}
		ShellCommand cmd = new ShellCommand("source", tempFile.getPath().replace('\\', '/'));
		executeCommand(cmd);
		if (cmd.isFail()) {
			throw new IOException(cmd.getErrorString().split("[\\n\\r]")[0]);
		}
		tempFile.delete();
	}
	
	/**
	 * @return Returns Standard ERR timeout.
	 */
	public long getErrTimeout() {
		return errTimeout;
	}
	
	/**
	 * @param errTimeout
	 *        Standard Error timeout to set.
	 */
	public void setErrTimeout(long errTimeout) {
		this.errTimeout = errTimeout;
	}
	
	class CloseThread extends Thread {
		TclShellLocal shell;
		
		public CloseThread(TclShellLocal shell) {
			this.shell = shell;
		}
		
		@Override
		public void run() {
			if (shell != null) {
				shell.close();
			}
		}
	}
	
}
