/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;

import jsystem.framework.analyzer.AnalyzerParameter;
import systemobject.terminal.Prompt;

public class TclShellRemote extends TclShellImpl {
	
	CliConnectionImpl conn;
	
	private StringBuffer buffer;
	
	private String tclPath = null;
	
	public TclShellRemote(CliConnectionImpl conn, String tclPath) throws Exception {
		this.conn = conn;
		this.tclPath = tclPath;
	}
	
	/**
	 * Launch Tcl shell.
	 */
	@Override
	public void launch() throws Exception {
		
		CliCommand cmd = new CliCommand(tclPath);
		cmd.setTimeout(10 * 1000);
		cmd.setPrompts(new Prompt[] { new Prompt("% ", false, true) });
		conn.command(cmd);
		if (cmd.isFailed()) {
			throw cmd.getThrown();
		}
		
	}
	
	@Override
	public void exit() {
		conn.command(new CliCommand("exit"));
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
	 * @throws Exception
	 */
	@Override
	public synchronized boolean command(String command) throws Exception {
		
		TclCliCommand cmd = new TclCliCommand(command);
		cmd.setSilent(true);
		cmd.setTimeout(getTimeout());
		conn.command(cmd);
		if (cmd.isFailed()) {
			throw cmd.getThrown();
		}
		AnalyzerParameter[] analyzers = cmd.getAnalyzers();
		if (analyzers != null) {
			for (int i = 0; i < analyzers.length; i++) {
				conn.analyze(analyzers[i], true);
			}
		}
		
		this.buffer.append(cmd.getResult());
		
		return true;
		
	}
	
	@Override
	public synchronized void executeCommand(ShellCommand command) {
		
		String cmd = null;
		String toSend;
		try {
			this.buffer = new StringBuffer();
			// command("set errorCode NONE; set errorInfo {}");
			cmd = command.getCommand() + " " + command.getParmetersAsString();
			String cmd1 = "puts \"\\nstdout: <$stdOut>\";"
					+ "puts \"return value: <$returnValue>\";"
					+ "puts \"errorCode: <$errorCode>\";"
					+ "puts \"errorInfo: <$errorInfo>\"";
			toSend = "set errorCode NONE; set errorInfo {}; set stdOut [catch { " + cmd + " } returnValue]; " + cmd1;
			command(toSend);
		} catch (Exception e) {
			command.setFail(true);
			command.setErrorString(e.getMessage());
			return;
		}
		
		String scriptOutput = getResults();
		if (scriptOutput.length() <= toSend.length()) {
			command.setFail(true);
			command.setErrorString("Script out shorter then command, command = " + toSend + ", out = " + scriptOutput);
			return;
		}
		Pattern p = Pattern.compile("\\<([^\\>]*)\\>", Pattern.DOTALL);
		Matcher m = p.matcher(scriptOutput.substring(toSend.length(), scriptOutput.length()));
		
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars Stdout from: " + scriptOutput);
			return;
		}
		command.setStdOut(m.group(1));
		
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars return value from: " + scriptOutput);
			return;
		}
		command.setReturnValue(m.group(1));
		
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars error code from: " + scriptOutput);
			return;
		}
		command.setErrorCode(m.group(1));
		
		if (!m.find()) {
			command.setFail(true);
			command.setErrorString("Unable to pars error string from: " + scriptOutput);
			return;
		}
		command.setErrorString(m.group(1));
		
		if (!command.getStdOut().equalsIgnoreCase("0")) {
			command.setFail(true);
			command.setErrorString(command.getReturnValue());
		}
		
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	public void source(String fileName) throws IOException {
		
		ShellCommand cmd = new ShellCommand("source", fileName.replace('\\', '/'));
		executeCommand(cmd);
		if (cmd.isFail()) {
			throw new IOException(cmd.getErrorString().split("[\\n\\r]")[0]);
		}
	}
	
	@Override
	public void source(InputStream stream) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void source(InputStream stream, Properties p) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void source(InputStream stream, HashMap<String[], String> entryMap) throws IOException {
	
	}
	
}
