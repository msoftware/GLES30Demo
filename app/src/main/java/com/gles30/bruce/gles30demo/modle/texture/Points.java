package com.gles30.bruce.gles30demo.modle.texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES30;

import com.gles30.bruce.gles30demo.util.Constant;
import com.gles30.bruce.gles30demo.util.MatrixState;
import com.gles30.bruce.gles30demo.util.ShaderUtil;

public class Points {
    private int mProgram;// 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;// 总变换矩阵引用
    private int maPositionHandle; // 顶点位置属性引用

    private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private int vCount = 0;

    public Points(Context context) {
        //调用初始化顶点数据的方法
        initVertexData();
        //调用初始化着色器的方法
        initShader(context);
    }

    // 初始化顶点数据的方法
    public void initVertexData() {
        // 顶点坐标数据的初始化================begin============================
        vCount = 9;

        float vertices[] = new float[]{
                0, 0, 0,
                0, Constant.UNIT_SIZE * 2, 0,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE / 2, 0,
                -Constant.UNIT_SIZE / 3, Constant.UNIT_SIZE, 0,
                -Constant.UNIT_SIZE * 0.4f, -Constant.UNIT_SIZE * 0.4f, 0,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0,
                Constant.UNIT_SIZE * 0.2f, -Constant.UNIT_SIZE * 0.7f, 0,
                Constant.UNIT_SIZE / 2, -Constant.UNIT_SIZE * 3 / 2, 0,
                -Constant.UNIT_SIZE * 4 / 5, -Constant.UNIT_SIZE * 3 / 2, 0,
        };

        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
        mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);// 设置缓冲区起始位置
        // 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        // 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        // 顶点坐标数据的初始化================end============================
    }

    // 初始化着色器
    public void initShader(Context context) {
        // 加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile(context, "texture/vertex_point.sh");
        // 加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile(context, "texture/frag_point.sh");
        // 基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(int texId) {
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                MatrixState.getFinalMatrix(), 0);
        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);

        //开启纹理
        GLES30.glEnable(GLES30.GL_TEXTURE_2D);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);    //激活纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);//绑定纹理
        //执行绘制
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);
    }
}
