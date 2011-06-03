/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.internal.widgets.IControlAdapter;


public class ControlSerialization_Test extends TestCase {

  private static class TestControl extends Control {
    private static final long serialVersionUID = 1L;

    TestControl( Composite parent ) {
      super( parent );
    }
  }
  
  private Control control;
  
  public void testControlAdapterIsNotSerializable() throws Exception {
    Control deserializedControl = Fixture.serializeAndDeserialize( control );
    assertNotNull( deserializedControl.getAdapter( IControlAdapter.class ) );
  }
  
  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    control = new TestControl( new Shell( new Display() ) ); 
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
