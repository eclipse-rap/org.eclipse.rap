/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CompositeSerialization_Test {

  private Composite composite;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell parent = new Shell( display );
    composite = new Composite( parent, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testChildrenAreSerializable() throws Exception {
    new TestControl( composite );

    Composite deserializedComposite = Fixture.serializeAndDeserialize( composite );
    attachToTestThread( deserializedComposite );

    assertEquals( 1, deserializedComposite.getChildren().length );
  }

  private static void attachToTestThread( Widget widget ) {
    Display display = widget.getDisplay();
    IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
    adapter.attachThread();
  }

  private static class TestControl extends Control {
    TestControl( Composite parent ) {
      super( parent, 0 );
    }
  }

}
