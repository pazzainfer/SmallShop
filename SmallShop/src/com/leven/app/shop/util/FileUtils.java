package com.leven.app.shop.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
	/**
	 * 保存Image的目录名
	 */
	private final static String FOLDER_NAME = "/SmallShop";

	/**
	 * 获取储存Image的目录
	 * @return
	 */
	private static String getStorageDirectory(Context context){
		String mDataRootPath = context.getCacheDir().getPath();
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
	}
	
	/**
	 * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * @param fileName 
	 * @param bitmap   
	 * @throws IOException
	 */
	public static void savaBitmap(Context context,  String fileName, Bitmap bitmap) throws IOException{
		if(bitmap == null){
			return;
		}
		String path = getStorageDirectory(context);
		File folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}
	
	public static void savaDrawable(Context context, String fileName, int drawableId) throws IOException{
		Bitmap bitmap = DrawableUtil.getBitmapFromDrawable(context, drawableId);
		savaBitmap(context, fileName, bitmap);
	}
	
	public static File getSavedFile(Context context, String fileName){
		return new File(getStorageDirectory(context) + File.separator + fileName);
	}
	
	/**
	 * 从手机或者sd卡获取Bitmap
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName){
		return BitmapFactory.decodeFile(getStorageDirectory(context) + File.separator + fileName);
	}
	
	/**
	 * 判断文件是否存在
	 * @param fileName
	 * @return
	 */
	public static boolean isFileExists(Context context, String fileName){
		return new File(getStorageDirectory(context) + File.separator + fileName).exists();
	}
	
	/**
	 * 获取文件的大小
	 * @param fileName
	 * @return
	 */
	public long getFileSize(Context context, String fileName) {
		return new File(getStorageDirectory(context) + File.separator + fileName).length();
	}
	
	
	/**
	 * 删除SD卡或者手机的缓存图片和目录
	 */
	public static void deleteFile(Context context) {
		File dirFile = new File(getStorageDirectory(context));
		if(! dirFile.exists()){
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		
		dirFile.delete();
	}
}