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

package org.eclipse.rap.rwt.internal.widgets.controlkit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.ControlEvent;
import org.eclipse.rap.rwt.events.ControlListener;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.*;


public class ControlLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUpWithoutResourceManager();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    ControlListener controlListener = new ControlListener() {
      public void controlMoved( final ControlEvent event ) {
      }
      public void controlResized( final ControlEvent event ) {
      }
    };
    shell.addControlListener( controlListener );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertEquals( adapter.getPreserved( Props.BOUNDS ), shell.getBounds() );
    Boolean asLlisteners;
    asLlisteners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, asLlisteners );
  }
}
