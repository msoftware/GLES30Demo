package com.gles30.bruce.gles30demo.surfaceview.base;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.gles30.bruce.gles30demo.modle.base.BeltElement;
import com.gles30.bruce.gles30demo.modle.base.CircleElement;
import com.gles30.bruce.gles30demo.util.Constant;
import com.gles30.bruce.gles30demo.util.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ElementSurface extends GLSurfaceView {

    public ElementSurface(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        SceneRenderer mRenderer = new SceneRenderer();
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        BeltElement belt;//条状物
        CircleElement circle;//圆

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            //创建圆对象
            circle = new CircleElement(getContext());
            //创建条状物对象
            belt = new BeltElement(getContext());
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口的大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算视口的宽高比
            Constant.ratio = (float) width / height;
            // 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, 20, 100);
            // 调用此方法产生摄像机矩阵
            MatrixState.setCamera(0, 8f, 30, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //保护现场
            MatrixState.pushMatrix();
            //绘制条状物
            MatrixState.pushMatrix();
            MatrixState.translate(-1.3f, 0, 0);//沿x轴负方向平移
            belt.drawSelf();
            MatrixState.popMatrix();
            //绘制圆
            MatrixState.pushMatrix();
            MatrixState.translate(1.3f, 0, 0);//沿x轴正方向平移
            circle.drawSelf();
            MatrixState.popMatrix();
            //恢复现场
            MatrixState.popMatrix();
        }


    }
}
