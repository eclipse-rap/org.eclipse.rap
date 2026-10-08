/*******************************************************************************
 * Copyright (c) 2008, 2012 EclipseSource and others.
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
package org.eclipse.rap.internal.design.example.builder;

import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class DummyBuilder extends ElementBuilder {

  public DummyBuilder( final Composite parent, final String subSetId ) {
    super( parent, subSetId );
    
  }

  public void addControl( final Control control, final Object layoutData ) {
  }

  public void addControl( final Control control, final String positionId ) {
    
  }

  public void addImage( final Image image, final Object layoutData ) {
    
  }

  public void addImage( final Image image, final String positionId ) {
    
  }

  public void build() {
    
  }

  public void dispose() {
    
  }

  public Control getControl() {
    
    return null;
  }

  public Point getSize() {
    
    return null;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == LayoutSet.class ) {
      result = getLayoutSet();
    }
    return result;
  }
}
