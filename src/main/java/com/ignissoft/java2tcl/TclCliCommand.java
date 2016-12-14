/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl;

import com.aqua.sysobj.conn.CliCommand;

import systemobject.terminal.Prompt;

public class TclCliCommand extends CliCommand {
	
	public TclCliCommand(String command) {
		super();
		setCommands(new String[] { command });
		setPrompts(new Prompt[] { new Prompt("% ", false, true) });
		addErrors("Command not found");
		addErrors("invalid command name");
		setSilent(true);
	}
	
}