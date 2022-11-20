package com.animal.d3Rotate;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 自定义实现的3D翻转效果的工具类
 * 
 * @author 熊小松
 * @time 2013-09-30
 * @introduce 实现3D立体翻转效果
 */
public class Util_Rotate3DAnimation extends Animation {
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean mReverse;
	private Camera mCamera;

	/**
	 * 构造函数 创建一个基于Y轴旋转的三维动画，由传入起始角度旋转到终止角度，旋转中心点由传入的X和Y坐标确定
	 * 
	 * @param fromDegrees
	 *            旋转起始角度
	 * @param toDegrees
	 *            旋转终止角度
	 * @param centerX
	 *            旋转中心X坐标
	 * @param centerY
	 *            旋转中心Y坐标
	 * @param depthZ
	 *            Z轴翻转深度
	 * @param reverse
	 *            如果为true则翻转，false则不翻转
	 */
	public Util_Rotate3DAnimation(float fromDegrees, float toDegrees,
			float centerX, float centerY, float depthZ, boolean reverse) {
		this.mFromDegrees = fromDegrees;
		this.mToDegrees = toDegrees;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.mDepthZ = depthZ;
		this.mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees
				+ ((mToDegrees - fromDegrees) * interpolatedTime);

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		if (mReverse) {
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}