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

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import com.w4t.engine.lifecycle.PhaseId;

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
    Composite shell = new Shell( display , RWT.NONE );
    // add different controls to the shell
    Control button = new Button( shell, RWT.PUSH );
    Control link = new Link( shell, RWT.NONE );
    new Label( shell, RWT.NONE );
    new Sash( shell, RWT.HORIZONTAL );
    Combo combo = new Combo( shell, RWT.DROP_DOWN );
    Composite composite = new Composite( shell, RWT.NONE );
    List list = new List( shell, RWT.NONE );
    Text text = new Text( shell, RWT.NONE );
    Control[] controls = shell.getTabList();
    // check that the right ones are in the list
    assertEquals( 6, controls.length );
    assertEquals( button, controls[ 0 ] );
    assertEquals( link, controls[ 1 ] );
    assertEquals( combo, controls[ 2 ] );
    assertEquals( composite, controls[ 3 ] );
    assertEquals( list, controls[ 4 ] );
    assertEquals( text, controls[ 5 ] );
  }

}
