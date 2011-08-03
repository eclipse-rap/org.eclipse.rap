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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class ProtocolMessageCreator {

  public String createMessage() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Button button = new Button( shell, SWT.TOGGLE );
    // create client objects
    IClientObject clientShell = ClientObjectFactory.getForWidget( shell );
    Map<String, Object> shellProperties = new HashMap<String, Object>();
    String[] allowedShellStyles = RWTStylesUtil.getAllowedStylesForWidget( shell );
    String[] shellStyles = StylesUtil.filterStyles( shell, allowedShellStyles );
    shellProperties.put( ProtocolConstants.CREATE_STYLE, shellStyles );
    clientShell.create( shellProperties );
    IClientObject clientButton = ClientObjectFactory.getForWidget( button );
    Map<String, Object> buttonProperties = new HashMap<String, Object>();
    String[] allowedButtonStyles = RWTStylesUtil.getAllowedStylesForWidget( button );
    String[] buttonStyles = StylesUtil.filterStyles( button, allowedButtonStyles );
    buttonProperties.put( ProtocolConstants.CREATE_STYLE, buttonStyles );
    clientButton.create( buttonProperties );
    // set some properties
    clientShell.setProperty( "foo", "bar" );
    clientShell.setProperty( "bar", new Object[] { "foo", new Integer( 42 ) } );
    clientButton.setProperty( "foo", "bar" );
    // call some methods
    Map<String, Object> callProperties = new HashMap<String, Object>();
    callProperties.put( "param1", "value1" );
    callProperties.put( "param2", "value2" );
    clientShell.call( "foo", callProperties );
    clientButton.call( "bar", callProperties );
    // add and remove some listeners
    clientShell.addListener( "selection" );
    clientShell.addListener( "focus" );
    clientButton.addListener( "selection" );
    clientShell.removeListener( "focus" );
    clientShell.removeListener( "selection" );
    clientButton.removeListener( "selection" );
    // execute a script
    clientShell.executeScript( "application/javascript", "var x = 23;" );
    // destroy objects
    clientShell.destroy();
    clientButton.destroy();
    // finish message
    return getMessage();
  }
  
  private String getMessage() {
    closeProtocolWriter();
    String markup = Fixture.getAllMarkup();
    markup = markup.replaceAll( "^" + JavaScriptResponseWriter.PROCESS_MESSAGE + "\\(", "" );
    markup = markup.replaceAll( "\\);$", "" );
    return markup;
  }

  private void closeProtocolWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
    writer.finish();
  }
  
  public static void main( String... args ) {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    ProtocolMessageCreator creator = new ProtocolMessageCreator();
    System.out.println( creator.createMessage() );
    Fixture.tearDown();
  }
}
