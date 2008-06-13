/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScope;

/**
 * Creates "session" scoped preference nodes.
 */
public final class SessionPreferencesFactory implements IScope {

  public IEclipsePreferences create( final IEclipsePreferences parent, 
                                     final String name ) {
    return new SessionPreferencesNode( parent, name );
  }
  
}
