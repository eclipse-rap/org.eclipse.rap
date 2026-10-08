/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.demo.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.demo.DemoActionBarAdvisor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class FooEditorInput implements IEditorInput {

  public FooEditorInput( final DemoActionBarAdvisor demoActionBarAdvisor ) {
  }

  public boolean exists() {
    return true;
  }

  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  public String getName() {
    return this.hashCode() + ".bar";
  }

  public IPersistableElement getPersistable() {
    return null;
  }

  public String getToolTipText() {
    return "/foo/bar/" + getName();
  }

  public Object getAdapter( final Class adapter ) {
    return null;
  }
}