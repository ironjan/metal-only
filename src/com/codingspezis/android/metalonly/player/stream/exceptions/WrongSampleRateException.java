package com.codingspezis.android.metalonly.player.stream.exceptions;

@SuppressWarnings("serial")
public class WrongSampleRateException extends Exception {
	
	private int rate;
	
	public WrongSampleRateException(int rate){
		this.rate = rate;
	}
	
	@Override
	public String getMessage() {
		return "Wrong sample rate detected: "+rate;
	}
	
}
