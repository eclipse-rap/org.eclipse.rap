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
package org.eclipse.rap.rwt.custom;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.custom.CTabFolder;
import org.eclipse.rap.rwt.custom.CTabItem;
import org.eclipse.rap.rwt.widgets.*;


public class CTabItem_Test extends TestCase {

  public void testInitialState() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    CTabItem item = new CTabItem( folder, RWT.NONE );

    assertEquals( null, item.getToolTipText() );
    assertEquals( "", item.getText() );
    assertEquals( null, item.getControl() );
    assertEquals( null, item.getImage() );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    CTabItem item1 = new CTabItem( folder, RWT.NONE );
    assertEquals( RWT.NONE, item1.getStyle() );
    
    CTabItem item2 = new CTabItem( folder, -1 );
    assertEquals( RWT.NONE, item2.getStyle() );
    
    CTabItem item3 = new CTabItem( folder, RWT.CLOSE );
    assertEquals( RWT.CLOSE, item3.getStyle() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
