/*
 * Created on Jan 23, 2006
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.ignissoft.java2tcl;

import com.aqua.sysobj.conn.CliCommand;

public class TclCliCommand extends CliCommand {
	
	public TclCliCommand(String command) {
		super();
		setCommands(new String[] { command });
		addErrors("Command not found");		
		addErrors("invalid command name");		
	}
	
}