package Main;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import Files.FileToBackup;

public class Gui extends Dialog implements UserInterface {

	private static final String VERSION = "1.0";
	
	protected boolean result;
	protected Shell shell;
	private Text fileToBackup;
	private Text fileToRestore;
	private Text fileToDelete;
	private int width = 450;
	private int height = 360;
	private Button btnBackupFile;
	private Button btnBrowseBackUp;
	private Button btnBackup;
	private Spinner repDeg;
	private Button btnRestoreFile;
	private Button btnSelectRestoreFile;
	private Button btnRestore;
	private Label lblBackupPath;
	private Label lblReplicationDegree;
	private Label lblKb;
	private Label lblRestorePath;
	private Button btnDeleteFile;
	private Button btnSelectDeleteFile;
	private Label lblDeletePath;
	private Button btnDelete;
	private Button btnReclaimSpace;
	private Spinner kbToFree;
	private Button btnFreeSpace;
	
	
	
	private String pathToFile;
	
	

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Gui(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		setText("Serverless Distributed Backup Service");
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
		
		Group grpStandardActions = new Group(shell, SWT.NONE);
		grpStandardActions.setText("Standard Actions");
		grpStandardActions.setBounds(10, 10, 430, 296);
		
		
		//BACKUP BLOCK ============================================================================
		
		btnBackupFile = new Button(grpStandardActions, SWT.RADIO);
		btnBackupFile.setBounds(10, 10, 100, 20);
		btnBackupFile.setText("Backup File");
		btnBackupFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnBackupFile.getSelection()) {
					setBackupFileEnable(true);
				} else {
					setBackupFileEnable(false);
					fileToBackup.setText("");
					pathToFile = "";
				}
			}
		});
		
		btnBrowseBackUp = new Button(grpStandardActions, SWT.NONE);
		btnBrowseBackUp.setBounds(25, 30, 95, 25);
		btnBrowseBackUp.setText("Browse");
		btnBrowseBackUp.setEnabled(false);
		btnBrowseBackUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(btnBrowseBackUp.getShell(),  SWT.OPEN );
				dlg.setText("Open");
				pathToFile = dlg.open();
				if (pathToFile == null) {
					pathToFile = "";
					return;	
				}
				fileToBackup.setText(dlg.getFileName());	
			}
		});
		
		
		lblBackupPath = new Label(grpStandardActions, SWT.NONE);
		lblBackupPath.setBounds(120, 33, 30, 20);
		lblBackupPath.setText("Path");
		lblBackupPath.setEnabled(false);
		
		fileToBackup = new Text(grpStandardActions, SWT.BORDER);
		fileToBackup.setBounds(150, 31, 190, 21);
		fileToBackup.setEditable(false);
		fileToBackup.setEnabled(false);
		
		lblReplicationDegree = new Label(grpStandardActions, SWT.NONE);
		lblReplicationDegree.setBounds(120, 55, 115, 18);
		lblReplicationDegree.setText("Replication Degree:");
		lblReplicationDegree.setEnabled(false);
		
		repDeg = new Spinner(grpStandardActions, SWT.BORDER);
		repDeg.setMinimum(1);
		repDeg.setBounds(240, 52, 60, 22);
		repDeg.setEnabled(false);
		
		btnBackup = new Button(grpStandardActions, SWT.NONE);
		btnBackup.setBounds(340, 30, 85, 25);
		btnBackup.setText("Backup");
		btnBackup.setSelection(false);
		btnBackup.setEnabled(false);
		btnBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (pathToFile.equals("")) {
//					MessageDialog.openError(null, "Printer Error Message", "Error getting print reply file.");
				} else {
					startBackup(pathToFile, repDeg.getSelection());
				}
			}
		});
		
		
		
		//RESTORE BLOCK ============================================================================
		
		btnRestoreFile = new Button(grpStandardActions, SWT.RADIO);
		btnRestoreFile.setBounds(10, 80, 100, 20);
		btnRestoreFile.setText("Restore File");
		btnRestoreFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnRestoreFile.getSelection()) {
					setRestoreFileEnable(true);
				} else {
					setRestoreFileEnable(false);
					fileToRestore.setText("");
					pathToFile = "";
				}
			}
		});
		
		btnSelectRestoreFile = new Button(grpStandardActions, SWT.NONE);
		btnSelectRestoreFile.setBounds(25, 100, 95, 25);
		btnSelectRestoreFile.setText("Select File");
		btnSelectRestoreFile.setEnabled(false);
		btnSelectRestoreFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectFileFromBackUp()) {
					fileToRestore.setText(pathToFile);
				};	
			}
		});
		
		lblRestorePath = new Label(grpStandardActions, SWT.NONE);
		lblRestorePath.setText("Path");
		lblRestorePath.setBounds(120, 103, 30, 21);
		lblRestorePath.setEnabled(false);
		
		fileToRestore = new Text(grpStandardActions, SWT.BORDER);
		fileToRestore.setBounds(150, 101, 190, 19);
		fileToRestore.setText("");
		fileToRestore.setEditable(false);
		fileToRestore.setEnabled(false);

		btnRestore = new Button(grpStandardActions, SWT.NONE);
		btnRestore.setBounds(340, 100, 85, 25);
		btnRestore.setText("Restore");
		btnRestore.setEnabled(false);
		btnRestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = fileToRestore.getText();
				if (path.equals("")) {
					//MessageDialog.openError(null, "Printer Error Message", "Error getting print reply file.");
				} else {
					startRestore(path);
				}
			}
		});
		
		
	
		//DELETE BLOCK ============================================================================
		
		btnDeleteFile = new Button(grpStandardActions, SWT.RADIO);
		btnDeleteFile.setBounds(10, 150, 92, 20);
		btnDeleteFile.setText("Delete File");
		btnDeleteFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnDeleteFile.getSelection()) {
					setDeleteFileEnable(true);
				} else {
					setDeleteFileEnable(false);
					fileToDelete.setText("");
				}
			}
		});
		
		btnSelectDeleteFile = new Button(grpStandardActions, SWT.NONE);
		btnSelectDeleteFile.setBounds(25, 170, 95, 28);
		btnSelectDeleteFile.setText("Select File");
		btnSelectDeleteFile.setEnabled(false);
		btnSelectDeleteFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectFileFromBackUp()) {
					fileToDelete.setText(pathToFile);
				};	
			}
		});
		
		lblDeletePath = new Label(grpStandardActions, SWT.NONE);
		lblDeletePath.setText("Path");
		lblDeletePath.setBounds(120, 174, 29, 18);
		lblDeletePath.setEnabled(false);
		
		fileToDelete = new Text(grpStandardActions, SWT.BORDER);
		fileToDelete.setBounds(149, 172, 190, 21);
		fileToDelete.setEditable(false);
		fileToDelete.setEnabled(false);
		
		btnDelete = new Button(grpStandardActions, SWT.NONE);
		btnDelete.setBounds(340, 170, 85, 28);
		btnDelete.setText("Delete");
		btnDelete.setEnabled(false);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = fileToDelete.getText();
				if (path.equals("")) {
					//MessageDialog.openError(null, "Printer Error Message", "Error getting print reply file.");
				} else {
					startDelete(path);
				}
			}
		});
		
		
		
		//RECLAIMING SPACE BLOCK ============================================================================
		
		btnReclaimSpace = new Button(grpStandardActions, SWT.RADIO);
		btnReclaimSpace.setBounds(10, 220, 120, 18);
		btnReclaimSpace.setText("Reclaim Space");
		btnReclaimSpace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnReclaimSpace.getSelection()) {
					setReclaimSpaceEnable(true);
				} else {
					setReclaimSpaceEnable(false);
				}
			}
		});
		
		
		kbToFree = new Spinner(grpStandardActions, SWT.BORDER);
		kbToFree.setPageIncrement(100);
		kbToFree.setMaximum(100000);
		kbToFree.setSelection(1);
		kbToFree.setBounds(25, 240, 92, 22);
		kbToFree.setEnabled(false);
		
		lblKb = new Label(grpStandardActions, SWT.NONE);
		lblKb.setBounds(120, 243, 30, 14);
		lblKb.setText("KB");
		lblKb.setEnabled(false);
		
		btnFreeSpace = new Button(grpStandardActions, SWT.NONE);
		btnFreeSpace.setBounds(150, 237, 100, 28);
		btnFreeSpace.setText("Free Space");
		btnFreeSpace.setEnabled(false);
		btnFreeSpace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = fileToDelete.getText();
				if (path.equals("")) {
					//MessageDialog.openError(null, "Printer Error Message", "Error getting print reply file.");
				} else {
					startReclaiming(kbToFree.getSelection());
				}
			}
		});
		
		
		Button btnExit = new Button(shell, SWT.NONE);
		btnExit.setBounds(345, 310, 95, 25);
		btnExit.setText("Exit");
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = true;
				shell.dispose();
			}
		});
		
		Label lblVersion = new Label(shell, SWT.NONE);
		lblVersion.setBounds(10, 315, 75, 14);
		lblVersion.setText("Version " + VERSION);

	}
	

	private boolean selectFileFromBackUp() {
		String[] backupFiles = FileToBackup.backedFiles();
		BackupTable dlg = new BackupTable(btnRestoreFile.getShell(), backupFiles);
		
		if (dlg.open()) {
			pathToFile = dlg.getFile();
			return true;
		}
		return false;
	}

	
	
	// BUTTON TOOGLES ======================================================================
	
	protected void setBackupFileEnable(boolean b) {	
		btnBrowseBackUp.setEnabled(b);
		lblBackupPath.setEnabled(b);
		fileToBackup.setEnabled(b);
		lblReplicationDegree.setEnabled(b);
		repDeg.setEnabled(b);
		btnBackup.setEnabled(b);		
	}
	
	
	protected void setRestoreFileEnable(boolean b) {
		btnSelectRestoreFile.setEnabled(b);
		lblRestorePath.setEnabled(b);
		fileToRestore.setEnabled(b);
		btnRestore.setEnabled(b);		
	}

	
	protected void setDeleteFileEnable(boolean b) {
		btnSelectDeleteFile.setEnabled(b);
		lblDeletePath.setEnabled(b);
		fileToDelete.setEnabled(b);
		btnDelete.setEnabled(b);
	}

	
	protected void setReclaimSpaceEnable(boolean b) {
		kbToFree.setEnabled(b);
		lblKb.setEnabled(b);
		btnFreeSpace.setEnabled(b);	
	}
	


	protected void startBackup(String path, int replications) {
		// TODO launch threat
		
		
		try {
			backupFile(path, replications);
		} catch (RemoteException e) {

		}
		
	}

	@Override
	public void backupFile(String path, int repDeg) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	

	protected void startRestore(String path) {
		// TODO Auto-generated method stub
		
		try {
			restoreFile(path);
		} catch (RemoteException e) {

		}
		
	}
	

	@Override
	public void restoreFile(String path) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	
	
	protected void startDelete(String path) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void deleteFile(String path) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	
	
	protected void startReclaiming(int selection) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void reclaimSpace(int size) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		Display display = Display.getDefault();
		Shell dialogShell = new Shell(display);
		
		Gui dlg = new Gui(dialogShell);
		
		dlg.open();
		
		
	}
	
	
}
