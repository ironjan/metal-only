package com.codingspezis.android.metalonly.player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * SpinnerOwnValue
 * @version 06.01.2013
 *
 * class similar to Spinner
 * supports a free chooseable value
 *
 */
public class SpinnerOwnValue{

	public enum Type{
		NAME, VALUE, NO_OWN
	}
	
	// constructor arguments
	private Context appContext;
	private String selectableObjects[];
	private Type type;
	int message;
	
	// views
	AlertDialog alert;
	LinearLayout layout;
	
	OnObjectSelectedListener listener = null;
	
	public SpinnerOwnValue(Context appContext, String selectableObjects[], Type type, int message){
		this.appContext=appContext;
		this.selectableObjects=selectableObjects;
		if(type!=Type.NO_OWN){
			this.selectableObjects = new String[selectableObjects.length+1];
			for(int i=0; i<selectableObjects.length;i++)
				this.selectableObjects[i]=selectableObjects[i];
			this.selectableObjects[selectableObjects.length]=appContext.getString(R.string.other);
		}
		this.type=type;
		this.message=message;
	}
	
	public void showSelection(){
		AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
	    builder.setItems(selectableObjects, new DialogInterface.OnClickListener() {
	    	@Override
			public void onClick(DialogInterface dialog, int which) {
    			// other value
				if(which==selectableObjects.length-1 &&
				   type!=Type.NO_OWN){
					AlertDialog.Builder alertB = new AlertDialog.Builder(appContext);
					// alert.setTitle();
					alertB.setMessage(R.string.choose_other);
					final EditText input = new EditText(appContext);
					if(type == Type.VALUE)
						input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
					alertB.setView(input);
					alertB.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(listener!=null)
								listener.objectSelected(input.getText().toString(), selectableObjects.length-1);
						}
					});
					alertB.show();
				// pre defined objects
				}else{
					if(listener!=null)
						listener.objectSelected(selectableObjects[which].toString(), which);
				}
           }
	    });
	    builder.show();
	}

	public void setOnObjectSelectedListener(OnObjectSelectedListener listener){
		this.listener=listener;
	}
	
	
}

