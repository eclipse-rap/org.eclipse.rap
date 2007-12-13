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
package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class WidgetUtil_Test extends TestCase {

  public void testFind() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Composite composite = new Composite( shell, SWT.NONE );
    Button button = new Button( composite, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String compositeId = WidgetUtil.getId( composite );
    String buttonId = WidgetUtil.getId( button );
    
    assertSame( composite, WidgetUtil.find( composite, compositeId ) ); 
    assertSame( button, WidgetUtil.find( composite, buttonId ) );
    assertSame( composite, WidgetUtil.find( composite, compositeId ) );
    assertNull( WidgetUtil.find( composite, shellId ) );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
