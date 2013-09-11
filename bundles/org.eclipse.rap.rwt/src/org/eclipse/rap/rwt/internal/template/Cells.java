/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import org.eclipse.swt.graphics.Image;


public class Cells {

  static final String TYPE_IMAGE = "image";
  static final String PROPERTY_IMAGE = "image";
  static final String TYPE_TEXT = "text";
  static final String PROPERTY_TEXT = "text";

  public static Cell createTextCell( RowTemplate template, String text ) {
    checkNotNull( template, "RowTemplate" );
    checkNotNull( text, "Text" );
    CellImpl cell = new CellImpl( template, TYPE_TEXT );
    cell.addAttribute( PROPERTY_TEXT, text );
    return cell;
  }

  public static Cell createTextCell( RowTemplate template, int bindingIndex ) {
    checkNotNull( template, "RowTemplate" );
    CellImpl cell = new CellImpl( template, TYPE_TEXT );
    cell.setBindingIndex( bindingIndex );
    return cell;
  }

  public static Cell createImageCell( RowTemplate template, Image image ) {
    checkNotNull( template, "RowTemplate" );
    checkNotNull( image, "Image" );
    CellImpl cell = new CellImpl( template, TYPE_IMAGE );
    cell.addAttribute( PROPERTY_IMAGE, image );
    return cell;
  }

  public static Cell createImageCell( RowTemplate template, int bindingIndex ) {
    checkNotNull( template, "RowTemplate" );
    CellImpl cell = new CellImpl( template, TYPE_IMAGE );
    cell.setBindingIndex( bindingIndex );
    return cell;
  }

  private static void checkNotNull( Object value, String name ) {
    if( value == null ) {
      throw new IllegalArgumentException( name + " must not be null" );
    }
  }

  private Cells() {
    // prevent instantiation
  }
}
