package org.eclipse.rap.e4.demo.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SwitchPerspectives {
	@Execute
	void switchPerspective(EPartService partService, MPerspective perspective) {
		if( perspective.getElementId().equals("org.eclipse.rap.e4.demo.perspective.0") ) {
			partService.switchPerspective((MPerspective) perspective.getParent().getChildren().get(1));
		} else {
			partService.switchPerspective((MPerspective) perspective.getParent().getChildren().get(0));
		}		
	}
}
