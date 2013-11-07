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

import org.eclipse.rap.json.JsonObject;


public class TextCell extends Cell<TextCell>  {

  private static final String TYPE_TEXT = "text";
  private static final String PROPERTY_TEXT = "text";
  private static final String PROPERTY_WRAP = "wrap";
  private String text;
  private boolean wrap;

  public TextCell( RowTemplate template ) {
    super( template, TYPE_TEXT );
  }

  // binding index wins. Only if no binding index is set the default will be used
  public TextCell setText( String text ) {
    checkNotNull( text, "Text" );
    this.text = text;
    return this;
  }

  String getText() {
    return text;
  }

  public TextCell setWrap( boolean wrap ) {
    this.wrap = wrap;
    return this;
  }

  boolean isWrap() {
    return wrap;
  }

  @Override
  protected JsonObject toJson() {
    JsonObject json = super.toJson();
    if( text != null ) {
      json.add( PROPERTY_TEXT, text );
    }
    if( wrap ) {
      json.add( PROPERTY_WRAP, wrap );
    }
    return json;
  }

  private void checkNotNull( Object value, String name ) {
    if( value == null ) {
      throw new IllegalArgumentException( name + " must not be null" );
    }
  }
}
