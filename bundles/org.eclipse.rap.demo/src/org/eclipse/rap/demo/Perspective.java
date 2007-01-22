package org.eclipse.rap.demo;

import org.eclipse.rap.ui.*;

public class Perspective implements IPerspectiveFactory {

  public void createInitialLayout( final IPageLayout layout ) {
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible( false );
    IFolderLayout topLeft = layout.createFolder( "topLeft",
                                                 IPageLayout.LEFT,
                                                 0.25f,
                                                 editorArea );
    topLeft.addView( "org.eclipse.rap.demo.DemoTreeViewPartI" );
    topLeft.addView( "org.eclipse.rap.demo.DemoTreeViewPartII" );
    IFolderLayout bottomLeft = layout.createFolder( "bottomLeft",
                                                    IPageLayout.BOTTOM,
                                                    0.50f,
                                                    "topLeft" );
    bottomLeft.addView( "org.eclipse.rap.demo.DemoTreeViewPartIII" );
    IFolderLayout bottom = layout.createFolder( "bottom",
                                                 IPageLayout.BOTTOM,
                                                 0.60f,
                                                 editorArea );
    bottom.addView( "org.eclipse.rap.demo.DemoTableViewPart" );
    bottom.addView( "org.eclipse.rap.demo.DemoTreeViewPartIV" );
    IFolderLayout topRight = layout.createFolder( "topRight",
                                                  IPageLayout.RIGHT,
                                                  0.70f,
                                                  editorArea );
    topRight.addView( "org.eclipse.rap.demo.DemoSelectionViewPart" );
  }
}
