/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.viewer.IExamplePage;
import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

public class LinkExample implements IExamplePage {

  public void createControl( final Composite parent ) {
    final Link link2 = new Link( parent, SWT.NONE );
    link2.setText( "The <a href=\"http://www.eclipse.org/rap/\">RAP project</a> enables developers to build rich, Ajax-enabled Web applications by using \nthe Eclipse development model, plug-ins with the well known Eclipse workbench extension \npoints, JFace, and a widget toolkit with SWT API (using <a>qooxdoo</a> for the client-side presentation). " );
    link2.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent e ) {
        ExternalBrowser.open( "foo", e.text, SWT.NONE );
      }
    } );
  }
}
