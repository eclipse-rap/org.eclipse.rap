/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit;

import org.eclipse.rap.ui.resources.IResource;


public final class ToggleHyperlinkResource implements IResource {

  public ClassLoader getLoader() {
    return ToggleHyperlinkResource.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/ui/forms/widgets/ToggleHyperlink.js"; //$NON-NLS-1$
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }

}
