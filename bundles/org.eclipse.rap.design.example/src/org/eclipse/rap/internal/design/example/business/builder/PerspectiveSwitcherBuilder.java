/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.rap.internal.design.example.business.layoutsets.PerspectiveSwitcherInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.preferences.SessionScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class PerspectiveSwitcherBuilder extends ElementBuilder {
  
  private static final String RAP_PERSPECTIVES = "RAP_PERSPECTIVES";
  
  private Composite background;
  private Map perspectiveButtonMap;
  private Map buttonPerspectiveMap;
  private List buttonList;
  private List perspectiveList;
  private Button otherButton;
  
  private PerspectiveAdapter perspectiveAdapter = new PerspectiveAdapter() {

    public void perspectiveActivated( 
      final IWorkbenchPage page,
      final IPerspectiveDescriptor perspective )
    {
      addIdToStore( perspective.getId() );
      
      Button button = createPerspectiveButton( perspective );
      cleanButtons( button );

      String activeId = PerspectiveSwitcherInitializer.ACTIVE;
      Font active = getLayoutSet().getFont( activeId );
      button.setFont( active );
      
      background.layout( true );
      Control[] children = { background };
      Composite parent = getParent();
      parent.changed( children );
      parent.layout( true );
      parent.getParent().layout( true );      
    }    
  };

  public PerspectiveSwitcherBuilder( 
    final Composite parent, final String subSetId )
  {
    super( parent, subSetId );
    background = new Composite( parent, SWT.NONE );
    background.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    RowLayout layout = new RowLayout();
    background.setLayout( layout );
    layout.spacing = 15;
    
    perspectiveButtonMap = new HashMap();
    buttonPerspectiveMap = new HashMap();
    buttonList = new ArrayList();
    perspectiveList = new ArrayList();
  }

  public void addControl( final Control control, final Object layoutData ) {
  }

  public void addControl( final Control control, final String positionId ) {
  }

  private void addIdToStore( final String id ) {
    if( !perspectiveList.contains( id ) ) {
      perspectiveList.add( id );
    }
    save();
  }

  public void addImage( final Image image, final Object layoutData ) {
  }

  public void addImage( final Image image, final String positionId ) {
  }
  
  public void build() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    workbenchWindow.addPerspectiveListener( perspectiveAdapter );
    
    IPerspectiveDescriptor[] descriptors = load();
    for( int i = 0; i < descriptors.length; i++ ) {
      createPerspectiveButton( descriptors[ i ] );
    }
    
    // Button for the perspective dialog
    otherButton = new Button( background, SWT.PUSH | SWT.FLAT );
    otherButton.setData( WidgetUtil.CUSTOM_VARIANT, "inactivePerspective" );
    otherButton.setText( "other..." );
    String inactive = PerspectiveSwitcherInitializer.INACTIVE;
    otherButton.setFont( getLayoutSet().getFont( inactive ) );
    IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
    final IWorkbenchAction perspectiveAction 
      = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create( activeWindow );
    otherButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        perspectiveAction.run();
      }
    } );

  }
  
  private void cleanButtons( final Button current ) {
    String inactive = PerspectiveSwitcherInitializer.INACTIVE;
    Font font = getLayoutSet().getFont( inactive );
    
    for( int i = 0; i < buttonList.size(); i++ ) {
      Button button = ( Button ) buttonList.get( i );
      if( !button.equals( current ) ) {        
        button.setFont( font );
      }
    }
  }

  private void cleanUpButton( 
    final IPerspectiveDescriptor perspective, final Button button )
  {
    buttonList.remove( button );
    perspectiveButtonMap.remove( perspective );
    buttonPerspectiveMap.remove( button );
    button.dispose();
    background.layout( true );
    Control[] children = { background };
    Composite parent = getParent();
    parent.changed( children );
    parent.layout( true );
    parent.getParent().layout( true );        
  }
  
  private void closePerspective( final IPerspectiveDescriptor desc ) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage activePage = workbenchWindow.getActivePage();
    activePage.closePerspective( desc, true, false );    
  }

  private Button createPerspectiveButton( final IPerspectiveDescriptor desc ) {
    
    Button result = (  Button ) perspectiveButtonMap.get( desc );
    if( result == null && desc != null && desc.getLabel() != null ) {    
      final Button perspButton = new Button( background, SWT.PUSH | SWT.FLAT );
      perspButton.setData( WidgetUtil.CUSTOM_VARIANT, "inactivePerspective" );

      LayoutSet layoutSet = getLayoutSet();
      String inactive = PerspectiveSwitcherInitializer.INACTIVE;
      perspButton.setFont( layoutSet.getFont( inactive ) );      
      
      perspButton.setText( desc.getLabel() );
      
      perspectiveButtonMap.put( desc, perspButton );
      buttonPerspectiveMap.put( perspButton, desc );
      buttonList.add( perspButton );
      
      perspButton.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( SelectionEvent e ) {
          cleanButtons( perspButton );
          switchPerspective( desc.getId() );             
        }
      } );     
      
      Menu menu = new Menu( perspButton );
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText( "close" );
      item.setImage( getImage( PerspectiveSwitcherInitializer.CLOSE ) );
      item.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( SelectionEvent e ) {
          removeIdFromStore( desc.getId() );
          Button button = ( Button ) perspectiveButtonMap.get( desc);
          if( button != null ) {
            cleanUpButton( desc, button );
          }
          closePerspective( desc );
          background.layout();
        }
      } );
      perspButton.setMenu( menu );
      if( otherButton != null ) {
        otherButton.moveBelow( perspButton );
      }
      result = perspButton;
    }
    
    return result;
  }
  
  private IEclipsePreferences createSessionScope() {
    return new SessionScope().getNode( RAP_PERSPECTIVES );
  }
  
  public void dispose() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    workbenchWindow.removePerspectiveListener( perspectiveAdapter );
    Composite parent = getParent();
    if( parent != null && !parent.isDisposed() ) {
      parent.dispose();
    }
    
  }

  public Control getControl() {
    return background;
  }

  public Point getSize() {
    return null;
  }
  
  private IPerspectiveDescriptor[] load() {
    Preferences store = createSessionScope();
    String[] keys;
    IPerspectiveDescriptor[] result = null;
    
    IWorkbench workbench = PlatformUI.getWorkbench();
    IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();
    
    try {
      keys = store.keys();
      result = new IPerspectiveDescriptor[ keys.length ];
      for( int i = 0; i < keys.length; i++ ) {
        String perspectiveId = keys[ i ];
        
        int pos = store.getInt( perspectiveId, 0 );
        if( pos <= perspectiveList.size() ) {
          perspectiveList.add( pos, perspectiveId );
        } else {
          perspectiveList.add( perspectiveId );
        }
      }
      for( int i = 0; i < perspectiveList.size(); i++ ) {
        String id = ( String ) perspectiveList.get( i );
        result[ i ] = registry.findPerspectiveWithId( id );
      }
      
    } catch( BackingStoreException e ) {
      e.printStackTrace();
    }
    return result;    
  }
  
  private void removeIdFromStore( final String id ) {
    perspectiveList.remove( id );
    Preferences store = createSessionScope();
    store.remove( id );
    save();
  }
  
  private void save() {
    Preferences store = createSessionScope();
    try {
      store.clear();
      for( int i = 0; i < perspectiveList.size(); i++ ) {
        String id = ( String ) perspectiveList.get( i );
        store.putInt( id, i );     
      }
      store.flush();
    } catch( BackingStoreException e ) {
      e.printStackTrace();
    }    
  }

  private void switchPerspective( final String perspectiveId) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();  
    try {
      workbench.showPerspective( perspectiveId, window );
    } catch( WorkbenchException e ) {
      e.printStackTrace();
    }    
  }
}
