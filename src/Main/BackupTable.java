package Main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;

public class BackupTable extends Dialog {

	protected boolean result;
	protected Shell shell;
	private int width = 500;
	private int height = 400;
	private Table fileTable;
	
	private String[] files;
	
	private String selectedFile = "";

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BackupTable(Shell parent, String[] files) {
		super(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		setText("Backup Table");
		
		this.files = files;
		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public boolean open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(width, height);
		shell.setText(getText());
		shell.setLocation(Display.getCurrent().getPrimaryMonitor().getBounds().width/2-width/2, 
				Display.getCurrent().getPrimaryMonitor().getBounds().height/2-height/2);
		
		fileTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		fileTable.setBounds(10, 10, 480, 330);
		fileTable.setHeaderVisible(true);
		fileTable.setLinesVisible(true);
		
		TableColumn tblclmnFile = new TableColumn(fileTable, SWT.LEFT);
		tblclmnFile.setWidth(478);
		tblclmnFile.setText("File");
		
		Button btnSelectFile = new Button(shell, SWT.NONE);
		btnSelectFile.setBounds(294, 346, 95, 28);
		btnSelectFile.setText("Select File");
		btnSelectFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e ) {
				selectedFile = files[fileTable.getSelectionIndex()];
				result = true;
				shell.dispose();
			}
		});
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(395, 346, 95, 28);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = false;
				shell.dispose();
			}
		});
		
		loadFiles();
	}
	
	private void loadFiles() {
		
		TableItem item;
		
		for (String string : files) {
			item = new TableItem(fileTable, SWT.NONE);
			item.setText(0, string);
		}
		
	}

	public String getFile() {
		return selectedFile;
	}
}
