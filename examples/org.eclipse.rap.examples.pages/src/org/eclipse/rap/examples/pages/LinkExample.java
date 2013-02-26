/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;


public class LinkExample implements IExamplePage {

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createFillLayout( true ) );
    parent.setLayoutData( ExampleUtil.createFillData() );
    final Link link2 = new Link( parent, SWT.NONE );
    link2.setText( "The <a href=\"http://www.eclipse.org/rap/\">RAP project</a> enables developers to build rich, Ajax-enabled Web applications by using \nthe Eclipse development model, plug-ins with the well known Eclipse workbench extension \npoints, JFace, and a widget toolkit with SWT API (using <a>qooxdoo</a> for the client-side presentation). " );
    link2.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        UrlLauncher service = RWT.getClient().getService( UrlLauncher.class );
        if( service != null ) {
          service.openURL( e.text );
        }
      }
    } );
  }
}
