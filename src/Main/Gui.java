package Main;

import java.util.Timer;

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
import Messages.Message;
import Workers.BackupOrder;
import Workers.DeleteOrder;
import Workers.ReclaimSpaceOrder;
import Workers.RestoreOrder;
import Workers.Scout;

public class Gui extends Dialog {

	private static final String VERSION = Message.getVersion();
	
	public static boolean RUNNING = false;
	
	
	// TODO Organize variables
	protected boolean result;
	protected Shell shell;
	private Text fileToBackup;
	private Text fileToRestore;
	private Text fileToDelete;
	private int width = 450;
	private int height = 505;
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
	
	
	
	private String pathToFile = "";

	private Text mdbIP;
	private Text mdrIP;
	private Text mcIP;
	private Label lblPort;
	private Label lblIp;

	private Button btnStart;

	private Spinner mdbPort;

	private Spinner mdrPort;

	private Spinner mcPort;
	
	
	private Scout mdbScout;
	private Scout mdrScout;
	private Scout mcScout;
	private Spinner backupSpace;
	private Label lblKb_1;
	private Label lblBackupSpace;
	private Text backupFolder;
	private Button btnBrowseFolder;
	private Label lblBackupFolder;
	
	

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
		
		
		// COMUNICATIONS PANEL ============================================================================
		
		Group grpComunicationSettings = new Group(shell, SWT.NONE);
		grpComunicationSettings.setText("Comunication Settings");
		grpComunicationSettings.setBounds(10, 10, 430, 145);
		
		mdbPort = new Spinner(grpComunicationSettings, SWT.BORDER);
		mdbPort.setMaximum(9999);
		mdbPort.setSelection(8888);
		mdbPort.setBounds(250, 45, 60, 22);
		
		mdrPort = new Spinner(grpComunicationSettings, SWT.BORDER);
		mdrPort.setMaximum(9999);
		mdrPort.setSelection(8887);
		mdrPort.setBounds(250, 70, 60, 22);
		
		mcPort = new Spinner(grpComunicationSettings, SWT.BORDER);
		mcPort.setMaximum(9999);
		mcPort.setSelection(8886);
		mcPort.setBounds(250, 95, 60, 22);
		
		mdbIP = new Text(grpComunicationSettings, SWT.BORDER);
		mdbIP.setText("227.0.0.1");
		mdbIP.setBounds(315, 45, 105, 21);
		
