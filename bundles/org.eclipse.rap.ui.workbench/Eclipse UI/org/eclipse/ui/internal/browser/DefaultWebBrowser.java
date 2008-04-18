/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.browser;

import java.net.URL;

import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.ui.browser.AbstractWebBrowser;

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
