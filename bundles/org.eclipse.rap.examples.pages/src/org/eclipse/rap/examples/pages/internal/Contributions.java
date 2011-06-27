/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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
import org.eclipse.rap.examples.demo.fileupload.FileUploadExamplePage;
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
    addContribution( "input", "Input Widgets", InputExamplePage.class );
    addContribution( "dialog", "Dialogs", DialogExamplePage.class );
    addContribution( "drag-and-drop", "Drag & Drop", ListExample.class );
    addContribution( "treeviewer", "TreeViewer", TreeViewerExample.class );
    addContribution( "tableviewer", "TableViewer", TableViewerExample.class );
    addContribution( "complex-data", "Complex Data", EnronExamplePage.class );
    addContribution( "canvas", "Canvas", CanvasExamplePage.class );
    addContribution( "row-layout", "Row Layout", RowLayoutExample.class );
    addContribution( "fill-layout", "Fill Layout", FillLayoutExample.class );
    addContribution( "grid-layout", "Grid Layout", GridLayoutExample.class );
    addContribution( "oscilloscope", "Oscilloscope", OscilloscopeExample.class );
    addContribution( "file-upload", "File Upload", FileUploadExamplePage.class );
  }

  private void addContribution( final String id,
                                final String title,
                                final Class<? extends IExamplePage> clazz )
  {
    IExampleContribution contribution = new IExampleContribution() {

      public String getId() {
        return id;
      }

      public String getTitle() {
        return title;
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
