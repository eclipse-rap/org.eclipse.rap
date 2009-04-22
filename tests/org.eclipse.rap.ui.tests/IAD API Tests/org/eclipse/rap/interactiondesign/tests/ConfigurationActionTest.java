/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.interactiondesign.tests.impl.ConfigurableStackImpl;
import org.eclipse.rap.junit.RAPTestCase;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.ConfigurationAction;
import org.eclipse.rap.ui.interactiondesign.IConfigurationChangeListener;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.ViewPane;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;


public class ConfigurationActionTest extends RAPTestCase {
  
  
  private ConfigurableStackProxy proxy;
  private ConfigurableStack stack;
  private IStackPresentationSite site;
  private ConfigurationAction configAction;
  private boolean presentationChanged;
  private boolean toolBarChanged;
  
  protected void setUp() throws Exception {
    if( stack == null ) {
      stack = getConfigurableStack();
    }
    if( site == null ) {
      site = stack.getSite();
    }
    if( configAction == null ) {
      configAction = stack.getConfigAction();
    }
  }
  
  public void testSaveAndLoadStackId() {
    ConfigurationAction configAction = stack.getConfigAction();
    String id = stack.getStackPresentationId();
    String savedStackId = ConfigurableStack.getSavedStackId( site );
    String defaultString = IPreferenceStore.STRING_DEFAULT_DEFAULT;
    assertEquals( defaultString, savedStackId );
    configAction.saveStackPresentationId( id );
    savedStackId = ConfigurableStack.getSavedStackId( site );
    assertEquals( id, savedStackId );
    configAction.saveStackPresentationId( defaultString );
    savedStackId = ConfigurableStack.getSavedStackId( site );
    assertEquals( defaultString, savedStackId );    
  } 
  
  public void testGetActionIdFromToolItem() {    
    ToolBar bar = new ToolBar( stack.getParent(), SWT.HORIZONTAL );
    ToolItem item = new ToolItem( bar, SWT.PUSH );
    Action action = new Action() { };
    String id = "org.eclipse.rap.actionIDTest";
    action.setId( id );
    ActionContributionItem actionItem = new ActionContributionItem( action );
    item.setData( actionItem );
    String actualId = ConfigurationAction.getActionIdFromToolItem( item );
    assertEquals( id, actualId );    
  }
  
  public void testConfigurationChangeListener() {
    presentationChanged = false;
    toolBarChanged = false;
    IConfigurationChangeListener listener = new IConfigurationChangeListener() {
      public void presentationChanged( String newStackPresentationId ) {
        presentationChanged = true;
      }

      public void toolBarChanged() {
        toolBarChanged = true;        
      }      
    };
    configAction.addConfigurationChangeListener( listener );
    configAction.fireLayoutChange( "newId" );
    assertTrue( presentationChanged );
    configAction.fireToolBarChange();
    assertTrue( toolBarChanged );
    presentationChanged = false;
    toolBarChanged = false;
    configAction.removeLayoutChangeListener( listener );
    configAction.fireLayoutChange( "newID" );
    assertFalse( presentationChanged );
    configAction.fireToolBarChange();
    assertFalse( toolBarChanged );    
  }
  
  public void testGetSite() {
    IStackPresentationSite actualSite = configAction.getSite();
    assertEquals( site, actualSite );  
  }
  
  public void testGetStackPresentation() {
    StackPresentation presentation = configAction.getStackPresentation();
    assertEquals( stack, presentation );
  }
  
  public void testHasPartMenu() {
    boolean hasPartMenu = configAction.hasPartMenu();
    assertFalse( hasPartMenu );
    PresentablePart part = ( PresentablePart ) site.getSelectedPart();
    PartPane pane = part.getPane();
    if( pane instanceof ViewPane ) {
      MenuManager menuManager = ( ( ViewPane ) pane ).getMenuManager();
      menuManager.add( new Action() { } );
    }
    hasPartMenu = configAction.hasPartMenu();
    assertTrue( hasPartMenu );
  }
  
  public void testPartMenuVisibility() {
    boolean visible = configAction.isPartMenuVisible();
    assertFalse( visible );
    configAction.savePartMenuVisibility( true );
    visible = configAction.isPartMenuVisible();
    assertTrue( visible );
    configAction.savePartMenuVisibility( false );
    visible = configAction.isPartMenuVisible();
    assertFalse( visible );    
  }
  
  public void testActionVisibility() {
    String viewId = "org.eclipse.view";
    String actionId = "org.eclipse.action";
    boolean visible = configAction.isViewActionVisibile( viewId, actionId );
    assertFalse( visible );
    configAction.saveViewActionVisibility( viewId, actionId, true );
    visible = configAction.isViewActionVisibile( viewId, actionId );
    assertTrue( visible );
    configAction.saveViewActionVisibility( viewId, actionId, false );
    visible = configAction.isViewActionVisibile( viewId, actionId );
    assertFalse( visible );
  }
  

  private ConfigurableStack getConfigurableStack() {
    ConfigurableStack result = null;
    PresentationFactory factory 
      = PresentationFactoryTest.getPresentationFactory();
    Object adapter = factory.getAdapter( StackPresentation.class );
    assertTrue( adapter instanceof List );
    List proxyList = ( List ) adapter;
    assertTrue( proxyList.size() > 0 );
    boolean found = false;
    for( int i = 0; i < proxyList.size() && !found; i++ ) {
      Object element = proxyList.get( i );
      assertTrue( element instanceof ConfigurableStackProxy );
      proxy = ( ConfigurableStackProxy ) element;
      ConfigurableStack stack = proxy.getCurrentStackPresentation();
      if( stack instanceof ConfigurableStackImpl ) {
        found = true;
        result = ( ConfigurableStack ) stack;
      }
    }
    return result;
  }
}
