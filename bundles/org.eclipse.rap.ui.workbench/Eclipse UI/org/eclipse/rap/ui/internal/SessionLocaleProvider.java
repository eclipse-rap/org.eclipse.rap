/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal;

import java.util.Locale;

import org.eclipse.osgi.service.localization.LocaleProvider;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.UISession;


public final class SessionLocaleProvider implements LocaleProvider {

  @Override
  public Locale getLocale() {
    if( ContextProvider.hasContext() ) {
      UISession uiSession = ContextProvider.getUISession();
      if( uiSession != null ) {
        return uiSession.getLocale();
      }
    }
    return Locale.getDefault();
  }

}
