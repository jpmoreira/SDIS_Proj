package Workers;

public class WorkOrder implements Runnable{

	
	private int taskNo;
	private String filePath = null;
	
	
	public WorkOrder(String path, int repDeg) {
		this.taskNo = 1;
		this.filePath  = path;
	}
	
	public WorkOrder(String path) {
		this.taskNo = 1;
		this.filePath  = path;
	}
	
	
	public WorkOrder(int sizeTofree) {
		this.taskNo = 4;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		switch (taskNo) {
		case 1:
			
			
			
			break;

		default:
			break;
		}
		
	}

}
