package com.satersoft.stardestroyer.domain.chronometer;

import com.satersoft.stardestroyer.domain.util.KHLeuvenMobileException;

import java.io.Serializable;

public abstract class StartableObject implements Serializable, Startable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5556135404581894429L;
	private StartableStatus status = StartableStatus.CREATED;

	@Override
	public void start() throws KHLeuvenMobileException {
		if(status.equals(StartableStatus.STARTED) || status.equals(StartableStatus.RESUMED)){
			throw new KHLeuvenMobileException("Already started");
		}
		setStatus(StartableStatus.STARTED);
	}

	@Override
	public void resume() throws KHLeuvenMobileException  {
		if(status.equals(StartableStatus.STARTED) || status.equals(StartableStatus.CREATED) || status.equals(StartableStatus.RESUMED)){
			throw new KHLeuvenMobileException("Already started");
		}
		setStatus(StartableStatus.RESUMED);
	}

	@Override
	public void stop() throws KHLeuvenMobileException  {
		if(status.equals(StartableStatus.STOPPED) || status.equals(StartableStatus.CREATED)){
			throw new KHLeuvenMobileException("Not started");
		}
		setStatus(StartableStatus.STOPPED);
	}

	@Override
	public StartableStatus getStatus() {
		return status;
	}

	private void setStatus(StartableStatus status) {
		this.status = status;
	}
	
}
