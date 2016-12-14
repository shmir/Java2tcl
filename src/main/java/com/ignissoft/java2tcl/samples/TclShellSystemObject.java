/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl.samples;

import com.ignissoft.java2tcl.TclShellImpl;

import jsystem.framework.system.SystemObjectImpl;

public class TclShellSystemObject extends SystemObjectImpl {
	
	private String shellPath;
	
	protected TclShellImpl shell = null;
	
	public TclShellImpl getShell() {
		return shell;
	}
	
	public String getShellPath() {
		return shellPath;
	}
	
	public void setShellPath(String shellPath) {
		this.shellPath = shellPath;
	}
	
}
