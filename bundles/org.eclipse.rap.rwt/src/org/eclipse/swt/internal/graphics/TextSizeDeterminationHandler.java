/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.io.IOException;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeDetermination.ICalculationItem;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;


final class TextSizeDeterminationHandler
  implements PhaseListener, HttpSessionBindingListener
{
  private static final long serialVersionUID = 1L;
  private static final String CALCULATION_HANDLER
    = TextSizeDeterminationHandler.class.getName() + ".CalculationHandler";

  ICalculationItem[] calculationItems;
  boolean renderDone;
  private final Display display;
  private IProbe[] probes;

  static void register() {
    Display display = Display.getCurrent();
    if( display != null && display.getThread() == Thread.currentThread() ) {
      ISessionStore session = ContextProvider.getSession();
      if( session.getAttribute( CALCULATION_HANDLER ) == null ) {
        TextSizeDeterminationHandler handler
          = new TextSizeDeterminationHandler( display );
        session.setAttribute( CALCULATION_HANDLER, handler );
        LifeCycleFactory.getLifeCycle().addPhaseListener( handler );
      }
    }
  }

  TextSizeDeterminationHandler( final Display display ) {
    this.display = display;
  }

  //////////////////////////
  // interface PhaseListener

  public void beforePhase( final PhaseEvent event ) {
  }

  public void afterPhase( final PhaseEvent event ) {
    if( display == Display.getCurrent() ) {
      try {
        if( renderDone && event.getPhaseId() == PhaseId.PROCESS_ACTION ) {
          readProbedFonts( probes );
          readMeasuredStrings();
          Shell[] shells = display.getShells();
          for( int i = 0; i < shells.length; i++ ) {
            // TODO [fappel]: Think about a lighter recalculation trigger.
            Point buffer = shells[ i ].getSize();
            AllWidgetTreeVisitor clearLayout = new AllWidgetTreeVisitor() {
              public boolean doVisit( final Widget widget ) {
                if( widget instanceof Composite ) {
                  Composite composite = ( Composite )widget;
                  composite.changed( composite.getChildren() );
                }
                return true;
              }
            };
            // TODO [rst] Special handling for ScrolledComposites:
            //            Resizing makes SCs forget about their scroll position.
            final String scOriginKey = "org.eclipse.rap.sc-origin";
            final String scContentSizeKey = "org.eclipse.rap.content-size";
            AllWidgetTreeVisitor saveSCOrigins = new AllWidgetTreeVisitor() {
              public boolean doVisit( final Widget widget ) {
                if( widget instanceof ScrolledComposite ) {
                  ScrolledComposite composite = ( ScrolledComposite )widget;
                  composite.setData( scOriginKey, composite.getOrigin() );
                  Control content = composite.getContent();
                  if( content != null ) {
                    content.setData( scContentSizeKey, content.getSize() );
                  }
                }
                return true;
              }
            };
            AllWidgetTreeVisitor restoreSCOrigins = new AllWidgetTreeVisitor() {
              public boolean doVisit( final Widget widget ) {
                // restore sc origins
                if( widget instanceof ScrolledComposite ) {
                  ScrolledComposite composite = ( ScrolledComposite )widget;
                  Point oldOrigin = ( Point )composite.getData( scOriginKey );
                  if( oldOrigin != null ) {
                    composite.setOrigin( oldOrigin );
                    composite.setData( scOriginKey, null );

                    Control content = composite.getContent();
                    if( content != null ) {
                      Point size = ( Point )content.getData( scContentSizeKey );
                      if( size != null ) {
                        content.setSize( size );
                        content.setData( scContentSizeKey, null );
                      }
                    }

                  }
                }
                return true;
              }
            };
            WidgetTreeVisitor.accept( shells[ i ], saveSCOrigins );
            WidgetTreeVisitor.accept( shells[ i ], clearLayout );
            shells[ i ].setSize( buffer.x + 1000, buffer.y + 1000 );
            WidgetTreeVisitor.accept( shells[ i ], clearLayout );
            shells[ i ].setSize( buffer );
            WidgetTreeVisitor.accept( shells[ i ], restoreSCOrigins );
          }
        }
        if( event.getPhaseId() == PhaseId.RENDER ) {
          probes = TextSizeDeterminationFacade.writeFontProbing();
          calculationItems
            = TextSizeDeterminationFacade.writeStringMeasurements();
          renderDone = true;
        }
      } catch( final IOException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        if( renderDone && event.getPhaseId() == PhaseId.PROCESS_ACTION ) {
          LifeCycleFactory.getLifeCycle().removePhaseListener( this );
          ISessionStore session = ContextProvider.getSession();
          session.removeAttribute( CALCULATION_HANDLER );
        }
      }
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  public static void readProbedFonts( final IProbe[] probes ) {
    boolean hasProbes = probes != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasProbes && i < probes.length; i++ ) {
      IProbe probe = probes[ i ];
      String name = String.valueOf( probe.getFont().hashCode() );
      String value = request.getParameter( name );
      if( value != null ) {
        Point size = getSize( value );
        TextSizeProbeStore.getInstance().createProbeResult( probe, size );
      }
    }
  }

  ///////////////////////////////////////
  // interface HttpSessionBindingListener

  public void valueBound( final HttpSessionBindingEvent event ) {
  }

  public void valueUnbound( final HttpSessionBindingEvent event ) {
    UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        ILifeCycle lifeCycle = LifeCycleFactory.getLifeCycle();
        lifeCycle.removePhaseListener( TextSizeDeterminationHandler.this );
      }
    } );
  }

  //////////////////
  // helping methods

  void readMeasuredStrings() {
    boolean hasItems = calculationItems != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasItems && i < calculationItems.length; i++ ) {
      ICalculationItem item = calculationItems[ i ];
      String name = String.valueOf( item.hashCode() );
      String value = request.getParameter( name );
      // TODO [fappel]: Workaround for background process problem
      if( value != null ) {
        Point size = getSize( value );
        TextSizeDataBase.store( item.getFont(),
                                item.getString(),
                                item.getWrapWidth(),
                                size );
      }
    }
  }

  private static Point getSize( final String value ) {
    String[] split = value.split( "," );
    return new Point( Integer.parseInt( split[ 0 ] ),
                      Integer.parseInt( split[ 1 ] ) );
  }
}