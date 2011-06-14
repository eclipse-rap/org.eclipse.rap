/*******************************************************************************
 * Copyright (c) 2008, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.internal.model.ExamplesModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;


public class ExampleView extends ViewPart {
  public static final String ID = "org.eclipse.rap.examples.exampleView";
  
  private final Map<String,Composite> examplePages;
  private PageBook book;
  private SelectionListener selectionListener;

  public ExampleView() {
    examplePages = new HashMap<String,Composite>();
  }
  
  public void createPartControl( Composite parent ) {
    book = new PageBook( parent, 0 );
    selectionListener = new SelectionListener();
    ISelectionService selectionService = getSelectionService();
    selectionService.addSelectionListener( selectionListener );
  }

  public void setFocus() {
    book.setFocus();
  }

  public void dispose() {
    ISelectionService selectionService = getSelectionService();
    selectionService.removeSelectionListener( selectionListener );
    super.dispose();
  }

  private void showPage( String name ) {
    book.showPage( createPage( name ) );
  }

  private Composite createPage( String name ) {
    Composite result = examplePages.get( name );
    if( result == null ) {
      Composite examplePage = new Composite( book, SWT.V_SCROLL );
      IExamplePage page = ExamplesModel.getInstance().getExample( name );
      page.createControl( examplePage );
      examplePages.put( name, examplePage );
      result = examplePage;
    }
    return result;
  }

  private ISelectionService getSelectionService() {
    return getSite().getWorkbenchWindow().getSelectionService();
  }

  private final class SelectionListener implements ISelectionListener {
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
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
