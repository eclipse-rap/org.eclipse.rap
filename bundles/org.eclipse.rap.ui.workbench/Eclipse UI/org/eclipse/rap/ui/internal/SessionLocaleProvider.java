/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.service.ContextProvider;


public final class SessionLocaleProvider implements LocaleProvider {

  public Locale getLocale() {
    Locale result;
    if( ContextProvider.hasContext() ) {
      result = RWT.getLocale();
    } else {
      result = Locale.getDefault();
    }
    return result;
  }

}
