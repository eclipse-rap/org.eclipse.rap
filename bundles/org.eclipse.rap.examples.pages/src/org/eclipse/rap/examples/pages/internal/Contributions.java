/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages.internal;

import java.util.*;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.*;


class Contributions {

  private List<IExampleContribution> contributions;

  Contributions() {
    contributions = new ArrayList<IExampleContribution>();
    collectContributions();
  }

  List<IExampleContribution> getContibutions() {
    return Collections.unmodifiableList( contributions );
  }

  private void collectContributions() {
    addContribution( "input", "Input Widgets", null, InputExamplePage.class );
    addContribution( "dialog", "Dialogs", null, DialogExamplePage.class );
    addContribution( "drag-and-drop", "Drag & Drop", null, ListExample.class );
    addContribution( "treeviewer", "TreeViewer", "Tree and Table", TreeViewerExample.class );
    addContribution( "tableviewer", "TableViewer", "Tree and Table", TableViewerExample.class );
    addContribution( "canvas", "Canvas", null, CanvasExamplePage.class );
    addContribution( "row-layout", "Row Layout", "Layouts", RowLayoutExample.class );
    addContribution( "fill-layout", "Fill Layout", "Layouts" ,FillLayoutExample.class );
    addContribution( "grid-layout", "Grid Layout", "Layouts" ,GridLayoutExample.class );
  }

  private void addContribution( final String id,
                                final String title,
                                final String category,
                                final Class<? extends IExamplePage> clazz )
  {
    IExampleContribution contribution = new IExampleContribution() {

      public String getId() {
        return id;
      }

      public String getTitle() {
        return title;
      }
      
      public String getCategory() {
        return category;
      }

      public IExamplePage createPage() {
        try {
          return clazz.newInstance();
        } catch( Exception exception ) {
          throw new RuntimeException( "Failed to instatiate class " + clazz.getName(), exception );
        }
      }
    };
    contributions.add( contribution );
  }
}
