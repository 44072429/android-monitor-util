package android.ys.com.monitor_util.util;

//import com.bqframe.resource.screen.ScreenUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public abstract class BaseConfig {
	/***
	 * Bitmap->recycle()异常 android 2.3系统后,Bitmap全部由虚拟机管理内存,因此调用不会异常 android
	 * 2.3系统前版本,Bitmap部分由C语言管理的内存,调用后会执行 C语言管理的内存空间,因此在引用Bitmap数据后,会报内存已经释放了的错误
	 */

	/** 是否已经赋值了数据 */
	public static boolean isAssignedValue = false;

	/** 图片资源选择器 */
	public static BitmapFactory.Options bfoOptions = null;

	public static Matrix matrix = null;

	/** 缩放配置数据 */
	public static float scale_x = 0;
	public static float scale_y = 0;
	public static int dpi = 0;

	/** 是否已经加载了配置 */
	public boolean isLoaded = false;

	public Context context = null;

	/** 组件背景图片宽度 */
	public int backWidth = 0;

	/** 组件背景图片高度 */
	public int backHeight = 0;

	/** 组件背景图片Drawble */
	public BitmapDrawable bmpDrawable = null;

	@SuppressLint("NewApi")
	public BaseConfig() {
		if (!isAssignedValue) {
			isAssignedValue = true;

			scale_x = ScreenUtils.scale_x;
			scale_y = ScreenUtils.scale_y;
			dpi = ScreenUtils.dpi;

			// 初始化选择器
			bfoOptions = new BitmapFactory.Options();
			// 目标像素密度
			bfoOptions.inDensity = dpi;
			// 图片不缩放处理
			bfoOptions.inScaled = false;

			matrix = new Matrix();
			matrix.postScale(scale_x, scale_y);
		}
	}

	/**
	 * 初始化配置
	 * @param context
	 */
	public abstract void init(Context context);

	/**
	 * 加载资源
	 */
	protected abstract void load();

	/**
	 * 矩形缩放
	 */
	public static void scaleRect(Rect r) {
		r.left = (int) (r.left * scale_x);
		r.right = (int) (r.right * scale_x);
		r.top = (int) (r.top * scale_y);
		r.bottom = (int) (r.bottom * scale_y);
	}

	/**
	 * 点坐标缩放
	 */
	public static void scalePoint(Point p) {
		p.x = (int) (p.x * scale_x);
		p.y = (int) (p.y * scale_y);
	}

	/**
	 * X值缩放
	 * @param vx
	 * @return
	 */
	public static int scaleValueX(int vx) {
		return (int) (vx * scale_x);
	}

	/**
	 * Y值缩放
	 * 
	 * @param vy
	 * @return
	 */
	public static int scaleValueY(int vy) {
		return (int) (vy * scale_y);
	}

	/**
	 * 得到字符的个数,一个汉字算两个长度
	 * 
	 * @param text
	 * @return
	 */
	public static int getCharCount(String text) {
		if (text == null)
			return 0;
		int cnt = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) > 127)
				cnt += 2;
			else
				cnt++;
		}
		return cnt;
	}

	/**
	 * 将bmp显示位置相对居中
	 * 
	 * @param bmp
	 * @param pos
	 */
	public static void translateCenter(Bitmap bmp, Point pos) {
		if (bmp != null && pos != null) {
			pos.x = pos.x - bmp.getWidth() / 2;
			pos.y = pos.y - bmp.getHeight() / 2;
		}
	}

	/**
	 * 将text显示位置相对居中
	 * 
	 * @param text
	 * @param pos
	 */
	public static void translateCenter(Paint paint, String text, Point pos) {
		if (paint != null && text != null && pos != null) {
			Rect rct = new Rect();
			paint.getTextBounds(text, 0, text.length(), rct);

			pos.x = pos.x - rct.width() / 2;

			// Paint.drawText方法,局部坐标系采用的是数学系标系,所以需要加上rct.height / 2
			pos.y = pos.y + rct.height() / 2;
		}
	}

	/**
	 * 将text显示位置[水平相对居中,垂直方向不变]
	 * 
	 * @param paint
	 * @param text
	 * @param pos
	 */
	public static void translateCenterHor(Paint paint, String text, Point pos) {
		if (paint != null && text != null && pos != null) {
			Rect rct = new Rect();
			paint.getTextBounds(text, 0, text.length(), rct);

			pos.x = pos.x - rct.width() / 2;
			pos.y = pos.y;
		}
	}

	/**
	 * 将text显示位置[垂直相对居中,水平方向不变]
	 * 
	 * @param paint
	 * @param text
	 * @param pos
	 */
	public static void translateCenterVer(Paint paint, String text, Point pos) {
		if (paint != null && text != null && pos != null) {
			Rect rct = new Rect();
			paint.getTextBounds(text, 0, text.length(), rct);

			pos.x = pos.x;
			pos.y = pos.y + rct.height() / 2;
		}
	}

	/**
	 * 复制一个Point
	 * 
	 * @param pos
	 * @return
	 */
	public static Point clonePoint(Point pos) {
		return new Point(pos);
	}
}
