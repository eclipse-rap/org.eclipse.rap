/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples;

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class Infobox extends Composite {

  private Composite contentComp;

  public Infobox( Composite parent ) {
    super( parent, SWT.NONE );
    setLayout( ExampleUtil.createGridLayout( 1, false, true, false ) );
    setLayoutData( ExampleUtil.createFillData() );
    contentComp = createInfoboxContentComposite();
  }

  private Composite createInfoboxContentComposite() {
    Composite contentComp = new Composite( this, SWT.NONE );
    contentComp.setBackgroundMode( SWT.INHERIT_FORCE );
    contentComp.setData( WidgetUtil.CUSTOM_VARIANT, "infobox" );
    GridLayout layout = ExampleUtil.createGridLayoutWithoutMargin( 1, false );
    layout.marginHeight = 35;
    layout.marginWidth = 35;
    layout.verticalSpacing = 20;
    contentComp.setLayout( layout );
    contentComp.setLayoutData( ExampleUtil.createHorzFillData() );
    return contentComp;
  }

  public void addHeading( String text ) {
    Label label = new Label( contentComp, SWT.NONE );
    label.setText( text.replace( "&", "&&" ) );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "infobox-heading" );
  }

  public void addParagraph( String text ) {
    Label label = new Label( contentComp, SWT.WRAP );
    label.setText( text );
    label.setLayoutData( ExampleUtil.createFillData() );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "infobox" );
  }

}
