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
package org.eclipse.rap.examples.pages.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.template.ImageCell;
import org.eclipse.rap.rwt.template.ImageCell.ScaleMode;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.template.TextCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class PersonsTemplate extends Template {
  private static final String MY_FONT = "Tahoma, Geneva, sans-serif";

  public PersonsTemplate() {
    super();
    createImageCell();
    createLastNameCell();
    createFirstNameCell();
    createMailLabelCell();
    createMailCell();
    createPhoneLabelCell();
    createSeparatorCell(); // TODO [tb] : do by theming
    createPhoneCell();
    createArrowIconCell();
  }

  private void createArrowIconCell() {
    ImageCell arrow = new ImageCell( this );
    arrow.setHorizontalAlignment( SWT.RIGHT );
    InputStream stream
      = PersonsTemplate.class.getClassLoader().getResourceAsStream( "/resources/right.png" );
    final Image arrowImage = new Image( Display.getCurrent(), stream );
    try {
      stream.close();
    } catch( IOException e ) {
      e.printStackTrace();
    }
    arrow.setImage( arrowImage );
    arrow.setTop( 8 );
    arrow.setWidth( 48 );
    arrow.setRight( 8 );
    arrow.setBottom( 8 );
    arrow.setName( "arrow" );
    arrow.setSelectable( true );
  }

  private void createFirstNameCell() {
    TextCell lastNameCell = new TextCell( this );
    lastNameCell.setHorizontalAlignment( SWT.LEFT );
    lastNameCell.setVerticalAlignment( SWT.TOP );
    lastNameCell.setBindingIndex( 0 );
    lastNameCell.setLeft( 60 );
    lastNameCell.setTop( 5 );
    lastNameCell.setWidth( 180 );
    lastNameCell.setHeight( 40 );
    Font font = new Font( Display.getCurrent(), new FontData( MY_FONT, 20, SWT.NORMAL ) );
    lastNameCell.setFont( font );
  }

  private void createLastNameCell() {
    TextCell firstNameCell = new TextCell( this );
    Font font = new Font( Display.getCurrent(), new FontData( MY_FONT, 14, SWT.NORMAL ) );
    firstNameCell.setFont( font );
    firstNameCell.setHorizontalAlignment( SWT.LEFT );
    firstNameCell.setBindingIndex( 1 );
    firstNameCell.setLeft( 60 );
    firstNameCell.setTop( 30 );
    firstNameCell.setWidth( 180 );
    firstNameCell.setBottom( 8 );
  }

  private void createPhoneLabelCell() {
    TextCell phoneLabelCell = new TextCell( this );
    Font font = new Font( Display.getCurrent(), new FontData( MY_FONT, 14, SWT.BOLD ) );
    phoneLabelCell.setFont( font );
    phoneLabelCell.setHorizontalAlignment( SWT.LEFT );
    phoneLabelCell.setText( "Phone:" );
    phoneLabelCell.setLeft( 250 );
    phoneLabelCell.setTop( 30 );
    phoneLabelCell.setRight( 8 );
    phoneLabelCell.setBottom( 8 );
  }

  private void createPhoneCell() {
    TextCell phoneCell = new TextCell( this );
    Font font = new Font( Display.getCurrent(), new FontData( MY_FONT, 14, SWT.NORMAL ) );
    phoneCell.setFont( font );
    phoneCell.setHorizontalAlignment( SWT.LEFT );
    phoneCell.setBindingIndex( 2 );
    phoneCell.setLeft( 310 );
    phoneCell.setTop( 30 );
    phoneCell.setWidth( 150 );
    phoneCell.setBottom( 8 );
    phoneCell.setName( "phone" );
    phoneCell.setSelectable( true );
  }

  private void createMailLabelCell() {
    TextCell phoneLabelCell = new TextCell( this );
    Font font = new Font( Display.getCurrent(), new FontData( MY_FONT, 14, SWT.BOLD ) );
    phoneLabelCell.setFont( font );
    phoneLabelCell.setHorizontalAlignment( SWT.LEFT );
    phoneLabelCell.setText( "E-Mail:" );
    phoneLabelCell.setLeft( 250 );
    phoneLabelCell.setTop( 8 );
    phoneLabelCell.setRight( 8 );
    phoneLabelCell.setBottom( 8 );
  }

  private void createMailCell() {
    TextCell phoneCell = new TextCell( this );
    Font font = new Font( Display.getCurrent(), new FontData( MY_FONT, 14, SWT.NORMAL ) );
    phoneCell.setFont( font );
    phoneCell.setHorizontalAlignment( SWT.LEFT );
    phoneCell.setBindingIndex( 3 );
    phoneCell.setLeft( 310 );
    phoneCell.setTop( 8 );
    phoneCell.setWidth( 150 );
    phoneCell.setBottom( 8 );
    phoneCell.setName( "mail" );
    phoneCell.setSelectable( true );
  }

  private void createImageCell() {
    ImageCell imageCell = new ImageCell( this );
    imageCell.setBindingIndex( 0 );
    imageCell.setTop( 4 );
    imageCell.setLeft( 4 );
    imageCell.setWidth( 48 );
    imageCell.setHeight( 48 );
    imageCell.setSelectable( true );
    imageCell.setName( "face" );
    imageCell.setScaleMode( ScaleMode.FIT );
  }

  private void createSeparatorCell() {
    TextCell cell = new TextCell( this );
    cell.setLeft( 0 );
    cell.setBottom( 0 );
    cell.setRight( 0 );
    cell.setHeight( 1 );
    cell.setBackground( new Color( Display.getCurrent(), 130, 130, 130 ) );
  }

}