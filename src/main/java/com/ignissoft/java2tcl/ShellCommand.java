package com.ignissoft.java2tcl;

import java.io.Serializable;

/**
 * @author yoram@ignissoft.com
 */
public class ShellCommand implements Serializable {
	
	private static final long serialVersionUID = -4272716476298057979L;
	
	String command;
	
	Object[] parameters;
	
	boolean fail;
	
	String errorCode;
	
	String errorString;
	
	String stdOut;
	
	String returnValue;
	
	public ShellCommand() {}
	
	public ShellCommand(String command) {
		this(command, (Object[]) null);
	}
	
	public ShellCommand(String command, Object... parameters) {
		this.command = command;
		this.parameters = parameters;
	}
	
	/**
	 * @return Returns the errorCode.
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * @param errorCode
	 *        The errorCode to set.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * @return Returns the returnValue.
	 */
	public String getReturnValue() {
		return returnValue;
	}
	
	/**
	 * @param returnValue
	 *        The returnValue to set.
	 */
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
	/**
	 * @return Returns the command.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * @param command
	 *        The command to set.
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * @return Returns the errorString.
	 */
	public String getErrorString() {
		return errorString;
	}
	
	/**
	 * @param errorString
	 *        The errorString to set.
	 */
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	
	/**
	 * @return Returns the fail.
	 */
	public boolean isFail() {
		return fail;
	}
	
	/**
	 * @param fail
	 *        The fail to set.
	 */
	public void setFail(boolean fail) {
		this.fail = fail;
	}
	
	/**
	 * @return Returns the parameters.
	 */
	public Object[] getParameters() {
		return parameters;
	}
	
	/**
	 * @param parameters
	 *        The parameters to set.
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @return Returns the stdOut.
	 */
	public String getStdOut() {
		return stdOut;
	}
	
	/**
	 * @param stdOut
	 *        The stdOut to set.
	 */
	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}
	
	public String getParmetersAsString() {
		
		StringBuffer paramsBuf = new StringBuffer();
		
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				paramsBuf.append('"');
				paramsBuf.append(parameters[i].toString());
				paramsBuf.append('"');
				paramsBuf.append(' ');
			}
		}
		
		return paramsBuf.toString();
		
	}
	
	public String getFullCommand() {
		return getCommand() + " " + getParmetersAsString();
	}
	
	@Override
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("Command: " + command + "\n");
		buf.append("Params: " + getParmetersAsString() + "\n");
		buf.append("Fail: " + fail + "\n");
		buf.append("Error: " + getErrorString() + "\n");
		buf.append("ErrorCode: " + getErrorCode() + "\n");
		buf.append("Return: " + getReturnValue() + "\n");
		buf.append("Stdout: " + getStdOut() + "\n");
		return buf.toString();
		
	}
	
}
