/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;

/**
 * <p>Utility class to create commonly used HTML artefacts.</p>
 */
public final class HTMLUtil {

  public static String hiddenInput( final String name, final String value ) {
    StringBuffer buffer = new StringBuffer();
    hiddenInput( buffer, name, value );
    return buffer.toString();
  }
  
  public static void hiddenInput( final StringBuffer buffer, 
                                  final String name, 
                                  final String value )
  {
    String id = name == null 
              ? null 
              : EntitiesUtil.encodeHTMLEntities( name );
    String encodedValue = value == null
                        ? null
                        : EntitiesUtil.encodeHTMLEntities( value );
    buffer.append( "<input type=\"hidden\" id=\"" );
    buffer.append( id );
    buffer.append( "\" name=\"" );
    buffer.append( id );
    buffer.append( "\" value=\"" );
    buffer.append( encodedValue );
    buffer.append( "\" />" );
  }

  public static void hiddenInput( final HtmlResponseWriter writer,
                                  final String name,
                                  final String value )
    throws IOException
  {
    writer.startElement( HTML.INPUT, null );
    writer.writeAttribute( HTML.TYPE, HTML.HIDDEN, null );
    writer.writeAttribute( HTML.ID, name, null );
    writer.writeAttribute( HTML.NAME, name, null );
    writer.writeAttribute( HTML.VALUE, value, null );
    writer.endElement( HTML.INPUT );
  }
  
  public static void attribute( final StringBuffer buffer, 
                                final String name, 
                                final String value  ) 
  {
    buffer.append( " " );
    buffer.append( name );
    buffer.append( "=\"" );
    String encodedValue = value == null 
                        ? value 
                        : EntitiesUtil.encodeHTMLEntities( value );
    buffer.append( encodedValue );
    buffer.append( "\"" );
  }
  
  public static String attribute( final String name, final String value ) {
    StringBuffer buffer = new StringBuffer();
    attribute( buffer, name, value );
    return buffer.toString();
  }

  // TODO [w4t] moved here from RenderUtil in favor of moving RenderUtil to RWT
  //      (which in turn depends on WebComponent)
  public static String createXmlProcessingInstruction() {
    StringBuffer result = new StringBuffer();
    result.append( "<?xml" );
    HTMLUtil.attribute( result, "version", "1.0" );
    HTMLUtil.attribute( result, "encoding", HTML.CHARSET_NAME_UTF_8 );
    result.append( " ?>" );
    return result.toString();
  }

  // TODO [w4t] moved here from RenderUtil in favor of moving RenderUtil to RWT 
  //      (which in turn depends on WebComponent)
  public static String createJavaScriptInline( final String javaScriptCode ) {
    StringBuffer result = new StringBuffer();
    if( javaScriptCode != null && !"".equals( javaScriptCode ) ) {
      result.append( "<script" );
      HTMLUtil.attribute( result, HTML.TYPE, HTML.CONTENT_TEXT_JAVASCRIPT );
      result.append( ">" );
      result.append( EntitiesUtil.encodeHTMLEntities( javaScriptCode ) );
      result.append( "</script>" );
    }
    return result.toString();
  }
}
