package openexplorer.actions;
/**
 * Copyright (c) 2011 zhiyanan
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import openexplorer.util.Messages;
import openexplorer.util.OperatingSystem;

public class DosHere implements IActionDelegate {

	protected ISelection currentSelection;
	protected IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

	public DosHere() {
		// TODO Auto-generated constructor stub
	}

	
	public void run(IAction action) {

		
		if (this.currentSelection == null || this.currentSelection.isEmpty()) {
			return;
		}
		if (this.currentSelection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) this.currentSelection;

			TreePath[] paths = treeSelection.getPaths();

			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				IResource resource = null;
				Object segment = path.getLastSegment();
				if ((segment instanceof IResource))
					resource = (IResource) segment;

				if (resource == null) {
					continue;
				}
				String location = resource.getLocation().toOSString();
				if ((resource instanceof IFile)) {
					location = ((IFile) resource).getParent().getLocation().toOSString();
				}
				openInTerminal( location);
				return ;
			}
		} else if (this.currentSelection instanceof ITextSelection
				|| this.currentSelection instanceof IStructuredSelection) {
			IEditorPart editor = window.getActivePage().getActiveEditor();
			if (editor != null) {
				IFile current_editing_file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
				String location = current_editing_file.getParent().getLocation().toOSString();
				
				openInTerminal( location);
				return ;
			}
		}
		
		MessageDialog.openError(window.getShell(), Messages.OpenExploer_Error, Messages.Cant_Open + " Unsupport selection:" + this.currentSelection);
	
	}
	
	protected void openInTerminal(String location) {
		try {
			if (OperatingSystem.INSTANCE.isWindows()) {
				Runtime.getRuntime().exec("cmd /c start cmd /k cd /d " + " \"" + location + "\"");
			} else {
				MessageDialog.openError(window.getShell(), Messages.OpenExploer_Error, Messages.Cant_Open + " Only support windows now");
			}
		} catch (IOException e) {
			MessageDialog.openError(window.getShell(), Messages.OpenExploer_Error, Messages.Cant_Open + " \"" + location + "\"");
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;

	}

}
