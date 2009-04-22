/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.presentations.IPresentablePart;


public class ConfigurableStackImpl2 extends ConfigurableStack {

  public ConfigurableStackImpl2() {
  }

  public void init() {
  }

  public void addPart( IPresentablePart newPart, Object cookie ) {
  }

  public void dispose() {
  }

  public Control getControl() {
    return new Composite( getParent(), SWT.NONE );
  }

  public Control[] getTabList( IPresentablePart part ) {
    return null;
  }

  public void removePart( IPresentablePart oldPart ) {
  }

  public void selectPart( IPresentablePart toSelect ) {
  }

  public void setActive( int newState ) {
  }

  public void setBounds( Rectangle bounds ) {
  }

  public void setState( int state ) {
  }

  public void setVisible( boolean isVisible ) {
  }

  public void showPaneMenu() {
  }

  public void showSystemMenu() {
  }
}
