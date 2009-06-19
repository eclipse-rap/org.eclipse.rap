/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.viewer.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rap.examples.viewer.IExamplePage;
import org.eclipse.rap.examples.viewer.internal.model.ExamplesModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;


public class ExampleView extends ViewPart {

  public static final String ID = "org.eclipse.rap.examples.viewer.exampleView";
  private PageBook book;
  private Map examplePages = new HashMap();
  private SelectionListener selectionListener;

  public void createPartControl( final Composite parent ) {
    book = new PageBook( parent, 0 );
    selectionListener = new SelectionListener();
    ISelectionService selectionService
      = getSite().getWorkbenchWindow().getSelectionService();
    selectionService.addSelectionListener( selectionListener );
  }

  public void setFocus() {
    book.setFocus();
  }

  public void dispose() {
    ISelectionService selectionService
      = getSite().getWorkbenchWindow().getSelectionService();
    selectionService.removeSelectionListener( selectionListener );
    super.dispose();
  }

  private void showPage( final String name ) {
    book.showPage( createPage( name ) );
  }

  private Composite createPage( final String name ) {
    Composite result = ( Composite )examplePages.get( name );
    if( result == null ) {
      Composite exPage = new Composite( book, SWT.V_SCROLL );
      IExamplePage page = ExamplesModel.getInstance().getExample( name );
      page.createControl( exPage );
      examplePages.put( name, exPage );
      result = exPage;
    }
    return result;
  }

  private final class SelectionListener implements ISelectionListener {

    public void selectionChanged( final IWorkbenchPart part,
                                  final ISelection selection )
    {
      if( selection instanceof IStructuredSelection ) {
        IStructuredSelection sselection = ( IStructuredSelection )selection;
        Object firstElement = sselection.getFirstElement();
        if( firstElement instanceof String ) {
          showPage( ( String )firstElement );
        }
      }
    }
  }
}
