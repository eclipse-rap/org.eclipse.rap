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
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.resources.TestUtil.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Message;
import org.eclipse.rwt.Message.*;


public class Message_Test extends TestCase {
  
  private static final String SUFFIX = " );";
  private static final String PREFIX = "org.eclipse.rwt.protocol.Processor.processMessage( ";
  private ProtocolMessageWriter writer;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    writer = new ProtocolMessageWriter();
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testConstructWithNull() {
    try {
      new Message( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testConstructWithInvalidJson() {
    try {
      new Message( PREFIX + "{" + SUFFIX );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Could not parse json" ) );
    }
  }

  public void testConstructWithoutOperations() {
    try {
      new Message( PREFIX + "{ \"foo\": 23 }" + SUFFIX );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  public void testConstructWithInvalidOperations() {
    try {
      new Message( PREFIX + "{ \"operations\": 23 }" + SUFFIX );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( "Missing operations array" ) );
    }
  }

  public void testGetOperationCountWhenEmpty() {
    assertEquals( 0, getMessage().getOperationCount() );
  }

  public void testGetOperationCount() {
    writer.appendCall( "w1", "method1", null );
    writer.appendCall( "w2", "method2", null );

    assertEquals( 2, getMessage().getOperationCount() );
  }

  public void testGetOperation() {
    writer.appendCall( "w2", "method", null );
    
    assertNotNull( getMessage().getOperation( 0 ) );
  }
  
  public void testGetCreateOperation() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", new Integer( 2 ) );
    writer.appendCreate( "w1", "w0", "type", properties );
    
    assertTrue( getMessage().getOperation( 0 ) instanceof CreateOperation );
  }
  
  public void testGetCallOperation() {
    writer.appendCall( "w2", "method", null );
    
    assertTrue( getMessage().getOperation( 0 ) instanceof CallOperation );
  }

  public void testGetSetOperation() {
    writer.appendSet( "w1", "key", true );
    
    assertTrue( getMessage().getOperation( 0 ) instanceof SetOperation );
  }
  
  public void testGetListenOperation() {
    writer.appendListen( "w1", "event", true );
    
    assertTrue( getMessage().getOperation( 0 ) instanceof ListenOperation );
  }
  
  public void testGetExecuteScriptOperation() {
    writer.appendExecuteScript( "w1", "java", "content" );
    
    assertTrue( getMessage().getOperation( 0 ) instanceof ExecuteScriptOperation );
  }
  
  public void testGetDestroyOperation() {
    writer.appendDestroy( "w1" );
    
    assertTrue( getMessage().getOperation( 0 ) instanceof DestroyOperation );
  }
  
  public void testGetOperationWithUnknownType() {
    Message message = new Message( PREFIX
                                   + "{ \"operations\" : [ { \"action\" : \"foo\" } ] }"
                                   + SUFFIX );    
    try {
      message.getOperation( 0 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCreateOperation() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", new Integer( 2 ) );
    String[] styles = new String[] { "FOO", "BAR" };
    properties.put( ProtocolConstants.CREATE_STYLE, styles );
    
    writer.appendCreate( "w1", "w0", "type", properties );
    
    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( "w1", operation.getTarget() );
    assertEquals( "w0", operation.getParent() );
    assertEquals( "type", operation.getType() );
    assertArrayEquals( styles, operation.getStyles() );
    assertEquals( "a", operation.getProperty( "key1" ) );
    assertEquals( new Integer( 2 ), operation.getProperty( "key2" ) );
  }
  
  public void testCallOperation() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", new Integer( 2 ) );
    writer.appendCall( "w2", "method", properties );
    
    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( "w2", operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ) );
    assertEquals( new Integer( 2 ), operation.getProperty( "key2" ) );
  }
  
  public void testSetOperation() {
    writer.appendSet( "w1", "key", true );
    writer.appendSet( "w1", "key2", "value" );
    
    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( "w1", operation.getTarget() );
    assertEquals( Boolean.TRUE, operation.getProperty( "key" ) );
    assertEquals( "value", operation.getProperty( "key2" ) );
  }
  
  public void testListenOperation() {
    writer.appendListen( "w1", "event", true );
    writer.appendListen( "w1", "event2", false );
    
    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( true, operation.listensTo( "event" ) );
    assertEquals( false, operation.listensTo( "event2" ) );
  }
  
  public void testExecuteScriptOperation() {
    writer.appendExecuteScript( "w1", "java", "content" );
    
    ExecuteScriptOperation operation = ( ExecuteScriptOperation )getMessage().getOperation( 0 );
    assertEquals( "java", operation.getScriptType() );
    assertEquals( "content", operation.getScript() );
  }
  
  public void testWithTwoOperations() {
    writer.appendDestroy( "w3" );
    writer.appendExecuteScript( "w1", "java", "content" );
    
    ExecuteScriptOperation operation = ( ExecuteScriptOperation )getMessage().getOperation( 1 );
    assertEquals( "java", operation.getScriptType() );
    assertEquals( "content", operation.getScript() );
  }
  
  public void testNonExistingValue() {
    writer.appendSet( "w1", "key", true );
    
    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    try {
      operation.getProperty( "key2" );
      fail();
    } catch ( IllegalStateException expected ) {
    }
  }
  
  public void testNonExistingOperation() {
    writer.appendSet( "w1", "key", true );
    
    try {
      getMessage().getOperation( 1 );
      fail();
    } catch ( IllegalStateException expected ) {
    }
  }
  
  private Message getMessage() {
    return new Message( PREFIX + writer.createMessage() + SUFFIX );
  }

}
