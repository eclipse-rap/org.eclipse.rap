/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * Typed listener for the <code>Show</code> event.
 * <p>This class is <em>not</em> intended to be used by clients.</p>
 * 
 * @see ShowEvent
 * @see org.eclipse.swt.SWT#Show
 * @since 1.2
 */
public interface ShowListener extends SWTEventListener {

  void controlShown( ShowEvent event );

  void controlHidden( ShowEvent showEvent );
}
