/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.FileSettingStore;

// TODO [fappel]: think about how we can provide this as API (subset rule
//                of RAP/RCP)
/**
 * Object representing the session scope in the Eclipse preferences
 * hierarchy. Can be used as a context for searching for preference
 * values (in the IPreferenceService APIs) or for determining the
 * corrent preference node to set values in the store.
 * <p>
 * Session preferences are stored on a <i>per-session</i> basis using
 * the underlying RWT SettingStore (see {@link RWT#getSettingStore()}. 
 * Preferences saved during a previous session will be retrieved, as long as
 * the user can identify himself with the setting store cookie. Session 
 * preferences are persisted using the setting store implementation
 * that is configured for the application (see {@link FileSettingStore}.
 * <p>
 * The path for preferences defined in the session scope hierarchy is: 
 * <code>/session/&lt;qualifier&gt;</code>
 * <p>
 * This class is not intented to be subclassed. It may be instantiated.
 *
 */
public final class SessionScope implements IScopeContext {

  /**
   * String constant (value of <code>"session"</code>) used for the 
   * scope name for the session preference scope.
   */
  public static final String SCOPE = "session"; //$NON-NLS-1$
  
  /**
   * Create and return a new session scope instance.
   */
  public SessionScope() {
      super();
  }

  public IPath getLocation() {
    return null;
  }

  public String getName() {
    return SCOPE;
  }

  public IEclipsePreferences getNode( String qualifier ) {
    ParamCheck.notNull( qualifier, "qualifier" ); //$NON-NLS-1$
    IEclipsePreferences root = Platform.getPreferencesService().getRootNode();
    return ( IEclipsePreferences ) root.node( SCOPE ).node( qualifier );
  }
}
