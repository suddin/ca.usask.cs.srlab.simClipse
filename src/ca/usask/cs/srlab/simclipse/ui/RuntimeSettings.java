package ca.usask.cs.srlab.simclipse.ui;

public class RuntimeSettings {

	private boolean enableDetectionOnResourceChange;
	private boolean enableAutoCloneIndexUpdate;
	
	public RuntimeSettings(boolean enableDetectionOnResourceChange, boolean enableAutoCloneIndexUpdate) {
		this.enableDetectionOnResourceChange = enableDetectionOnResourceChange;
		this.enableAutoCloneIndexUpdate = enableAutoCloneIndexUpdate;
	}

	public boolean isEnableDetectionOnResourceChange() {
		return enableDetectionOnResourceChange;
	}

	public boolean isEnableAutoCloneIndexUpdate() {
		return enableAutoCloneIndexUpdate;
	}

}
