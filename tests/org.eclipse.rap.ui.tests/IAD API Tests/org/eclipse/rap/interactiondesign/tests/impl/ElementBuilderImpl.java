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

import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class ElementBuilderImpl extends ElementBuilder {
  
  private String exists;

  
  public String getExists() {
    return exists;
  }

  public ElementBuilderImpl( Composite parent, String layoutSetId ) {
    super( parent, layoutSetId );   
    exists = "";
  }

  public void addControl( Control control, Object layoutData ) {
  }

  public void addControl( Control control, String positionId ) {
  }

  public void addImage( Image image, Object layoutData ) {
  }

  public void addImage( Image image, String positionId ) {
  }

  public void build() {
  }

  public void dispose() {
    exists = null;
  }

  public Control getControl() {
    return null;
  }

  public Point getSize() {
    return null;
  }
  
  public Composite getParentComp() {
    return getParent();
  }
  
  public LayoutSet getBuilderLayoutSet() {
    return getLayoutSet();
  }
  
  public Image createBuilderImage( final String path) {
    return createImage( path );
  }
}
