/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.ButtonExamplePage;
import org.eclipse.rap.examples.pages.CanvasExamplePage;
import org.eclipse.rap.examples.pages.DialogExamplePage;
import org.eclipse.rap.examples.pages.FillLayoutExample;
import org.eclipse.rap.examples.pages.GridLayoutExample;
import org.eclipse.rap.examples.pages.InternationalizationExamplePage;
import org.eclipse.rap.examples.pages.ListExample;
import org.eclipse.rap.examples.pages.MarkupExample;
import org.eclipse.rap.examples.pages.MarkupLabelExample;
import org.eclipse.rap.examples.pages.RowLayoutExample;
import org.eclipse.rap.examples.pages.RowTemplateExample;
import org.eclipse.rap.examples.pages.TableViewerExample;
import org.eclipse.rap.examples.pages.TextInputExamplePage;
import org.eclipse.rap.examples.pages.TreeViewerExample;
import org.eclipse.rap.examples.pages.ValidationExamplePage;


class Contributions {

  private final List<IExampleContribution> contributions;

  Contributions() {
    contributions = new ArrayList<IExampleContribution>();
    collectContributions();
  }

  List<IExampleContribution> getContibutions() {
    return Collections.unmodifiableList( contributions );
  }

  private void collectContributions() {
    addContribution( "button", "Buttons", ButtonExamplePage.class );
    addContribution( "rich-label", "Markup Support", MarkupLabelExample.class );
    addContribution( "input", "Input Widgets", TextInputExamplePage.class );
    addContribution( "dialog", "Dialogs", DialogExamplePage.class );
    addContribution( "drag-and-drop", "Drag & Drop", ListExample.class );
    addContribution( "treeviewer", "TreeViewer", TreeViewerExample.class );
    addContribution( "tableviewer", "TableViewer", TableViewerExample.class );
    addContribution( "canvas", "Canvas", CanvasExamplePage.class );
    addContribution( "row-layout", "Row Layout", RowLayoutExample.class );
    addContribution( "fill-layout", "Fill Layout", FillLayoutExample.class );
    addContribution( "grid-layout", "Grid Layout", GridLayoutExample.class );
    addContribution( "table-markup", "Table with Markup", MarkupExample.class );
    addContribution( "table-template", "Table with RowTemplate", RowTemplateExample.class );
    addContribution( "nls", "Internationalization", InternationalizationExamplePage.class );
    addContribution( "validation", "Input Validation", ValidationExamplePage.class );
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
