/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl.samples;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ignissoft.java2tcl.ShellCommand;

import junit.framework.SystemTestCase4;

public class TclTest extends SystemTestCase4 {
	
	TclShellSystemObject tcl;
	
	@Before
	public void setUp() throws Exception {
		tcl = (TclShellSystemObject) system.getSystemObject("tcl");
		tcl.getShell().launch();
		tcl.getShell().setPrintCommand(true);
		tcl.getShell().setPrintReturn(true);
	}
	
	@After
	public void tearDown() {
		tcl.getShell().exit();
	}
	
	@Test
	public void testHelloWorld() throws IOException {
		ShellCommand cmd = new ShellCommand("puts", "Hello World 1");
		tcl.getShell().executeCommand(cmd);
	}
	
	@Test
	public void testShellCommand() throws IOException {
		
		ShellCommand cmd = new ShellCommand();
		cmd.setCommand("set dummy");
		cmd.setParameters(new String[] { "Hello World 1" });
		tcl.getShell().executeCommand(cmd);
		assertTrue("ShellCommand constructor with no parameters failed", cmd.getReturnValue().equals("Hello World 1"));
		
		cmd = new ShellCommand("set dummy", "Hello World 2");
		tcl.getShell().executeCommand(cmd);
		assertTrue("ShellCommand constructor with parameters failed", cmd.getReturnValue().indexOf("Hello World 2") >= 0);
		
	}
	
}
