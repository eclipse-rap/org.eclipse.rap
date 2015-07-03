/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.controlkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getLCA;
import static org.eclipse.rap.rwt.testfixture.internal.Fixture.getProtocolMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public class ControlLCATestUtil {

  public static void testCommonControlProperties( Control control ) throws IOException {
    testRendersChangedControlProperties( control );
    testDoesNotRenderPreservedValues( control );
  }

  private static void testRendersChangedControlProperties( Control control ) throws IOException {
    Fixture.fakeNewRequest();
    Fixture.clearPreserved();
    Fixture.markInitialized( control );
    AbstractWidgetLCA lca = getLCA( control );
    Display display = control.getDisplay();
    control.getShell().open();
    control.setSize( 16, 16 );

    lca.preserveValues( control );
    control.setEnabled( false );
    control.setBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( control );

    TestMessage protocolMessage = getProtocolMessage();
    assertNotNull( protocolMessage.findSetOperation( control, "enabled" ) );
    assertNotNull( protocolMessage.findSetOperation( control, "background" ) );
  }

  private static void testDoesNotRenderPreservedValues( Control control ) throws IOException {
    Fixture.fakeNewRequest();
    Fixture.clearPreserved();
    Fixture.markInitialized( control );
    AbstractWidgetLCA lca = getLCA( control );

    lca.preserveValues( control );
    lca.renderChanges( control );

    assertEquals( 0, getProtocolMessage().getOperationCount() );
  }

}
