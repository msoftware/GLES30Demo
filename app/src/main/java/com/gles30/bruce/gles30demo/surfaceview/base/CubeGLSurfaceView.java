package com.gles30.bruce.gles30demo.surfaceview.base;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.gles30.bruce.gles30demo.modle.base.Cube;
import com.gles30.bruce.gles30demo.util.Constant;
import com.gles30.bruce.gles30demo.util.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Update by Bruce on 2017/1/23.
 */

public class CubeGLSurfaceView extends GLSurfaceView {

    public CubeGLSurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        SceneRenderer mRenderer = new SceneRenderer();
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        Cube cube;//立方体对象引用

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            //创建立方体对象
            cube = new Cube(getContext());
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算视口的宽高比
            Constant.ratio = (float) width / height;
            // 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-Constant.ratio * 0.8f, Constant.ratio * 1.2f, -1, 1, 20, 100);
            // 调用此方法产生摄像机矩阵
            MatrixState.setCamera(-24f, 6f, 60,
                    0f, 0f, 0f,
                    0f, 1.0f, 0.0f);

            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            //绘制原立方体
            MatrixState.pushMatrix();//保护现场
            cube.drawSelf();//绘制立方体
            MatrixState.popMatrix();//恢复现场

            //绘制变换后的立方体
            MatrixState.pushMatrix();//保护现场
            MatrixState.translate(3.5f, 0, 0);//沿x方向平移3.5
            MatrixState.rotate(30, 0, 0, 1);
            MatrixState.scale(0.4f, 2f, 0.6f);                    //x、y、z 3个方向按各自的缩放因子进行缩放
            cube.drawSelf();//绘制立方体
            MatrixState.popMatrix();//恢复现场
        }
    }
}