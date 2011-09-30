/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BrowserLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private BrowserLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new BrowserLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testTextChanged() throws IOException {
    Fixture.markInitialized( display );
    Browser browser = new Browser( shell, SWT.NONE );

    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    String expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    assertFalse( BrowserLCA.hasUrlChanged( browser ) );

    browser = new Browser( shell, SWT.NONE );
    browser.setText( "Hello" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "Hello".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    browser.setText( "GoodBye" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "GoodBye".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );
    Fixture.preserveWidgets();
    browser.setText( "GoodBye" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "GoodBye".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );

    browser = new Browser( shell, SWT.NONE );
    browser.setText( "" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );
  }

  public void testUrlChanged() throws IOException {
    Fixture.markInitialized( display );
    Browser browser = new Browser( shell, SWT.NONE );

    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    String expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    assertFalse( BrowserLCA.hasUrlChanged( browser ) );

    browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rap", BrowserLCA.getUrl( browser ) );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    browser.setUrl( "http://eclipse.org/rip" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rip", BrowserLCA.getUrl( browser ) );
    Fixture.preserveWidgets();
    browser.setUrl( "http://eclipse.org/rip" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rip", BrowserLCA.getUrl( browser ) );
  }

  public void testResetUrlChanged_NotInitialized() throws IOException {
    Fixture.markInitialized( display );
    Browser browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );
    Fixture.fakeResponseWriter();

    BrowserLCA lca = new BrowserLCA();
    lca.renderChanges( browser );

    assertFalse( getAdapter( browser).hasUrlChanged() );
  }

  public void testResetUrlChanged_Initialized() throws IOException {
    Fixture.markInitialized( display );
    Browser browser = new Browser( shell, SWT.NONE );
    Fixture.markInitialized( browser );
    browser.setUrl( "http://eclipse.org/rap" );
    Fixture.fakeResponseWriter();

    BrowserLCA lca = new BrowserLCA();
    lca.renderChanges( browser );

    assertFalse( getAdapter( browser).hasUrlChanged() );
  }

  public void testExecuteFunction() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Browser browser = new Browser( shell, SWT.NONE );
    new BrowserFunction( browser, "func" ) {
      public Object function( final Object[] arguments ) {
        for( int i = 0; i < arguments.length; i++ ) {
          log.append( arguments[ i ].toString() );
          log.append( "|" );
        }
        return new Object[ 0 ];
      }
    };
    Fixture.fakeNewRequest( display );
    String browserId = WidgetUtil.getId( browser );
    String param = browserId + "." + BrowserLCA.PARAM_EXECUTE_FUNCTION;
    Fixture.fakeRequestParam( param, "func" );
    param = browserId + "." + BrowserLCA.PARAM_EXECUTE_ARGUMENTS;
    Fixture.fakeRequestParam( param, "[\"eclipse\",3.6]" );
    Fixture.readDataAndProcessAction( browser );
    assertTrue( log.indexOf( "eclipse" ) != -1 );
    assertTrue( log.indexOf( "3.6" ) != -1 );
  }

  public void testParseArguments() {
    String input = "[]";
    Object result = BrowserLCA.parseArguments( input );
    assertNotNull( result );
    assertTrue( result.getClass().isArray() );
    Object[] resultArray = ( Object[] )result;
    assertEquals( 0, resultArray.length );
    input = "[null]";
    result = BrowserLCA.parseArguments( input );
    assertTrue( result.getClass().isArray() );
    resultArray = ( Object[] )result;
    assertEquals( 1, resultArray.length );
    assertNull( resultArray[ 0 ] );
    input = "[undefined]";
    result = BrowserLCA.parseArguments( input );
    assertTrue( result.getClass().isArray() );
    resultArray = ( Object[] )result;
    assertEquals( 1, resultArray.length );
    assertNull( resultArray[ 0 ] );
    input = "[\"eclipse\"]";
    result = BrowserLCA.parseArguments( input );
    assertTrue( result.getClass().isArray() );
    resultArray = ( Object[] )result;
    assertEquals( 1, resultArray.length );
    assertEquals( new String( "eclipse" ), resultArray[ 0 ] );
    input = "[\"ecl[\\\"]ipse\"]";
    result = BrowserLCA.parseArguments( input );
    assertTrue( result.getClass().isArray() );
    resultArray = ( Object[] )result;
    assertEquals( 1, resultArray.length );
    assertEquals( new String( "ecl[\"]ipse" ), resultArray[ 0 ] );
    input = "[3.6]";
    result = BrowserLCA.parseArguments( input );
    assertTrue( result.getClass().isArray() );
    resultArray = ( Object[] )result;
    assertEquals( 1, resultArray.length );
    assertEquals( new Double( 3.6 ), resultArray[ 0 ] );
    input = "[12,false,null,[3.6,[\"swt\",true]],\"eclipse\"]";
    result = BrowserLCA.parseArguments( input );
    assertTrue( result.getClass().isArray() );
    resultArray = ( Object[] )result;
    assertEquals( 5, resultArray.length );
    assertEquals( new Double( 12 ), resultArray[ 0 ] );
    assertEquals( new Boolean( false ), resultArray[ 1 ] );
    assertNull( resultArray[ 2 ] );
    assertTrue( resultArray[ 3 ].getClass().isArray() );
    Object[] resultArray1 = ( Object[] )resultArray[ 3 ];
    assertEquals( 2, resultArray1.length );
    assertEquals( new Double( 3.6 ), resultArray1[ 0 ] );
    assertTrue( resultArray1[ 1 ].getClass().isArray() );
    Object[] resultArray2 = ( Object[] )resultArray1[ 1 ];
    assertEquals( 2, resultArray2.length );
    assertEquals( "swt", resultArray2[ 0 ] );
    assertEquals( new Boolean( true ), resultArray2[ 1 ] );
    assertEquals( "eclipse", resultArray[ 4 ] );
  }

  public void testWithType() {
    String input = "null";
    Object result = BrowserLCA.withType( input );
    assertNull( result );
    input = "undefined";
    result = BrowserLCA.withType( input );
    assertNull( result );
    input = "true";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof Boolean );
    assertTrue( ( ( Boolean )result ).booleanValue() );
    input = "false";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof Boolean );
    assertFalse( ( ( Boolean )result ).booleanValue() );
    input = "\"eclipse\"";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof String );
    assertEquals( "eclipse", ( String )result );
    input = "3.6";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof Double );
    assertEquals( new Double( 3.6 ), result );
    input = "bla-bla";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof String );
    assertEquals( "bla-bla", ( String )result );
    input = "3.6 percent";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof String );
    assertEquals( "3.6 percent", ( String )result );
    input = "null \" 3.6 true";
    result = BrowserLCA.withType( input );
    assertTrue( result instanceof String );
    assertEquals( "null \" 3.6 true", ( String )result );
  }

  public void testToJson() {
    Object input = null;
    String result = BrowserLCA.toJson( input, true );
    String expected = "null";
    assertEquals( expected, result );
    input = Boolean.TRUE;
    result = BrowserLCA.toJson( input, true );
    expected = "true";
    assertEquals( expected, result );
    input = Boolean.FALSE;
    result = BrowserLCA.toJson( input, true );
    expected = "false";
    assertEquals( expected, result );
    input = new Double( 3.6 );
    result = BrowserLCA.toJson( input, true );
    expected = "3.6";
    assertEquals( expected, result );
    input = new String( "eclipse" );
    result = BrowserLCA.toJson( input, true );
    expected = "\"eclipse\"";
    assertEquals( expected, result );
    input = new Object[] {
      new Short( ( short )3 ),
      new Boolean( true ),
      null,
      new Object[] { "a string", new Boolean( false ) },
      "hi",
      new Float( 2.0 )
    };
    result = BrowserLCA.toJson( input, true );
    expected = "[3,true,null,[\"a string\",false],\"hi\",2.0]";
    assertEquals( expected, result );
    input = new String( "\"RAP\"" );
    result = BrowserLCA.toJson( input, true );
    expected = "\"\\\"RAP\\\"\"";
    assertEquals( expected, result );
  }

  public void testToJson_EmptyArray() {
    Object input = new Object[ 0 ];
    String result = BrowserLCA.toJson( input, true );
    String expected = "[]";
    assertEquals( expected, result );
    input = new Object[] {
      new Object[ 0 ]
    };
    result = BrowserLCA.toJson( input, true );
    expected = "[[]]";
    assertEquals( expected, result );
    input = new Object[] {
      "string1",
      new Object[ 0 ],
      "string2"
    };
    result = BrowserLCA.toJson( input, true );
    expected = "[\"string1\",[],\"string2\"]";
    assertEquals( expected, result );
  }

  public void testProgressEvent() {
    final ArrayList<String> log = new ArrayList<String>();
    Fixture.markInitialized( display );
    Browser browser = new Browser( shell, SWT.NONE );
    browser.addProgressListener( new ProgressListener() {
      public void changed( final ProgressEvent event ) {
        log.add( "changed" );
      }
      public void completed( final ProgressEvent event ) {
        log.add( "completed" );
      }
    } );
    String browserId = WidgetUtil.getId( browser );
    Fixture.fakeRequestParam( BrowserLCA.EVENT_PROGRESS_COMPLETED, browserId );
    Fixture.readDataAndProcessAction( browser );
    assertEquals( 2, log.size() );
    assertEquals( "changed", log.get( 0 ) );
    assertEquals( "completed", log.get( 1 ) );
  }

  public void testRenderCreate() throws IOException {
    Browser browser = new Browser( shell, SWT.NONE );

    lca.renderInitialization( browser );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertEquals( "rwt.widgets.Browser", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Browser browser = new Browser( shell, SWT.NONE );

    lca.renderInitialization( browser );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertEquals( WidgetUtil.getId( browser.getParent() ), operation.getParent() );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Browser browser = new Browser( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.addProgressListener( new ProgressListener(){
      public void changed( ProgressEvent event ) {
      }
      public void completed( ProgressEvent event ) {
      }
    } );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( browser, "progress" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Browser browser = new Browser( shell, SWT.NONE );
    ProgressListener listener = new ProgressListener(){
      public void changed( ProgressEvent event ) {
      }
      public void completed( ProgressEvent event ) {
      }
    };
    browser.addProgressListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.removeProgressListener( listener );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( browser, "progress" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Browser browser = new Browser( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.addProgressListener( new ProgressListener(){
      public void changed( ProgressEvent event ) {
      }
      public void completed( ProgressEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( browser, "progress" ) );
  }
  
  public void testRenderInitialUrl() throws IOException {
    Browser browser = new Browser( shell, SWT.NONE );

    lca.render( browser );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertTrue( operation.getPropertyNames().indexOf( "url" ) != -1 );
  }

  public void testRenderUrl() throws IOException {
    Browser browser = new Browser( shell, SWT.NONE );

    browser.setUrl( "http://eclipse.org/rap" );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "http://eclipse.org/rap", message.findSetProperty( browser, "url" ) );
  }

  public void testRenderUrlUnchanged() throws IOException {
    Browser browser = new Browser( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    browser.setUrl( "http://eclipse.org/rap" );
    lca.renderChanges( browser );
    Fixture.fakeNewRequest( display );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( browser, "url" ) );
  }
  
  public void testCallEvaluate() {
    Browser browser = new Browser( shell, SWT.NONE ) {
      public boolean execute( String script ) {        
        executeScript = script;  
        return true;
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    
    browser.execute( "alert('33');" );
    Fixture.executeLifeCycleFromServerThread();
    
    Message message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "evaluate" );
    assertEquals( "alert('33');", callOperation.getProperty( "script" ) );
  }

  private static IBrowserAdapter getAdapter( Browser browser ) {
    return ( IBrowserAdapter )browser.getAdapter( IBrowserAdapter.class );
  }
}