		Label label = new Label(grpComunicationSettings, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(205, 5, 2, 115);
		
		Label lblChannels = new Label(grpComunicationSettings, SWT.NONE);
		lblChannels.setBounds(270, 8, 70, 14);
		lblChannels.setText("Channels");
		
		mdrIP = new Text(grpComunicationSettings, SWT.BORDER);
		mdrIP.setText("227.0.0.2");
		mdrIP.setBounds(315, 70, 105, 21);
		
		mcIP = new Text(grpComunicationSettings, SWT.BORDER);
		mcIP.setText("227.0.0.3");
		mcIP.setBounds(315, 95, 105, 21);
		
		Label lblMdb = new Label(grpComunicationSettings, SWT.NONE);
		lblMdb.setBounds(210, 48, 40, 14);
		lblMdb.setText("MDB");
		
		Label lblMdr = new Label(grpComunicationSettings, SWT.NONE);
		lblMdr.setBounds(210, 73, 40, 14);
		lblMdr.setText("MDR");
		
		Label lblMc = new Label(grpComunicationSettings, SWT.NONE);
		lblMc.setBounds(210, 98, 40, 14);
		lblMc.setText("MC");
		
		lblPort = new Label(grpComunicationSettings, SWT.NONE);
		lblPort.setBounds(250, 27, 50, 14);
		lblPort.setText("Port");
		
		lblIp = new Label(grpComunicationSettings, SWT.NONE);
		lblIp.setBounds(315, 27, 60, 14);
		lblIp.setText("IP");
		
		btnStart = new Button(grpComunicationSettings, SWT.TOGGLE);
		btnStart.setBounds(60, 5, 95, 28);
		btnStart.setText("Start");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e ) {
				if (btnStart.getSelection()) {
					
					if (!validChannels()) {
						btnStart.setSelection(false);
						return;
					}
					
					startServer();
					
				} else {
					
					stopServer();
				}
			}
		});
		
		
		
		
		backupSpace = new Spinner(grpComunicationSettings, SWT.BORDER);
		backupSpace.setPageIncrement(1000);
		backupSpace.setIncrement(100);
		backupSpace.setMaximum(9999999);
		backupSpace.setSelection(256);
		backupSpace.setBounds(10, 95, 70, 22);
		
		lblKb_1 = new Label(grpComunicationSettings, SWT.NONE);
		lblKb_1.setBounds(80, 98, 20, 14);
		lblKb_1.setText("KB");
		
		lblBackupSpace = new Label(grpComunicationSettings, SWT.NONE);
		lblBackupSpace.setBounds(10, 78, 90, 16);
		lblBackupSpace.setText("Backup Space");
		
		backupFolder = new Text(grpComunicationSettings, SWT.BORDER);
		backupFolder.setBounds(10, 50, 120, 19);
		
		btnBrowseFolder = new Button(grpComunicationSettings, SWT.NONE);
		btnBrowseFolder.setBounds(130, 47, 75, 28);
		btnBrowseFolder.setText("Browse");
		
		lblBackupFolder = new Label(grpComunicationSettings, SWT.NONE);
		lblBackupFolder.setBounds(10, 35, 100, 16);
		lblBackupFolder.setText("Backup Folder");
		
		
		
		
		
		
		
		
		
		
		// ACTIONS CONTROL ============================================================================
		
		Group grpStandardActions = new Group(shell, SWT.NONE);
		grpStandardActions.setText("Standard Actions");
		grpStandardActions.setBounds(10, 160, 430, 296);
		
		
		//BACKUP BLOCK ============================================================================
		
		btnBackupFile = new Button(grpStandardActions, SWT.RADIO);
		btnBackupFile.setBounds(10, 10, 100, 20);
		btnBackupFile.setText("Backup File");
		btnBackupFile.setEnabled(false);
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
		btnRestoreFile.setEnabled(false);
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
		btnDeleteFile.setEnabled(false);
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
		btnReclaimSpace.setEnabled(false);
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
		btnExit.setBounds(345, 455, 95, 25);
		btnExit.setText("Exit");
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = true;
				shell.dispose();
			}
		});
		
		Label lblVersion = new Label(shell, SWT.NONE);
		lblVersion.setBounds(10, 460, 75, 14);
		lblVersion.setText("Version " + VERSION);


	}

	protected boolean validChannels() {
		if (mdbIP.getText().equals(mdrIP.getText()) || 
				mdbIP.getText().equals(mcIP.getText()) ||
				mdrIP.getText().equals(mcIP.getText())) return false;
		
		if (mdbPort.getSelection() == mdrPort.getSelection() ||
				mdbPort.getSelection() == mcPort.getSelection() ||
				mdrPort.getSelection() == mcPort.getSelection()) return false;
		
		return true;
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

	
	
	/*
	 * 
	 *  BUTTON TOOGLES ======================================================================
	 *  
	 */

	

	protected void setActionsEnable(boolean b) {
		
		setSettingEnable(!b);
		
		btnBackupFile.setEnabled(b);
		btnRestoreFile.setEnabled(b);
		btnDeleteFile.setEnabled(b);
		btnReclaimSpace.setEnabled(b);
		
		if (btnBackupFile.getSelection()) {
			setBackupFileEnable(b);
		} else if (btnRestoreFile.getSelection()) {
			setRestoreFileEnable(b);
		} else if (btnDeleteFile.getSelection()) {
			setDeleteFileEnable(b);
		} else if (btnReclaimSpace.getSelection()) {
			setReclaimSpaceEnable(b);
		}
	}

	protected void setSettingEnable(boolean b) {
		
		mcIP.setEditable(b);
		mcIP.setEnabled(b);
		mcPort.setEnabled(b);
		
		mdbIP.setEditable(b);
		mdbIP.setEnabled(b);
		mdbPort.setEnabled(b);
		
		mdrIP.setEditable(b);
		mdrIP.setEnabled(b);
		mdrPort.setEnabled(b);
		
		backupFolder.setEditable(b);
		backupFolder.setEnabled(b);
		btnBrowseFolder.setEnabled(b);
		lblBackupFolder.setEnabled(b);
		
		backupSpace.setEnabled(b);
		lblKb_1.setEnabled(b);
		lblBackupSpace.setEnabled(b);
		
	}

	
	
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
	
	
	/*
	 * 
	 * WORK ORDERS  =========================================================
	 * 
	 */
	


	protected void startBackup(String path, int replications) {
			
		new Timer().schedule(new BackupOrder(path, replications,mdbPort.getSelection(),mdbIP.getText()), 2000, 2000);
	
	}

	
	
	protected void startRestore(String path) {
		
		//TODO Check this call and see what is the condition to sleep
		if (!mcScout.isAlive()) mcScout.start();
		
		
		new Timer().schedule(new RestoreOrder(path, mdrPort.getSelection(),mdrIP.getText()), 2000, 2000);
		
	}
	
	
	
	protected void startDelete(String path) {
		
		new Timer().schedule(new DeleteOrder(path, mcPort.getSelection(),mcIP.getText()), 1);
		
	}

	
	
	protected void startReclaiming(int size) {
		new Timer().schedule(new ReclaimSpaceOrder(kbToFree.getSelection(), mcPort.getSelection(),mcIP.getText()), 1);
	}
	
	
	
	
	
	
	protected void startServer() {
		
		kbToFree.setMaximum(backupSpace.getSelection());
		kbToFree.setSelection(backupSpace.getSelection()/2);
		
		btnStart.setText("Stop");
		setActionsEnable(true);
		RUNNING = true;
		
		mdbScout = new Scout(mdbPort.getSelection(), mdbIP.getText());
		mdrScout = new Scout(mdrPort.getSelection(), mdrIP.getText());
		mcScout = new Scout(mcPort.getSelection(), mcIP.getText());
		
		if (!mdbScout.isAlive()) mdbScout.start();
		if (!mdrScout.isAlive()) mdrScout.start();
		
	}

	protected void stopServer() {
		btnStart.setText("Start");
		setActionsEnable(false);
		RUNNING = false;
		
		mdbScout.interrupt();
		mdrScout.interrupt();
		mcScout.interrupt();
		
	}

	public static void main(String[] args) {
		
		Display display = Display.getDefault();
		Shell dialogShell = new Shell(display);
		
		Gui dlg = new Gui(dialogShell);
		
		dlg.open();
		
		
	}
}
