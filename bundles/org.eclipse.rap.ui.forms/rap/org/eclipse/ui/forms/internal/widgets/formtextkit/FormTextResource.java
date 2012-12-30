/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import org.eclipse.rap.ui.resources.IResource;


public final class FormTextResource implements IResource {

  public ClassLoader getLoader() {
    return FormTextResource.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/ui/forms/widgets/FormText.js"; //$NON-NLS-1$
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }

}
