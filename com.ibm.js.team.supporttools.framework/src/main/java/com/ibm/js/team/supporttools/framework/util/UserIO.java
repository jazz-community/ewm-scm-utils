/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ibm.js.team.supporttools.framework.framework.SupportToolsRuntimeException;

/**
 * Some operations for user I/O
 * 
 * 
 */
public class UserIO {

	public static void prompt(String message) {
		System.out.println(message);
	}

	/**
	 * read a line terminated with return
	 * 
	 * @return
	 * @throws SCMToolsException
	 */
	public static String userInput() {
		// open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String input = null;

		// read from the command-line; need to use try/catch with the readLine()
		// method
		try {
			input = br.readLine();
		} catch (IOException ioe) {
			throw new SupportToolsRuntimeException("Faild to read input");
		}
		return input;
	}

	/**
	 * Split the input message at ';' list the choices and return a choice
	 * 
	 * @param message
	 * @param input
	 * @return
	 */
	public static String userChoiceList(String message, String input) {
		String[] items = input.split(";");
		boolean selected = false;
		String selection = null;
		do {
			prompt(message);
			for (int i = 0; i < items.length; i++) {
				prompt(i + " " + items[i]);
			}
			prompt("enter number ('Q' to quit)");
			String userChoice = userInput();
			if (userChoice.equalsIgnoreCase("q"))
				return selection;
			Integer choice = new Integer(userChoice);
			if (0 <= choice.intValue() && choice.intValue() < items.length)
				selected = true;
			return items[choice.intValue()];
		} while (!selected);

	}

	/**
	 * @param message
	 * @param pattern
	 * @return
	 */
	public static String userChoice(String message, String pattern) {
		prompt(message);
		// Regexp regexp = new Regexp(pattern);

		String choice = null;
		do {
			choice = userInput();
		} while (!choice.matches(pattern));
		return choice;
	}
}
