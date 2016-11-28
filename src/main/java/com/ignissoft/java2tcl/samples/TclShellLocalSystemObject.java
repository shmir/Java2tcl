/*
 * Created on Mar 17, 2005
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.ignissoft.java2tcl.samples;

import java.io.File;

import com.ignissoft.java2tcl.TclShellLocal;

public class TclShellLocalSystemObject extends TclShellSystemObject {
	
	@Override
	public void init() throws Exception {
		super.init();
		shell = new TclShellLocal(new File(getShellPath()));		
	}
	
}
