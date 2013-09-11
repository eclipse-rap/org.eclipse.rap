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


public final class Cells {

  static final String TYPE_IMAGE = "image";
  static final String PROPERTY_DEFAULT_IMAGE = "defaultImage";
  static final String TYPE_TEXT = "text";
  static final String PROPERTY_DEFAULT_TEXT = "defaultText";

  public static Cell createTextCell( RowTemplate template ) {
    checkNotNull( template, "RowTemplate" );
    CellImpl cell = new CellImpl( template, TYPE_TEXT );
    return cell;
  }

  public static Cell createTextCell( RowTemplate template, String defaultText ) {
    CellImpl cell = ( CellImpl )createTextCell( template );
    checkNotNull( defaultText, "Text" );
    cell.addAttribute( PROPERTY_DEFAULT_TEXT, defaultText );
    return cell;
  }

  public static Cell createImageCell( RowTemplate template ) {
    checkNotNull( template, "RowTemplate" );
    CellImpl cell = new CellImpl( template, TYPE_IMAGE );
    return cell;
  }

  public static Cell createImageCell( RowTemplate template, Image image ) {
    CellImpl cell = ( CellImpl )createImageCell( template );
    checkNotNull( image, "Image" );
    cell.addAttribute( PROPERTY_DEFAULT_IMAGE, image );
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
