/*******************************************************************************
* Copyright (c) 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;


public class RemoteObjectImpl_Test extends TestCase {

  private String objectId;
  private RemoteObjectImpl remoteObject;
  private ProtocolMessageWriter writer;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    objectId = "testId";
    remoteObject = new RemoteObjectImpl( objectId );
    writer = mock( ProtocolMessageWriter.class );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDoesNotRenderOperationsImmediately() {
    remoteObject.create( "type" );

    assertEquals( 0, getMessage().getOperationCount() );
  }

  public void testOperationsAreRenderedDeferred() {
    remoteObject.create( "type" );

    remoteObject.render( getProtocolWriter() );

    assertEquals( 1, getMessage().getOperationCount() );
  }

  public void testCreateIsRendered() {
    remoteObject.create( "type" );

    remoteObject.render( writer );

    verify( writer ).appendCreate( eq( objectId ), eq( "type" ) );
  }

  public void testSetIntIsRendered() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  public void testSetDoubleIsRendered() {
    remoteObject.set( "property", 47.11 );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( 47.11 ) );
  }

  public void testSetBooleanIsRendered() {
    remoteObject.set( "property", true );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( true ) );
  }

  public void testSetStringIsRendered() {
    remoteObject.set( "property", "foo" );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( "foo" ) );
  }

  public void testSetObjectIsRendered() {
    remoteObject.set( "property", "foo" );

    remoteObject.render( writer );

    verify( writer ).appendSet( eq( objectId ), eq( "property" ), eq( "foo" ) );
  }

  public void testListenIsRendered() {
    remoteObject.listen( "event", true );

    remoteObject.render( writer );

    verify( writer ).appendListen( eq( objectId ), eq( "event" ), eq( true ) );
  }

  public void testCallIsRendered() {
    Map<String, Object> properties = mockProperties();
    remoteObject.call( "method", properties );

    remoteObject.render( writer );

    verify( writer ).appendCall( eq( objectId ), eq( "method" ), same( properties ) );
  }

  public void testDestroyIsRendered() {
    remoteObject.destroy();

    remoteObject.render( writer );

    verify( writer ).appendDestroy( eq( objectId ) );
  }

  public void testRenderQueueIsClearedAfterRender() {
    remoteObject.set( "property", 23 );

    remoteObject.render( writer );
    remoteObject.render( writer );

    verify( writer, times( 1 ) ).appendSet( eq( objectId ), eq( "property" ), eq( 23 ) );
  }

  @SuppressWarnings( "unchecked" )
  private static Map<String, Object> mockProperties() {
    return mock( Map.class );
  }

  private static Message getMessage() {
    return new Message( getProtocolWriter().createMessage() );
  }

  private static ProtocolMessageWriter getProtocolWriter() {
    return ContextProvider.getProtocolWriter();
  }

}
