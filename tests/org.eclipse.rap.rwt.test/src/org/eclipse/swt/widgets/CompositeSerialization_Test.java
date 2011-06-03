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
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;


public class CompositeSerialization_Test extends TestCase {

  private static class TestControl extends Control {
    private static final long serialVersionUID = 1L;

    TestControl( Composite parent ) {
      super( parent, 0 );
    }
  }
  
  private Composite composite;
  
  public void testChildrenAreSerializable() throws Exception {
    new TestControl( composite );
    
    Composite deserializedComposite = Fixture.serializeAndDeserialize( composite );
    attachToTestThread( deserializedComposite );
    
    assertEquals( 1, deserializedComposite.getChildren().length );
  }

  private static void attachToTestThread( Widget widget ) {
    Display display = widget.getDisplay();
    IDisplayAdapter adapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    adapter.attachThread();
  }
  
  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    Shell parent = new Shell( display );
    composite = new Composite( parent, SWT.NONE ); 
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
