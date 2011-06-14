/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal.model;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.internal.Activator;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.ui.statushandlers.StatusManager;


/**
 * Model for the RAP examples plugin.
 * <p>
 * Scans the org.eclipse.rap.examples.widgets extensions and builds a
 * categorized list of examples names and a list of IExamplePage instances.
 */
public final class ExamplesModel {

  private final static String EXT_POINT = "org.eclipse.rap.examples.pages";
  private static final String ELEM_CATEGORY = "category";
  private static final String ELEM_PAGE = "page";
  private static final String EXT_ATTR_ID = "id";
  private final static String EXT_ATTR_NAME = "name";
  private final static String EXT_ATTR_CATEGORY = "category";
  private final static String EXT_ATTR_DESCRIPTION = "description";
  private final static String EXT_ATTR_CLASS = "class";

  private final List<ExampleCategory> categories;
  private final List<PageWrapper> pages;

  private ExamplesModel() {
    categories = new ArrayList<ExampleCategory>();
    pages = new ArrayList<PageWrapper>();
    IConfigurationElement[] extensions = readRegistry();
    createCategories( extensions );
    createPages( extensions );
  }

  public static ExamplesModel getInstance() {
    // TODO [rh] this instance should have application scope
    Object instance = SessionSingletonBase.getInstance( ExamplesModel.class );
    return ( ExamplesModel )instance;
  }

  /**
   * Returns a list of all categories.
   *
   * @return ExampleCategory[]
   */
  public ExampleCategory[] getCategories() {
    ExampleCategory[] result = new ExampleCategory[ categories.size() ];
    categories.toArray( result );
    return result;
  }

  /**
   * Returns the names of the examples which belong to a particular category.
   *
   * @param category
   * @return String[] with example names
   */
  public String[] getExamplesInCategory( final ExampleCategory category ) {
    List<String> resultList = new ArrayList<String>();
    String catId = category.getId();
    Iterator iterator = pages.iterator();
    while( iterator.hasNext() ) {
      PageWrapper pageWrapper = ( PageWrapper )iterator.next();
      if( catId.equals( pageWrapper.categoryId ) ) {
        resultList.add( pageWrapper.name );
      }
    }
    String[] result = new String[ resultList.size() ];
    resultList.toArray( result );
    return result;
  }

  public IExamplePage getExample( final String name ) {
    PageWrapper pageWrapper = getWrapper( name );
    return pageWrapper == null ? null : pageWrapper.page;
  }

  public String getDescriptionUrl( final String name ) {
    PageWrapper pageWrapper = getWrapper( name );
    return pageWrapper == null ? null : pageWrapper.descriptionUrl;
  }

  private static IConfigurationElement[] readRegistry() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    return registry.getConfigurationElementsFor( EXT_POINT );
  }

  private void createCategories( final IConfigurationElement[] extensions ) {
    for( int i = 0; i < extensions.length; i++ ) {
      IConfigurationElement extension = extensions[ i ];
      if( ELEM_CATEGORY.equals( extension.getName() ) ) {
        String id = extension.getAttribute( EXT_ATTR_ID );
        String name = extension.getAttribute( EXT_ATTR_NAME );
        categories.add( new ExampleCategory( id, name ) );
      }
    }
  }

  private void createPages( final IConfigurationElement[] extensions ) {
    for( int i = 0; i < extensions.length; i++ ) {
      IConfigurationElement extension = extensions[ i ];
      if( ELEM_PAGE.equals( extension.getName() ) ) {
        String name = extension.getAttribute( EXT_ATTR_NAME );
        String categoryId = extension.getAttribute( EXT_ATTR_CATEGORY );
        IExamplePage page = null;
        try {
          Object instance
            = extension.createExecutableExtension( EXT_ATTR_CLASS );
          page = ( IExamplePage )instance;
        } catch( CoreException e ) {
          StatusManager.getManager().handle( e, Activator.PLUGIN_ID );
        }
        String descriptionUrl = getDescriptionUrl( extension );
        pages.add( new PageWrapper( name, categoryId, descriptionUrl, page ) );
      }
    }
  }

  private static String getDescriptionUrl(
    final IConfigurationElement extension )
  {
    StringBuffer result = new StringBuffer();
    result.append( "/" );
    result.append( extension.getContributor().getName() );
    String descriptionPath = extension.getAttribute( EXT_ATTR_DESCRIPTION );
    if( descriptionPath != null ) {
      if( !descriptionPath.startsWith( "/" ) ) {
        result.append( "/" );
      }
      result.append( descriptionPath );
    }
    return result.toString();
  }

  private PageWrapper getWrapper( final String name ) {
    PageWrapper result = null;
    Iterator iterator = pages.iterator();
    while( iterator.hasNext() ) {
      PageWrapper pageWrapper = ( PageWrapper )iterator.next();
      if( name.equals( pageWrapper.name ) ) {
        result = pageWrapper;
      }
    }
    return result;
  }

  private static final class PageWrapper {
    public final String name;
    public final String categoryId;
    public final String descriptionUrl;
    public final IExamplePage page;

    public PageWrapper( final String name,
                        final String categoryId,
                        final String descriptionUrl,
                        final IExamplePage page )
    {
      this.name = name;
      this.categoryId = categoryId;
      this.descriptionUrl = descriptionUrl;
      this.page = page;
    }
  }
}
