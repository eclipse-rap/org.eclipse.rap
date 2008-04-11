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

package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;

public class Composite_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testTabList() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    // add different controls to the shell
    Control button = new Button( shell, SWT.PUSH );
    Control link = new Link( shell, SWT.NONE );
    new Label( shell, SWT.NONE );
    new Sash( shell, SWT.HORIZONTAL );
    Combo combo = new Combo( shell, SWT.DROP_DOWN );
    Composite composite = new Composite( shell, SWT.NONE );
    List list = new List( shell, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    Control[] controls = shell.getTabList();
    // check that the right ones are in the list
    assertEquals( 6, controls.length );
    assertEquals( button, controls[ 0 ] );
    assertEquals( link, controls[ 1 ] );
    assertEquals( combo, controls[ 2 ] );
    assertEquals( composite, controls[ 3 ] );
    assertEquals( list, controls[ 4 ] );
    assertEquals( text, controls[ 5 ] );
    // A once manually set tabList doesn't change when new controls are created
    Composite group = new Composite( shell, SWT.NONE );
    Text text1 = new Text( group, SWT.NONE );
    Text text2 = new Text( group, SWT.NONE );
    Control[] tabList = new Control[] { text2, text1 };
    group.setTabList( tabList );
    new Text( group, SWT.NONE );
    assertEquals( 2, group.getTabList().length );
    assertSame( text2, group.getTabList()[ 0 ] );
    assertSame( text1, group.getTabList()[ 1 ] );
  }
  
  public void testLayout() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    GridLayout gridLayout = new GridLayout();
    shell.setLayout( gridLayout );
    assertSame( gridLayout, shell.getLayout() );
    RowLayout rowLayout = new RowLayout();
    shell.setLayout( rowLayout );
    assertSame( rowLayout, shell.getLayout() );
  }
  
  public void testBackgroundMode() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    IControlAdapter adapter 
      = ( IControlAdapter )button.getAdapter( IControlAdapter.class );
    shell.setBackgroundMode( SWT.INHERIT_NONE );
    assertEquals( SWT.INHERIT_NONE, shell.getBackgroundMode() );
    assertFalse( adapter.getBackgroundTransparency() );
    shell.setBackgroundMode( SWT.INHERIT_DEFAULT );
    assertEquals( SWT.INHERIT_DEFAULT, shell.getBackgroundMode() );
    assertFalse( adapter.getBackgroundTransparency() );
    shell.setBackgroundMode( SWT.INHERIT_FORCE );
    assertEquals( SWT.INHERIT_FORCE, shell.getBackgroundMode() );
    assertTrue( adapter.getBackgroundTransparency() );
  }
}
