/*
 * Copyright (C) 2015 iChano incorporation's Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongyun.viewer.video;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.ichano.rvs.viewer.Media;
import com.zhongyun.viewer.utils.CommUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyRenderer implements Renderer{
	private static final String TAG = "MyRenderer";

	private byte[] y,u,v;
	int connectCount;
	Handler handler;

	private int[] yTextureNames;
	private int[] uTextureNames;
	private int[] vTextureNames;

	private FloatBuffer mVertices;
	private ShortBuffer mIndices;

	private int mProgramObject;
	private int mPositionLoc;
	private int mTexCoordLoc;

	private int yTexture;
	private int uTexture;
	private int vTexture;

	private final float[] mVerticesData = {

	-1.f, 1.f, 0.0f, // Position 0
			0.0f, 0.0f, // TexCoord 0

			-1.f, -1.f, 0.0f, // Position 1
			0.0f, 1.0f, // TexCoord 1

			1.f, -1.f, 0.0f, // Position 2
			1.0f, 1.0f, // TexCoord 2

			1.f, 1.f, 0.0f, // Position 3
			1.0f, 0.0f // TexCoord 3

	};
	private short[] mIndicesData = { 0, 1, 2, 0, 2, 3 };

	private ByteBuffer yBuffer = null;
	private ByteBuffer uBuffer = null;
	private ByteBuffer vBuffer = null;

	private IntBuffer frameBuffer;
	private IntBuffer renderBuffer;
	private IntBuffer parameterBufferWidth;
	private IntBuffer parameterBufferHeigth;

	public int display_w, display_h, play_pos_x, play_pos_y,gl_w,gl_h,pixelsWidth,yuv_w,yuv_h;

	String FRAG_SHADER = "varying lowp vec2 tc;\n" + "uniform sampler2D SamplerY;\n" + "uniform sampler2D SamplerU;\n"
			+ "uniform sampler2D SamplerV;\n" + "void main(void)\n" + "{\n" + "mediump vec3 yuv;\n"
			+ "lowp vec3 rgb;\n" + "yuv.x = texture2D(SamplerY, tc).r;\n"
			+ "yuv.y = texture2D(SamplerU, tc).r - 0.5;\n" + "yuv.z = texture2D(SamplerV, tc).r - 0.5;\n"
			+ "rgb = mat3( 1,   1,   1,\n" + "0,       -0.39465,  2.03211,\n" + "1.13983,   -0.58060,  0) * yuv;\n"
			+ "gl_FragColor = vec4(rgb, 1);\n" + "}\n";

	String VERTEX_SHADER = "attribute vec4 vPosition;\n" + "attribute vec2 a_texCoord;\n" + "varying vec2 tc;\n"
			+ "void main()\n" + "{\n" + "gl_Position = vPosition;\n" + "tc = a_texCoord;\n" + "}\n";

	private GLSurfaceView glSurfaceView;
	// ////////////////////new avs related//////////////////////
	// private boolean isNewAvs;
	private long mediaStreamId;
	private long decoderId;
	private Media media;
	private AtomicInteger timestamp = new AtomicInteger(0);// 解码录制视频时的时间戳，毫秒
	public boolean isShowVideo = true,isCloseStream = false;

	public MyRenderer(Context context, long handle, Media media, Handler handler)
	{
		this.mediaStreamId = handle;
		this.media = media;
		this.handler = handler;
		mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVertices.put(mVerticesData).position(0);

		mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		mIndices.put(mIndicesData).position(0);
		if (CommUtil.getPixelsWidth((Activity) context) > CommUtil.getPixelsHeight((Activity) context))
		{
			pixelsWidth = CommUtil.getPixelsHeight((Activity) context);
		} else
		{
			pixelsWidth = CommUtil.getPixelsWidth((Activity) context);
		}
		initYUV();
	}

	private void initYUV()
	{
		try{
			y = new byte[1280 * 720];
			u = new byte[640 * 360];
			v = new byte[640 * 360];
			Arrays.fill(y, 0, 1280 * 720, (byte) 0);
			Arrays.fill(u, 0, 640 * 360, (byte) 128);
			Arrays.fill(v, 0, 640 * 360, (byte) 128);
		}catch(OutOfMemoryError e){
			Log.e("renderer", "OutOfMemoryError");
			handler.sendEmptyMessage(1005);
		}
	}

	public void setStreamDecoder(long decoderId, int width, int height)
	{
		Log.d("media", "decoderId:" + decoderId + ",width:" + width + ",height:" + height);

		this.decoderId = decoderId;
		yuv_w = width;
		yuv_h = height;
	}

	/*public int getCurTime()
	{
		return timestamp.get();
	}*/
	
	public void setCurTime(int time){
		//timestamp.set(time);
		Message message = Message.obtain();
		message.what = 1099;
		message.obj = time;
		handler.sendMessage(message);
	}

	public void setGlSurfaceView(GLSurfaceView glSurfaceView)
	{
		this.glSurfaceView = glSurfaceView;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		GLES20.glActiveTexture(GLES20.GL_ACTIVE_TEXTURE);
		try
		{
			gl_w = width;
			gl_h = height;
			if (yuv_w == 0)
			{
				GLES20.glViewport(0, 0, width, height);
			} else
			{
				float w_h = (float) width / height;
				if (yuv_h * w_h > yuv_w)
				{
					display_w = height * yuv_w / yuv_h;
					display_h = height;
					play_pos_x = (width - display_w) / 2;
					play_pos_y = 0;
				} else
				{
					display_w = width;
					display_h = width * yuv_h / yuv_w;
					play_pos_x = 0;
					play_pos_y = (gl_h - display_h) / 2;
				}
				GLES20.glViewport(play_pos_x, play_pos_y, display_w, display_h);
				if (pixelsWidth == display_h)
				{
					GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				} else
				{
					GLES20.glClearColor(0.957f, 0.937f, 0.921f, 0.0f);
				}
				if (glSurfaceView != null)
				{
					glSurfaceView.getLayoutParams().width = display_w;
					glSurfaceView.getLayoutParams().height = display_h;
				}
				handler.sendEmptyMessage(8000);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		frameBuffer = IntBuffer.allocate(1);
		renderBuffer = IntBuffer.allocate(1);

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);

		GLES20.glGenFramebuffers(1, frameBuffer);
		GLES20.glGenRenderbuffers(1, renderBuffer);
		GLES20.glActiveTexture(GLES20.GL_ACTIVE_TEXTURE);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(0));
		GLES20.glClear(0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer.get(0));

		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, 1280, 720);

		parameterBufferHeigth = IntBuffer.allocate(1);
		parameterBufferWidth = IntBuffer.allocate(1);
		GLES20.glGetRenderbufferParameteriv(GLES20.GL_RENDERBUFFER, GLES20.GL_RENDERBUFFER_WIDTH, parameterBufferWidth);
		GLES20.glGetRenderbufferParameteriv(GLES20.GL_RENDERBUFFER, GLES20.GL_RENDERBUFFER_HEIGHT, parameterBufferHeigth);
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, renderBuffer.get(0));
		if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			System.out.println("gl frame buffer status != frame buffer complete");
		}
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glClear(0);

		mProgramObject = loadProgram(VERTEX_SHADER, FRAG_SHADER);

		mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "vPosition");
		mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord");

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		yTexture = GLES20.glGetUniformLocation(mProgramObject, "SamplerY");
		yTextureNames = new int[1];
		GLES20.glGenTextures(1, yTextureNames, 0);

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		uTexture = GLES20.glGetUniformLocation(mProgramObject, "SamplerU");
		uTextureNames = new int[1];
		GLES20.glGenTextures(1, uTextureNames, 0);

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		vTexture = GLES20.glGetUniformLocation(mProgramObject, "SamplerV");
		vTextureNames = new int[1];
		GLES20.glGenTextures(1, vTextureNames, 0);

		GLES20.glClearColor(0.957f, 0.937f, 0.921f, 0.0f);
	}

	boolean isFirst = true;
	int count = 0;
	int[] getWH = new int[2];
	@Override
	public void onDrawFrame(GL10 gl)
	{
		if (isShowVideo)
		{
			if(!isCloseStream){
				if (decoderId == 0||yuv_w==0||yuv_h==0)
					return;
				int tmp = 0;
				if(null != y && null != media){
					tmp = media.getVideoDecodedData(mediaStreamId, decoderId, y, u, v,getWH);
				}
				if (tmp > 0)
				{
					setCurTime(tmp);
				}
				if (getWH[0] != 0 && isFirst)
				{
					yuv_w = getWH[0];
					yuv_h = getWH[1];
					onSurfaceChanged(gl, gl_w, gl_h);
					isFirst = false;
					handler.sendEmptyMessage(8001);
				}
				if (!isFirst)
				{
					try{
						yBuffer = ByteBuffer.wrap(y);
						uBuffer = ByteBuffer.wrap(u);
						vBuffer = ByteBuffer.wrap(v);
						GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
						GLES20.glUseProgram(mProgramObject);
						mVertices.position(0);
						GLES20.glVertexAttribPointer(mPositionLoc, 3, GLES20.GL_FLOAT, false, 5 * 4, mVertices);
						mVertices.position(3);
						GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 5 * 4, mVertices);
		
						GLES20.glEnableVertexAttribArray(mPositionLoc);
						GLES20.glEnableVertexAttribArray(mTexCoordLoc);
		
						GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
						GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureNames[0]);
						GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, yuv_w, yuv_h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
						GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
						GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
						GLES20.glUniform1i(yTexture, 0);
		
						GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
						GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureNames[0]);
						GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, yuv_w / 2, yuv_h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, uBuffer);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
						GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
						GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
						GLES20.glUniform1i(uTexture, 1);
		
						GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
						GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureNames[0]);
						GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, yuv_w / 2, yuv_h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, vBuffer);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
						GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
						GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
						GLES20.glUniform1i(vTexture, 2);
		
						GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices);
						GLES20.glUseProgram(0);
					}catch(Exception e){}
				}
			}
		}
	}


	private static String readTextFileFromRawResource(final Context context, final int resourceId)
	{
		final InputStream inputStream = context.getResources().openRawResource(resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try
		{
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e)
		{
			return null;
		}

		return body.toString();
	}

	public static int loadShader(int type, String shaderSrc)
	{
		int shader;
		int[] compiled = new int[1];
		// Create the shader object
		shader = GLES20.glCreateShader(type);
		if (shader == 0)
		{
			return 0;
		}
		// Load the shader source
		GLES20.glShaderSource(shader, shaderSrc);
		// Compile the shader
		GLES20.glCompileShader(shader);
		// Check the compile status
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0)
		{
			GLES20.glDeleteShader(shader);
			return 0;
		}
		return shader;
	}

	public static int loadProgram(String vertShaderSrc, String fragShaderSrc)
	{
		int vertexShader;
		int fragmentShader;
		int programObject;
		int[] linked = new int[1];

		vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertShaderSrc);
		if (vertexShader == 0)
		{
			return 0;
		}

		fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderSrc);
		if (fragmentShader == 0)
		{
			GLES20.glDeleteShader(vertexShader);
			return 0;
		}

		// Create the program object
		programObject = GLES20.glCreateProgram();

		if (programObject == 0)
		{
			return 0;
		}

		GLES20.glAttachShader(programObject, vertexShader);
		GLES20.glAttachShader(programObject, fragmentShader);

		GLES20.glLinkProgram(programObject);

		GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

		if (linked[0] == 0)
		{
			GLES20.glDeleteProgram(programObject);
			return 0;
		}

		// Free up no longer needed shader resources
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);

		return programObject;
	}

	public void rawByteArray2RGBABitmap2(FileOutputStream b)
	{
		int yuvi = yuv_w * yuv_h;
		int uvi = 0;
		byte[] yuv = new byte[yuv_w * yuv_h * 3 / 2];
		System.arraycopy(y, 0, yuv, 0, yuvi);
		for (int i = 0; i < yuv_h / 2; i++)
		{
			for (int j = 0; j < yuv_w / 2; j++)
			{
				yuv[yuvi++] = v[uvi];
				yuv[yuvi++] = u[uvi++];
			}
		}
		YuvImage yuvImage = new YuvImage(yuv, ImageFormat.NV21, yuv_w, yuv_h, null);
		Rect rect = new Rect(0, 0, yuv_w, yuv_h);
		yuvImage.compressToJpeg(rect, 100, b);
	}

	public void clear(){
		mIndicesData = null;
		
		yBuffer = null;
		uBuffer = null;
		vBuffer = null;
		
		y = null;
		u = null;
		v = null;
		Log.d("media", "clear");
		System.gc();
	}
	
}