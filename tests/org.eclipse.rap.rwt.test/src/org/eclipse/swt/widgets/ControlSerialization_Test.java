/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertNotNull;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ControlSerialization_Test {

  private Control control;

  @Before
  public void setUp() {
    Fixture.setUp();
    control = new TestControl( new Shell( new Display() ) );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlAdapterIsNotSerializable() throws Exception {
    Control deserializedControl = serializeAndDeserialize( control );
    assertNotNull( deserializedControl.getAdapter( IControlAdapter.class ) );
  }

  private static class TestControl extends Control {
    TestControl( Composite parent ) {
      super( parent );
      display = parent.getDisplay();
    }
  }

}
