/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.actions;

import org.eclipse.core.commands.common.CommandException;

/**
 * Indicates that an action has no command mapping. The declaration can be
 * updated to include a definitionId.
 * 
 * @since 1.1
 */
public class CommandNotMappedException extends CommandException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public CommandNotMappedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CommandNotMappedException(String message, Throwable cause) {
		super(message, cause);
	}
}
