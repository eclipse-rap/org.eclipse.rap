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

package org.eclipse.rap.rwt.events;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.*;

public class SelectionEvent_Test extends TestCase {

  private static final String WIDGET_SELECTED = "widgetSelected|";
  private String log = "";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testButtonAddRemoveListener() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    final Button button = new Button( shell, RWT.PUSH );
    button.addSelectionListener( new SelectionListener() {

      public void widgetSelected( final SelectionEvent event ) {
        assertSame( button, event.getSource() );
        assertNull( event.item );
        assertEquals( 10, event.x );
        assertEquals( 20, event.y );
        assertEquals( 30, event.width );
        assertEquals( 40, event.height );
        assertEquals( true, event.doit );
        log += WIDGET_SELECTED;
      }
    } );
    SelectionEvent event = new SelectionEvent( button,
                                               null,
                                               SelectionEvent.WIDGET_SELECTED,
                                               new Rectangle( 10, 20, 30, 40 ),
                                               true,
                                               RWT.NONE );
    event.processEvent();
    assertEquals( WIDGET_SELECTED, log );
  }

  public void testTabFolderAddRemoveListener() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    final TabFolder folder = new TabFolder( shell, RWT.NONE );
    final TabItem item = new TabItem( folder, RWT.NONE );
    Composite composite = new Composite( folder, RWT.NONE );
    item.setControl( composite );
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionListener() {

      public void widgetSelected( SelectionEvent event ) {
        assertSame( folder, event.getSource() );
        assertSame( item, event.item );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log += WIDGET_SELECTED;
      }
    } );
    SelectionEvent event = new SelectionEvent( folder,
                                               item,
                                               SelectionEvent.WIDGET_SELECTED,
                                               new Rectangle( 0, 0, 0, 0 ),
                                               true,
                                               RWT.NONE );
    event.processEvent();
    assertEquals( WIDGET_SELECTED, log );
  }
}
