/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.internal;


import java.util.EventListener;

/**
 * This interface is the cross-platform version of the
 * java.util.EventListener interface.
 * <p>
 * It is part of our effort to provide support for both J2SE
 * and J2ME platforms. Under this scheme, classes need to 
 * implement SWTEventListener instead of java.util.EventListener.
 * </p>
 * <p>
 * Note: java.util.EventListener is not part of CDC and CLDC.
 * </p>
 */
public interface SWTEventListener extends EventListener, SerializableCompatibility {
}