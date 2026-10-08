/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import org.eclipse.rap.ui.resources.IResource;


public final class HyperlinkAdapterResource implements IResource {

  public ClassLoader getLoader() {
    return HyperlinkAdapterResource.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/ui/forms/widgets/HyperlinkAdapter.js"; //$NON-NLS-1$
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }

}
