/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.ui.internal.browser;

import java.net.URL;

import org.eclipse.rap.rwt.widgets.ExternalBrowser;
import org.eclipse.ui.browser.AbstractWebBrowser;

// RAP [bm]: Own implementation of default web browser
public final class DefaultWebBrowser extends AbstractWebBrowser {

  private final DefaultWorkbenchBrowserSupport support;
  private final int style;

  public DefaultWebBrowser( final DefaultWorkbenchBrowserSupport support,
                            final String id,
                            final int style )
  {
    super( id );
    this.support = support;
    this.style = style;
  }

  public void openURL( final URL url ) {
    ExternalBrowser.open( getId(), url.toExternalForm(), style );
  }

  public boolean close() {
    ExternalBrowser.close( getId() );
    support.unregisterBrowser( this );
    return true;
  }
}
