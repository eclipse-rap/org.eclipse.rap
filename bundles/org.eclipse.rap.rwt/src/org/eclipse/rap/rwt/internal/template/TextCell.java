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


public class TextCell extends Cell<TextCell>  {

  static final String TYPE_TEXT = "text";
  static final String PROPERTY_DEFAULT_TEXT = "defaultText";
  static final String PROPERTY_WRAP = "wrap";

  public TextCell( RowTemplate template ) {
    super( template, TYPE_TEXT );
  }

  // binding index wins. Only if no binding index is set the default will be used
  public TextCell setDefaultText( String text ) {
    checkNotNull( text, "Text" );
    addAttribute( PROPERTY_DEFAULT_TEXT, text );
    return this;
  }

  public TextCell setWrap( boolean wrap ) {
    addAttribute( PROPERTY_WRAP, Boolean.valueOf( wrap ) );
    return this;
  }

  private void checkNotNull( Object value, String name ) {
    if( value == null ) {
      throw new IllegalArgumentException( name + " must not be null" );
    }
  }
}
