/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;



public final class HyperlinkResource implements IResource {

  public String getCharset() {
    return "ISO-8859-1"; //$NON-NLS-1$
  }

  public ClassLoader getLoader() {
    return HyperlinkResource.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/ui/forms/widgets/Hyperlink.js"; //$NON-NLS-1$
  }

  public RegisterOptions getOptions() {
    return RegisterOptions.VERSION_AND_COMPRESS;
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }
}
