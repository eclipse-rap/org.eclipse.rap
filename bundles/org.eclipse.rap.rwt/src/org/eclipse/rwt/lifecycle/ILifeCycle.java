/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
 * <p>This interface represents the lifecycle of a request.</p>
 * <p>This interface is not inteded to be implemented by clients.</p>
 */
// TODO: [fappel] write lifecycle documentation in package.html
public interface ILifeCycle {

  /**
   * <p>Registers a <code>PhaseListener</code> with the lifecycle</p>
   * @param listener the listener to be added, must not be <code>null</code>.
   */
  void addPhaseListener( PhaseListener listener );

  /**
   * <p>Removes a <code>PhaseListener</code> from the lifecycle. Has no effect
   * if an identical listener is not already registered.</p>
   * @param listener the listener to be removed, must not be <code>null</code>.
   */
  void removePhaseListener( PhaseListener listener );
}