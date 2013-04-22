package com.codingspezis.android.lazylistmodification;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.codingspezis.android.metalonly.player.R;

import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * this class comes in the original form from:
 * https://github.com/thest1/LazyList
 * 
 * it works not longer with URLs but with moderator names of metal only
 * some synchronized statements are also added
 */
public class ImageLoader {
	
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler(); //handler to display images in UI thread
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    final int stub_id=R.drawable.mo_wait;
    public void DisplayImage(String moderator, ImageView imageView)
    {
        imageViews.put(imageView, moderator);
        Bitmap bitmap=memoryCache.get(moderator);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else{
            queuePhoto(moderator, imageView);
            imageView.setImageResource(stub_id);
        }
    }
        
    private void queuePhoto(String moderator, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(moderator, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private synchronized Bitmap getBitmap(String moderator) 
    {    	
        // from internal private storage
        Bitmap b = fileCache.decodeMod(moderator); // decodeFile(f);
        if(b!=null)
            return b;
        // from web
        try {
        	//
            Bitmap bitmap=null;
            URL imageUrl = new URL("http://www.metal-only.de/botcon/mob.php?action=pic&nick="+moderator);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            //
            OutputStream os = fileCache.getOutputStream(moderator); // new FileOutputStream(f); 
            Utils.CopyStream(is, os);
            os.close();
            bitmap = fileCache.decodeMod(moderator); // decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String moderator;
        public ImageView imageView;
        public PhotoToLoad(String moderator, ImageView imageView){
            this.moderator=moderator; 
            this.imageView=imageView;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.moderator);
                memoryCache.put(photoToLoad.moderator, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.moderator))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        @Override
		public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
    
}
