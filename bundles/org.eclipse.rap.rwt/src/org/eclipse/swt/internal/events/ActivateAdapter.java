/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.events;


/**
 * This adapter class provides default implementations for the
 * methods described by the <code>ActivateListener</code> interface.
 * 
 * <p>This class is <em>not</em> intended to be used by clients.</p>
 * 
 * @see ActivateListener
 * @see ActivateEvent  
 * @since 1.0
 */
public abstract class ActivateAdapter implements ActivateListener {

  public void activated( final ActivateEvent event ) {
  }

  public void deactivated( final ActivateEvent event ) {
  }
}
