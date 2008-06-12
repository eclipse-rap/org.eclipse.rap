/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;


/**
 * An implementation of {@link IEntryPoint} represents the main
 * entry point for a RAP application. It can be compared with the
 * main() method in SWT applications.
 * 
 * @since 1.0
 */
public interface IEntryPoint {
  
  /**
   * This method is called to initiate the application. Normally
   * the display and the corresponding shells are created here.
   * 
   * @return the display to use
   */
  // TODO [rh] adjust JavaDoc
  int createUI();
}
