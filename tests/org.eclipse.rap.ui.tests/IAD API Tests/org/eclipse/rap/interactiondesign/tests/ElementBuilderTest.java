/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests;

import org.eclipse.rap.interactiondesign.tests.impl.ElementBuilderImpl;
import org.eclipse.rap.junit.RAPTestCase;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.LayoutRegistry;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public class ElementBuilderTest extends RAPTestCase {
  
  private ElementBuilder builder;
  private LayoutRegistry registry;
  
  protected void setUp() throws Exception {
    if ( builder == null ) {
      Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
      Composite parent = new Composite( shell, SWT.NONE );
      String layoutSetId 
        = "org.eclipse.rap.ui.interactiondesign.test.layoutSet";
      builder = new ElementBuilderImpl( parent, layoutSetId );
    }
    registry = LayoutRegistry.getInstance();
  }
  
  public void testBuilderCreation() {
    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    Composite parent = new Composite( shell, SWT.NONE );
    ElementBuilder eBuilder
      = new ElementBuilderImpl( parent, LayoutRegistry.DEFAULT_LAYOUT_ID );
    ElementBuilderImpl impl = ( ElementBuilderImpl ) eBuilder;
    Composite parentComp = impl.getParentComp();
    assertEquals( parent, parentComp );
    LayoutSet builderLayoutSet = impl.getBuilderLayoutSet();
    String id = builderLayoutSet.getId();
    assertEquals( LayoutRegistry.DEFAULT_LAYOUT_ID, id );
  }
  
  public void testCreateImage() {
    ElementBuilderImpl impl = ( ElementBuilderImpl ) builder;
    Image image = impl.createBuilderImage( "img/configure.png" );
    assertNotNull( image );
    try {
      image = impl.createBuilderImage( "some/path.jpg" );
    } catch( NullPointerException e ) {
      assertNotNull( e );      
    }    
  }
  
  public void testGetImage() {
    Image image = builder.getImage( "conf" );
    assertNotNull( image );
  }
  
  public void testGetColor() {
    Color color = builder.getColor( "color" );
    RGB rgb = color.getRGB();
    RGB newRgb = new RGB( 0, 0, 0 );
    assertEquals( newRgb, rgb );
  }
  
  public void testGetFont() {
    Font font = builder.getFont( "font" );
    assertNotNull( font );
  }
  
  public void testDisposeBuilder() {
    ElementBuilderImpl impl = ( ElementBuilderImpl ) builder;
    String exists = impl.getExists();
    assertNotNull( exists );
    registry.notifyLayoutChanged();
    exists = impl.getExists();
    assertNull( exists );
    
  }

}
