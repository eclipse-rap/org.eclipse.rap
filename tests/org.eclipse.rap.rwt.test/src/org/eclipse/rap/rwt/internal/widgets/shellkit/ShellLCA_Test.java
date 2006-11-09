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

package org.eclipse.rap.rwt.internal.widgets.shellkit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.ShellEvent;
import org.eclipse.rap.rwt.events.ShellListener;
import org.eclipse.rap.rwt.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Display;
import org.eclipse.rap.rwt.widgets.Shell;
import com.w4t.Fixture;


public class ShellLCA_Test extends TestCase {
  
  public void testReadData() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    shell.addShellListener( new ShellListener() {
      public void shellClosed( final ShellEvent event ) {
        log.append( "closed" );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    new ShellLCA().readData( shell );
    assertEquals( "closed", log.toString() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
