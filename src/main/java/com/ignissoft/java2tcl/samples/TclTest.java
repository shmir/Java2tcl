/*
 * Created on 08/05/2005
 */
package com.ignissoft.java2tcl.samples;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aqua.sysobj.conn.WindowsDefaultCliConnection;
import com.ignissoft.java2tcl.ShellCommand;
import com.ignissoft.java2tcl.TclShell;
import com.ignissoft.java2tcl.TclShellLocal;
import com.ignissoft.java2tcl.TclShellRemote;

public class TclTest {
	
	TclShell shell;
	
	@Before
	public void setUp() throws Exception {
		shell = new TclShellLocal(new File("C:/Tcl8532/bin/wish85.exe"));
		WindowsDefaultCliConnection conn = new WindowsDefaultCliConnection();
		conn.setHost("localhost");
		conn.setUser("bgp2");
		conn.setPassword("bgp211");
		conn.connect();
		shell = new TclShellRemote(conn, "C:/Tcl8532/bin/tclsh85.exe");
		shell.launch();
		shell.setPrintCommand(true);
		shell.setPrintReturn(true);
	}

	@After
	public void tearDown() {
		shell.exit();
	}
	
	@Test
	public void testHelloWorld() throws IOException {
		ShellCommand cmd = new ShellCommand("puts", "Hello World 1");
		shell.executeCommand(cmd);
	}
	
	@Test
	public void testShellCommand() throws IOException {
		
		ShellCommand cmd = new ShellCommand();
		cmd.setCommand("sett dummy");
		cmd.setParameters(new String[] { "Hello World 1" });
		shell.executeCommand(cmd);
		assertTrue("ShellCommand constructor with no parameters failed", cmd.getReturnValue().equals("Hello World 1"));
		
		cmd = new ShellCommand("set dummy", "Hello World 2");
		shell.executeCommand(cmd);
		assertTrue("ShellCommand constructor with parameters failed", cmd.getReturnValue().indexOf("Hello World 2") >= 0);

	}
	
}
