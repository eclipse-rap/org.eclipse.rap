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

package org.eclipse.swt.internal.events;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * Typed listener for the <code>Activate</code> and <code>Deactivate</code>
 * events.
 * <p>This class is <em>not</em> intended to be used by clients.</p>
 * 
 * @see ActivateAdapter
 * @see ActivateEvent
 * @see SWT#Activate
 * @see SWT#Deactivate
 * @since 1.0
 */
public interface ActivateListener extends SWTEventListener {

  void activated( ActivateEvent event );
  void deactivated( ActivateEvent event );
}
