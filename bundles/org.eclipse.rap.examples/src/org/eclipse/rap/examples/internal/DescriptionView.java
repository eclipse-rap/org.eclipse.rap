/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rap.examples.internal.model.ExamplesModel;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;


/**
 * Shows the description of the currently selected example. The description is
 * loaded from a HTML file in a org.eclipse.swt.browser.Browser.
 */
public class DescriptionView extends ViewPart {

  public static final String ID
    = "org.eclipse.rap.examples.descriptionView";
  private static final String DOWNLOAD
    = "<html><head></head><body>"
    + "<p style=\"font: 12px Verdana, 'Lucida Sans', sans-serif;\">"
    + "<a href=\"http://rap.eclipsesource.com/download/rapdemo.war\">" 
    + "Download</a> the Examples WAR"
    + "</p>"
    + "</body></html>";
  private static final String NO_DESCRIPTION = "";
  private static final String BASE_URL = ".";

  private Browser brwDescription;
  private Browser brwDownload;

  public void createPartControl( final Composite parent ) {
    GridLayout layout = new GridLayout();
    layout.verticalSpacing = 15;
    parent.setLayout( layout );
    brwDescription = new Browser( parent, SWT.NONE );
    brwDescription.setData( WidgetUtil.CUSTOM_VARIANT, "descriptionView" );
    GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
    brwDescription.setLayoutData( gridData );
    brwDownload = new Browser( parent, SWT.NONE );
    brwDownload.setText( DOWNLOAD );
    gridData = new GridData( SWT.FILL, SWT.BOTTOM, true, false );
    gridData.heightHint = 30;
    brwDownload.setLayoutData( gridData );
    createSelectionListener();
  }

  public void setFocus() {
    brwDescription.setFocus();
  }

  private void createSelectionListener() {
    ISelectionService selectionService
      = getSite().getWorkbenchWindow().getSelectionService();
    selectionService.addSelectionListener( new ISelectionListener() {
      public void selectionChanged( final IWorkbenchPart part,
                                    final ISelection selection )
      {
        IStructuredSelection sselection = ( IStructuredSelection )selection;
        Object firstElement = sselection.getFirstElement();
        if( firstElement != null ) {
          if( firstElement instanceof String ) {
            showPage( ( String )firstElement );
          }
        } else {
          brwDescription.setText( NO_DESCRIPTION );
        }
      }
    } );
  }

  private void showPage( final String name ) {
    String descriptionPath
      = ExamplesModel.getInstance().getDescriptionUrl( name );
    if( descriptionPath != null ) {
      boolean loaded = brwDescription.setUrl( BASE_URL + descriptionPath );
      if( !loaded ) {
        brwDescription.setText( NO_DESCRIPTION );
      }
    }
  }
}
