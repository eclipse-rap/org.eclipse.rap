/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.lifecycle;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.widgets.*;

public class WidgetUtil_Test extends TestCase {

  public void testGetAdapter() {
    RWTFixture.deregisterAdapterFactories();
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    try {
      WidgetUtil.getAdapter( shell );
      fail( "Must throw exception if no adapter could be found." );
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testHasChanged() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Text text = new Text( shell, RWT.NONE );
    // test initial behaviour, text is same as default value -> no 'change'
    text.setText( "" );
    boolean hasChanged;
    hasChanged = WidgetUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( false, hasChanged );
    // test initial behaviour, text is different as default value -> 'change'
    text.setText( "other value" );
    hasChanged = WidgetUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( true, hasChanged );
    // test subsequent behaviour (when already initialized)
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    hasChanged = WidgetUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( false, hasChanged );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    text.setText( "whatsoevervaluehasbeensetduringrequest" );
    hasChanged = WidgetUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( true, hasChanged );
  }
  
  public void testHasChangedWidthArrays() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    List list = new List( shell, RWT.MULTI );
    
    boolean hasChanged;
    hasChanged = WidgetUtil.hasChanged( list, "items", new String[] { "a" } );
    assertEquals( true, hasChanged );
    
    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetUtil.hasChanged( list, "items", new String[] { "a" } );
    assertEquals( false, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetUtil.hasChanged( list, "items", new String[] { "b" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged 
      = WidgetUtil.hasChanged( list, "items", new String[] { "a", "b" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetUtil.hasChanged( list, "items", null );
    assertEquals( true, hasChanged );
    
    list.setItems( new String[] { "a", "b", "c" } );
    list.setSelection( new int[] { 0, 1, 2 } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetUtil.hasChanged( list, "selection", new int[] { 0, 1, 4 } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a", "b", "c" } );
    list.setSelection( new int[] { 0, 1, 2 } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetUtil.hasChanged( list, "selection", new int[] { 0, 1, 2 } );
    assertEquals( false, hasChanged );
  }
  
  public void testEquals() {
    assertTrue( WidgetUtil.equals( null, null ) );
    assertFalse( WidgetUtil.equals( null, "1" ) );
    assertFalse( WidgetUtil.equals( "1", null ) );
    assertFalse( WidgetUtil.equals( "1", "2" ) );
    assertTrue( WidgetUtil.equals( "1", "1" ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
