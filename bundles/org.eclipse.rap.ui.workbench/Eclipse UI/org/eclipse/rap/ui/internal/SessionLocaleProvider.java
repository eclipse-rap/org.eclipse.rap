/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
