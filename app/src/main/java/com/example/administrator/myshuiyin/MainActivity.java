package com.example.administrator.myshuiyin;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private ImageView iv_shuiyin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt_taking_pictures = findViewById(R.id.bt_taking_pictures);
		iv_shuiyin = findViewById(R.id.iv_shuiyin);
		bt_taking_pictures.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_taking_pictures:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap takingPicturesBitmap = (Bitmap) extras.get("data");
			Bitmap shuiyinBitmap = getRes("sp_shuiyin");
			Bitmap waterMaskBitmap = createWaterMaskBitmap(takingPicturesBitmap, shuiyinBitmap);
			iv_shuiyin.setImageBitmap(waterMaskBitmap);
		}
	}

	/**
	 * 获取本地图片
	 *
	 * @param name 图片名称
	 * @return
	 */
	public Bitmap getRes(String name) {
		ApplicationInfo appInfo = getApplicationInfo();
		int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
		return BitmapFactory.decodeResource(getResources(), resID);
	}


	/**
	 * @param src       拍照获取的图片
	 * @param watermark 水印图片
	 * @return
	 */
	public Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark) {
		int paddingTop;
		if (src == null) {
			return null;
		}
		//获取图片宽高
		int width = src.getWidth();
		int height = src.getHeight();
		//获取水印图片宽高
		int watermarkWidth = watermark.getWidth();
		int watermarkHeight = watermark.getHeight();
//		//缩小或者放大水印图片  使水印宽度与图片一致
		if (width > 0 && watermarkWidth > 0) {
			float scale = ((float) width) / watermarkWidth;
			// 取得想要缩放的matrix参数
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
//			// 得到新的水印图片
			watermark = Bitmap.createBitmap(watermark, 0, 0, watermarkWidth, watermarkHeight, matrix, true);
			watermarkHeight = watermark.getHeight();
		}
		if (height > watermarkHeight) {
			//设置水印图片竖直位置在图片中间
			paddingTop = (height - watermarkHeight) / 2;
		} else {
			paddingTop = 0;
		}
		//创建一个bitmap
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		//将该图片作为画布
		Canvas canvas = new Canvas(newBitmap);
		//在画布 0，0坐标上开始绘制原始图片
		canvas.drawBitmap(src, 0, 0, null);
		// 建立Paint 物件
		Paint vPaint = new Paint();
		vPaint.setStyle(Paint.Style.STROKE);   //空心
		vPaint.setAlpha(80);
		//在画布上绘制水印图片
		canvas.drawBitmap(watermark, 0, paddingTop, vPaint);
		// 保存
		canvas.save(Canvas.ALL_SAVE_FLAG);
		// 存储
		canvas.restore();
		return newBitmap;
	}
}
