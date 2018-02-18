package com.configscanner;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileModificationListener {
	private String pathToFileName;
	private String fileName;
	private Path path;	
	private WatchService fileWatchService;
	ExecutorService executorservice = Executors.newCachedThreadPool();
	Future<Void> future;
	public FileModificationListener(String fileNameWithPath) throws IOException{
		assert fileNameWithPath != null : "Filename is null.";
		path = Paths.get(fileNameWithPath);
		assert Files.exists(path) : "Invalid filename or path :  "+fileNameWithPath;
		ConfigManager.reloadConfigurations(fileNameWithPath);
		this.pathToFileName = fileNameWithPath;
		this.fileName = path.getFileName().toString();
		this.path = Paths.get(fileNameWithPath).toAbsolutePath().getParent();
		fileWatchService =  path.getFileSystem().newWatchService();
		path.register(fileWatchService, StandardWatchEventKinds.ENTRY_MODIFY);
	}


	public void listenForChanges(){
				
		Callable<Void> callableObj = () -> { 
			WatchKey watchKey = null;
			while (Thread.interrupted() == false) {
			    try {
			    	watchKey = fileWatchService.take();
				} catch (InterruptedException ie) {
					System.out.println("Interupted....... !!");
					break;
				} catch (ClosedWatchServiceException cwe) {
					System.out.println("Closed Watch Service....... !!");
					break;
				}
			    if(watchKey != null) {
			        watchKey.pollEvents().stream().forEach(event -> {
			        									try {
			        										if (fileName.equals(event.context().toString())){
			        											ConfigManager.reloadConfigurations(pathToFileName);
			        										}
														} catch (IOException e) {
															e.printStackTrace();
														}
			        								});
	                boolean reset = watchKey.reset();
	                if (!reset) {
	                    System.out.println("Could not reset the watch key.");
	                    break;
	                }
			    }
	
			}
			return null;
		};
		
		future = executorservice.submit(callableObj);
		
	}
	
	public void stopListening() throws IOException{
			future.cancel(true);
			fileWatchService.close();
	}
	
}
