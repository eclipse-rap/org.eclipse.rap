/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.demo.presentation;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public abstract class NavigationPaneContent {
  private Control control;
  private Object selector;
  
  public abstract void createControl( Composite parent );
  public abstract String getLabel();

  public ISelectionProvider getSelectionProvider() {
    return null;
  }
  
  public boolean isSelectionProvider() {
    return false;
  }
  
  final void setControl( final Control control ) {
    this.control = control;
  }
  
  final Control getControl() {
    return control;
  }
  
  final Object getSelector() {
    return selector;
  }
  
  final void setSelector( final Object selector ) {
    this.selector = selector;
  }
}
