/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BrowserLCA_Test extends TestCase {

  public void testTextChanged() throws IOException {
    Display display = new Display();
    Fixture.markInitialized( display );
    Shell shell = new Shell( display );
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
    Display display = new Display();
    Fixture.markInitialized( display );
    Shell shell = new Shell( display );
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

  public void testRenderUrl() throws IOException {
    Display display = new Display();
    Fixture.markInitialized( display );
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );
    BrowserLCA lca = new BrowserLCA();
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    lca.renderChanges( browser );
    String expected = "w.setSource( \"http://eclipse.org/rap\" );";
    expected += "w.syncSource()";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testExecuteFunction() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
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
    Fixture.fakeNewRequest();
    String displayId = DisplayUtil.getId( display );
    String browserId = WidgetUtil.getId( browser );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
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

  public void testPreserveProgressListener() {
    Display display = new Display();
    Fixture.markInitialized( display );
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( browser );
    Boolean hasListeners
      = ( Boolean )adapter.getPreserved( BrowserLCA.PARAM_PROGRESS_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    browser.addProgressListener( new ProgressListener() {
      public void changed( final ProgressEvent event ) {
      }
      public void completed( final ProgressEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( browser );
    hasListeners
      = ( Boolean )adapter.getPreserved( BrowserLCA.PARAM_PROGRESS_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  public void testProgressEvent() {
    final ArrayList log = new ArrayList();
    Display display = new Display();
    Fixture.markInitialized( display );
    Shell shell = new Shell( display );
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

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
