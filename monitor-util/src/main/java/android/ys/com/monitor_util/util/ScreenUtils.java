package android.ys.com.monitor_util.util;

//import com.bqframe.tools.log.LogTools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
@SuppressLint("NewApi")
public final class ScreenUtils {
	/** 水平设计分辨率 */
	public static final float design_x = 720.0f;

	/** 垂直设计分辨率 */
	public static final float design_y = 1280.0f;

	/** 水平物理分辨率 */
	public static float physical_x = 0.0f;

	/** 垂直物理分辨率 */
	public static float physical_y = 0.0f;

	/** 状态栏高度 */
	public static float state_bar_top = 0.0f;

	/** 水平缩放比率 */
	public static float scale_x = 1.0f;

	/** 垂直缩放比率 */
	public static float scale_y = 1.0f;

	/** 抽象设计密度 */
	public static float density = 0.0f;

	/** 抽象像素密度 */
	public static int dpi = 0;

	/**
	 * 设计物理分辨率
	 * 
	 * @param x
	 * @param y
	 */
	private static void setPhysicalPixels(final float x, final float y) {
		physical_x = x;
		physical_y = y;

		scale_x = physical_x / design_x;
		scale_y = physical_y / design_y;

	}

	/**
	 * 得到屏幕分辨率
	 * 
	 * @param act
	 */
	public static void init(Activity act) {
		// 屏幕分辨率
		DisplayMetrics dMetrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
		float width = dMetrics.widthPixels;
		float height = dMetrics.heightPixels;
		setPhysicalPixels(width, height);

		// 状态栏区域
		Rect rect = new Rect();
		act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		state_bar_top = rect.top;

		// 像素密度
		density = dMetrics.density;
		dpi = dMetrics.densityDpi;

//		LogTools.addLogI("ScreenUtils", String.format("[physical_x=%.2f physical_y=%.2f]", physical_x, physical_y));
//		LogTools.addLogI("ScreenUtils", String.format("[scale_x=%.2f scale_y=%.2f]", scale_x, scale_y));
//		LogTools.addLogI("ScreenUtils", String.format("[state_bar_top=%.2f]", state_bar_top));
//		LogTools.addLogI("ScreenUtils", String.format("density=%.2f", density));
//		LogTools.addLogI("ScreenUtils", String.format("dpi=%d", dpi));

	}

}
