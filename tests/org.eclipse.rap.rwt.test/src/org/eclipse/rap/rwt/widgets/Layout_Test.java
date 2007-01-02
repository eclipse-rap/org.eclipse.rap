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
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.layout.FillLayout;
import com.w4t.engine.lifecycle.PhaseId;

public class Layout_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testLayoutCall() {
    RWTFixture.fakePhase( PhaseId.PREPARE_UI_ROOT );
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Composite composite = new Composite( shell, RWT.NONE );
    Control control = new Button( composite, RWT.PUSH );
    Rectangle empty = new Rectangle( 0, 0, 0, 0 );
    assertEquals( empty, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    Rectangle shellBounds = new Rectangle( 40, 50, 60, 70 );
    shell.setBounds( shellBounds );
    assertEquals( shellBounds, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    shell.layout();
    assertEquals( shellBounds, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    shell.setLayout( new FillLayout() );
    composite.setLayout( new FillLayout() );
    assertEquals( shellBounds, shell.getBounds() );
    assertEquals( empty, composite.getBounds() );
    assertEquals( empty, control.getBounds() );
    shell.layout();
    assertEquals( shellBounds, shell.getBounds() );
    Rectangle clientArea = shell.getClientArea();
    assertEquals( clientArea, composite.getBounds() );
    Rectangle expected = new Rectangle( 0,
                                        0,
                                        clientArea.width,
                                        clientArea.height );
    assertEquals( expected, control.getBounds() );
  }
}