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
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.StartupPageTemplate.VariableWriter;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class StartupPageTemplate_Test extends TestCase {
  
  private ByteArrayOutputStream outputStream;
  private PrintWriter printWriter;
  private VariableWriter variableValueProvider;

  public void testWritePageWithoutVariables() throws UnsupportedEncodingException {
    String templateWithoutVariables = "<html page/>";
    StartupPageTemplate template = new StartupPageTemplate( templateWithoutVariables );
    
    template.writePage( printWriter, variableValueProvider );
    
    assertEquals( templateWithoutVariables, getWrittenOutput() );
    verifyZeroInteractions( variableValueProvider );
  }
  
  public void testWritePageWithVariable() throws UnsupportedEncodingException {
    doAnswer( new Answer() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        printWriter.write( "value" );
        return null;
      }
    } ).when( variableValueProvider ).writeVariable( printWriter, "var" );
    StartupPageTemplate template = new StartupPageTemplate( "<html ${var} page/>" );
    
    template.writePage( printWriter, variableValueProvider );
    
    assertEquals( "<html value page/>", getWrittenOutput() );
    variableValueProvider.writeVariable( printWriter, "var" ); 
  }

  @Override
  protected void setUp() throws Exception {
    outputStream = new ByteArrayOutputStream();
    printWriter = new PrintWriter( outputStream );
    variableValueProvider = mock( VariableWriter.class );
  }

  private String getWrittenOutput() throws UnsupportedEncodingException {
    return outputStream.toString( HTTP.CHARSET_UTF_8 );
  }
}
