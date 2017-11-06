/**
 * @author yoram@ignissoft.com
 */
package com.ignissoft.java2tcl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TclShellUtils {
	
	public static String handelShellCommand(TclShell shell, ShellCommand cmd) {
		shell.executeCommand(cmd);
		return (cmd.isFail()) ? null : cmd.getReturnValue();
	}
	
	public static String setVar(TclShell shell, String name, String value) {
		return handelShellCommand(shell, new ShellCommand("set " + name + " " + value));
	}
	
	public static String getVar(TclShell shell, String name) {
		return handelShellCommand(shell, new ShellCommand("set dummy $" + name.replace("$", "")));
	}
	
	public static String setArrayEntry(TclShell shell, String array, String key, String value) {
		return setVar(shell, array + "(" + key + ")", "{" + value + "}");
	}
	
	public static String unsetArrayEntry(TclShell shell, String array, String key) {
		return handelShellCommand(shell, new ShellCommand("array unset " + array + " " + key));
	}
	
	public static String setArray(TclShell shell, String array, String list) {
		return handelShellCommand(shell, new ShellCommand("array set " + array + " {" + list + "}"));
	}
	
	public static String setList(TclShell shell, String list, String entries) {
		setVar(shell, "{" + list + "}", " {" + entries + "}");
		return handelShellCommand(shell, new ShellCommand("set returnValue $" + list));
	}
	
	public static String searchList(TclShell shell, String list, String value, boolean inLine, boolean all, boolean regExp, int index) {
		String options = (regExp ? "-regexp" : "-exact")
				+ " "
				+ (inLine ? "-inline" : "")
				+ " "
				+ (all ? "-all" : "")
				+ " "
				+ (index >= 0 ? "-index " + index : "")
				+ " ";
		return handelShellCommand(shell, new ShellCommand("lsearch " + options + "$" + list, value));
	}
	
	public static String searchList(TclShell shell, String list, String value, boolean inLine, boolean all, boolean regExp) {
		return searchList(shell, list, value, inLine, all, regExp, -1);
	}
	
	public static int searchList(TclShell shell, String list, String regExp) {
		return Integer.parseInt(searchList(shell, list, regExp, false, false, false));
	}
	
	public static String getListVal(TclShell shell, String list, int index) {
		return handelShellCommand(shell, new ShellCommand("lindex", "$" + list, index));
	}
	
	public static String getListVal(TclShell shell, String list, String regExp) {
		return getListVal(shell, list, searchList(shell, list, regExp));
	}
	
	public static int getListLength(TclShell shell, String list) {
		return Integer.parseInt(handelShellCommand(shell, new ShellCommand("llength", "$" + list)));
	}
	
	public static void getArray(TclShell shell, String array, String entries) {
		handelShellCommand(shell, new ShellCommand("array unset " + array + "; array set " + array + " {" + entries + "}"));
		// Following GET is just for debug
		if (shell.isPrintReturn()) {
			handelShellCommand(shell, new ShellCommand("array", "get", array));
		}
	}
	
	public static String getArrayVal(TclShell shell, String array, String key) {
		return handelShellCommand(shell, new ShellCommand("set returnValue $" + array + "(" + key + ")"));
	}
	
	public static ArrayList<String> tclList2JavaList(TclShell shell, String list) {
		if (list == null || list.equals("")) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(Arrays.asList(TclShellUtils.eval(shell, "join {" + list + "} \\t").split("\\t")));
	}
	
	public static void source(TclShell shell, String file) {
		handelShellCommand(shell, new ShellCommand("source", file));
	}
	
	public static void after(TclShell shell, int seconds) {
		handelShellCommand(shell, new ShellCommand("after", seconds * 1000));
	}
	
	public static void puts(TclShell shell, String message) {
		handelShellCommand(shell, new ShellCommand("puts \"" + message + "\""));
	}
	
	public static String eval(TclShell shell, String command) {
		return handelShellCommand(shell, new ShellCommand("eval {" + command + "}"));
	}
	
	public static Object[] buildPropertiesString(HashMap<String, Object> properties) {
		ArrayList<String> propertiesS = new ArrayList<String>();
		for (String key : properties.keySet()) {
			propertiesS.add("-" + key);
			propertiesS.add(properties.get(key).toString());
		}
		return propertiesS.toArray();
	}
	
	public static boolean tcl2javaBoolean(String tclBool) {
		if (tclBool.equals("1") || tclBool.equals("True") || tclBool.equals("true")) {
			return true;
		}
		return false;
	}
	
	public static HashMap<String, String> list2JavaHashMap(TclShell shell, String list) {
		getArray(shell, "list2JavaHashMapArray", list);
		HashMap<String, String> hm = new HashMap<String, String>();
		for (String name : TclShellUtils.handelShellCommand(shell, new ShellCommand("array names list2JavaHashMapArray")).split(" ")) {
			hm.put(name, TclShellUtils.getArrayVal(shell, "list2JavaHashMapArray", name));
		}
		return hm;
	}
	
}
