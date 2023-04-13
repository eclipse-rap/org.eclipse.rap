/*******************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.eclipse.rap.rwt.application.Application.OperationMode.JEE_COMPATIBILITY;
import static org.eclipse.rap.rwt.application.Application.OperationMode.SWT_COMPATIBILITY;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class Clipboard_Test {
  
  private static final String REMOTE_TYPE = "rwt.client.Clipboard";
  private Connection connection;
  private RemoteObject remoteObject;
  
  @Rule
  public TestContext context = new TestContext();
  
  private Clipboard clipboard;
  
  @Before
  public void setUp() {
    Display display = new Display();
    connection = mock( Connection.class );
    remoteObject = mock( RemoteObject.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    context.replaceConnection( connection );
    clipboard = new Clipboard( display );
  }
  
  @Test
  public void testConstructor_createsRemoteObject() {
    verify( connection ).createRemoteObject( eq( REMOTE_TYPE ) );
  }
  
  @Test
  public void testWriteText() {
    clipboard.writeText( "foo" );
    
    verify( remoteObject ).call( "writeText", new JsonObject().add( "text", "foo" ) );
  }
  
  @Test
  public void testReadText() {
    clipboard.readText();
    
    verify( remoteObject ).call( "readText", null );
  }
  
  @Test
  public void testSetContents_JEE_COMPATIBILITY() {
    ensureOperationMode( JEE_COMPATIBILITY );
    try {
      Object[] data = new Object[] { "foo" };
      Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
      clipboard.setContents( data, transfers );
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }
  
  @Test( expected = IllegalArgumentException.class )
  public void testSetContents_nonTextTransfer() {
    ensureOperationMode( SWT_COMPATIBILITY );
    
    Object[] data = new Object[] { "foo" };
    Transfer[] transfers = new Transfer[] { ImageTransfer.getInstance() };
    clipboard.setContents( data, transfers );
  }
  
  @Test( expected = IllegalArgumentException.class )
  public void testSetContents_multipleTransfers() {
    ensureOperationMode( SWT_COMPATIBILITY );
    
    Object[] data = new Object[] { "foo", "bar" };
    Transfer[] transfers = new Transfer[] { 
      TextTransfer.getInstance(), 
      TextTransfer.getInstance() 
    };
    clipboard.setContents( data, transfers );
  }
  
  @Test
  public void testGetContents_JEE_COMPATIBILITY() {
    ensureOperationMode( JEE_COMPATIBILITY );
    try {
      clipboard.getContents( TextTransfer.getInstance() );
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }
  
  @Test( expected = IllegalArgumentException.class )
  public void testGetContents_nonTextTransfer() {
    ensureOperationMode( SWT_COMPATIBILITY );
    
    clipboard.getContents( ImageTransfer.getInstance() );
  }
  
  @Test
  public void testDispose() {
    clipboard.dispose();
    
    assertTrue( clipboard.isDisposed() );
  }
  
  private static void ensureOperationMode( OperationMode operationMode ) {
    LifeCycleFactory lifeCycleFactory = getApplicationContext().getLifeCycleFactory();
    lifeCycleFactory.deactivate();
    if( SWT_COMPATIBILITY.equals( operationMode ) ) {
      lifeCycleFactory.configure( RWTLifeCycle.class );
    }
    lifeCycleFactory.activate();
  }
  
}
