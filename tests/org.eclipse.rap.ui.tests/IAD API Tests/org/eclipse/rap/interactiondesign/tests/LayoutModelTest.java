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

import java.util.Map;

import org.eclipse.rap.junit.RAPTestCase;
import org.eclipse.rap.ui.interactiondesign.layout.model.Layout;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;


public class LayoutModelTest extends RAPTestCase {
  
  private Layout layout;
  private LayoutSet set;

  protected void setUp() throws Exception {
    layout = new Layout( "org.eclipse.rap.layout" );
    set = new LayoutSet( "org.eclipse.rap.layoutset" );
  }
  
  public void testLayoutConstructor() {
    String id = "org.eclipse.layout";
    Layout layout = new Layout( id );
    assertNotNull( layout );
    String layoutId = layout.getId();
    assertEquals( id, layoutId );
  }
  
  public void testLayoutSetConstructor() {
    String id = "org.eclipse.layoutset";
    LayoutSet set = new LayoutSet( id );
    assertNotNull( set );
    String setId = set.getId();
    assertEquals( id, setId );    
  }
  
  public void testAddLayoutSet() {
    layout.addLayoutSet( set );
    LayoutSet layoutSet = layout.getLayoutSet( set.getId() );
    assertEquals( set, layoutSet );
  }
  
  public void testClearLayoutSet() {
    set.addImagePath( "key", "imagePath" );
    layout.addLayoutSet( set );
    layout.clearLayoutSet( set.getId() );
    LayoutSet layoutSet = layout.getLayoutSet( set.getId() );
    assertFalse( set.equals( layoutSet ) );
  }
  
  public void testGetLayoutSet() {
    layout.addLayoutSet( set );
    String id = "some.other.id";
    LayoutSet newSet = new LayoutSet( id );
    newSet.addImagePath( "key", "image" );
    LayoutSet layoutSet = layout.getLayoutSet( set.getId() );
    assertEquals( set, layoutSet );
    LayoutSet layoutSet2 = layout.getLayoutSet( id );
    assertFalse( newSet.equals( layoutSet2 ) );    
  }
  
  public void testGetLayoutSets() {
    layout.addLayoutSet( set );
    layout.addLayoutSet( new LayoutSet( "org.id" ) );
    layout.addLayoutSet( new LayoutSet( "org.id.2" ) );
    Map layoutSets = layout.getLayoutSets();
    assertEquals( 3, layoutSets.size() );
    layout.clearLayoutSet( set.getId() );
    layoutSets = layout.getLayoutSets();
    assertEquals( 2, layoutSets.size() );
  }
  
  public void testLayoutSetExists() {
    boolean exists = layout.layoutSetExist( set.getId() );
    assertFalse( exists );
    layout.addLayoutSet( set );
    exists = layout.layoutSetExist( set.getId() );
    assertTrue( exists );
  }
  
  public void testColor() {
    Color color = Graphics.getColor( 0, 0, 0 );
    set.addColor( "key", color );
    Color newColor = set.getColor( "key" );
    assertEquals( color, newColor );
  }
  
  public void testFont() {
    Font font = Graphics.getFont( "Arial", 12, SWT.BOLD );
    set.addFont( "key", font );
    Font newFont = set.getFont( "key" );
    assertEquals( font, newFont );
  }
  
  public void testPosition() {
    FormData fData = new FormData();
    fData.height = 100;
    fData.left = new FormAttachment( 0, 0 );
    set.addPosition( "key", fData );
    FormData newFData = set.getPosition( "key" );
    assertEquals( fData, newFData );
  }
  
  public void testImagePath() {
    String imagePath = "/path/image.gif";
    set.addImagePath( "key", imagePath );
    String newImagePath = set.getImagePath( "key" );
    assertEquals( imagePath, newImagePath );
  }
    
}
