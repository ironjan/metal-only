package com.codingspezis.android.metalonly.player;

import java.io.BufferedReader;

import android.content.Context;

/**
 * WishChecker
 * @version 21.01.2013
 * 
 * checks for open wishes / regards on wish page on metal-only.de
 *
 */
public class WishChecker {

	private OnWishesCheckedListener wishListener;
	
	private HTTPGrabber grabber;
	private OnHTTPGrabberListener grabberListener =
		
		new OnHTTPGrabberListener() {
			
			@Override
			public void onSuccess(BufferedReader httpResponse) {
				AllowedActions allowedActions = new AllowedActions();
				allowedActions.regards = true;
				allowedActions.wishes = true;
				allowedActions.moderated = true;
				try{
					String line = httpResponse.readLine();
					while(line!=null){
						// format: "Derzeitiges Limit: 4 Wünsche und 3 Grüße pro Hörer"
						if(line.contains("Derzeitiges Limit:")){
							try{
								allowedActions.limit = line.substring(line.indexOf("Derzeitiges Limit:"),
													   line.indexOf("pro Hörer") ).trim();
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						if(line.contains("sind keine Grüße möglich.")){
							allowedActions.regards=false;
						}
						if(line.contains("die Playlist ist bereits voll")){
							allowedActions.wishes=false;
						}
						if(line.contains("sind nur in der moderierten Sendezeit möglich!") ||
						   line.contains("Aktuell On Air: MetalHead")){
							allowedActions.regards = false;
							allowedActions.wishes = false;
							allowedActions.moderated = false;
							break;
						}
						line = httpResponse.readLine();
					}
					if(wishListener!=null)
						wishListener.onWishesChecked(allowedActions);
					
				}catch(Exception e){
					e.printStackTrace();
				}			
			}
			
			@Override
			public void onTimeout() {}
			
			@Override
			public void onError(String error) {}
			
			@Override
			public void onCancel() {}
			
		};
	
	/**
	 * AllowedActions
	 * @version 04.01.2013
	 * 
	 * class for saving information about actions that are allowed on wish page on metal-only.de
	 * 
	 */
	public static class AllowedActions{
		public String  limit = "";
		public boolean wishes = false;
		public boolean regards = false;
		public boolean moderated = false;
	}
	
	/**
	 * constructor
	 * @param parent parent activity
	 * @param url url for request
	 */
	public WishChecker(Context context, String URL) {
		grabber = new HTTPGrabber(context, URL, grabberListener);
	}
	
	public void start(){
		grabber.start();
	}

	/**
	 * settings OnWishesCheckedListener
	 * @param listener OnWishesCheckedListener
	 */
	public void setOnWishesCheckedListener(OnWishesCheckedListener listener){
		wishListener=listener;
	}
		
}
