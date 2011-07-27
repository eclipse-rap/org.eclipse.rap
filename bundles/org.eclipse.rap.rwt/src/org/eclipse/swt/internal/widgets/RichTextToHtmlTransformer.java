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
package org.eclipse.swt.internal.widgets;

import java.util.Map;

import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class RichTextToHtmlTransformer implements IRichTextParserCallback {
  private final StringBuilder buffer;
  private final Map<String,Image> imageMap;

  public RichTextToHtmlTransformer( TableItem tableItem ) {
    this.buffer = new StringBuilder();
    this.imageMap = getImageMap( tableItem );
  }

  public void beginHtml() {
    buffer.setLength( 0 );
    buffer.append( "<div" );
    appendAttribute( "style", "line-height:normal;" );
    buffer.append( ">" );
  }

  public void endHtml() {
    buffer.append( "</div>" );
  }

  public void beginFont( String name, int height ) {
    buffer.append( "<span" );
    String encodeName = EncodingUtil.encodeHTMLEntities( name );
    String fontStyle = "font-family:" + encodeName + ";font-size:" + height + "px";
    appendAttribute( "style", fontStyle );
    buffer.append( ">" );
  }

  public void endFont() {
    buffer.append( "</span>" );
  }

  public void image( String src ) {
    Image image = imageMap.get( src );
    Rectangle imageBounds = image.getBounds();
    buffer.append( "<img" );
    appendAttribute( "src", ImageFactory.getImagePath( image ) );
    appendAttribute( "width", imageBounds.width + "px" );
    appendAttribute( "height", image.getBounds().height + "px" );
    buffer.append( " />" );
  }

  public void lineBreak() {
    buffer.append( "<br />" );
  }

  public void text( String text ) {
    buffer.append( text );
  }

  public String getHtml() {
    return buffer.toString();
  }
  
  private void appendAttribute( String name, String value ) {
    buffer.append( " " );
    buffer.append( name );
    buffer.append( "=\"" );
    buffer.append( value );
    buffer.append( "\"" );
  }

  @SuppressWarnings("unchecked")
  private static Map<String,Image> getImageMap( TableItem tableItem ) {
    return ( Map<String,Image> )tableItem.getParent().getData( Table.IMAGE_MAP );
  }
}