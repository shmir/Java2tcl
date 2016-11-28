/*
 * Created on Mar 17, 2005
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.ignissoft.java2tcl.samples;

import com.aqua.sysobj.conn.CliConnectionImpl;
import com.ignissoft.java2tcl.TclShellRemote;

public class TclShellRemoteSystemObject extends TclShellSystemObject {
	
	public CliConnectionImpl conn;
	
	@Override
	public void init() throws Exception {
		super.init();
		shell = new TclShellRemote(conn, getShellPath());		
	}
	
}
