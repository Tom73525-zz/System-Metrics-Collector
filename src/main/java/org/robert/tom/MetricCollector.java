package org.robert.tom;


public class MetricCollector {
	
	private ArrayList<Integer> pidlist;

	public MetricCollector(){
		pidList = new ArrayList<Integer>();
	}

	public ArrayList<Integer> getCurrentPidlist(){
		File procDir;

		try {
			procDir = new File("/proc/");
		} catch(FileNotFoundException e){
			System.out.println("ERROR: Could not find /proc/");
		}
		String[] directories = procDir.list(new FilenameFilter(){
			@Override
			public boolean accept(File current, String name){
				File tempFile = new File(current, name);
				if(tempFile.getName().matches("[0-9]+")){
					System.out.println(tempFile.getName());
					return tempFile.isDirectory();
				} else {
					return false;
				}
			}
		});
	}

	public void collectMetrics(){

	}
}