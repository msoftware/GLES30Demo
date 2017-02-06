package com.gles30.bruce.gles30demo.modle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES30;

import com.gles30.bruce.gles30demo.util.Constant;
import com.gles30.bruce.gles30demo.util.MatrixState;
import com.gles30.bruce.gles30demo.util.ShaderUtil;

//颜色圆
public class CircleElement {
    private int mProgram;// 自定义渲染管线着色器程序id
    private int muMVPMatrixLocation;// 总变换矩阵引用
    private int maPositionLocation; // 顶点位置属性引用
    private int maColorLocation; // 顶点颜色属性引用

    private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;// 顶点着色数据缓冲
    private ByteBuffer mIndexBuffer;// 顶点索引数据缓冲
    private int iCount = 0;//索引数量

    public CircleElement(Context context) {
        // 初始化顶点坐标与着色数据
        initVertexData();
        // 初始化shader
        initShader(context);
    }

    // 初始化顶点坐标与着色数据的方法
    private void initVertexData() {
        // 顶点坐标数据的初始化================begin============================
        int n = 10;
        int vCount = n + 2;

        float angdegSpan = 360.0f / n;
        float[] vertices = new float[vCount * 3];// 顶点坐标数据
        // 坐标数据初始化
        int count = 0;
        vertices[count++] = 0;
        vertices[count++] = 0;
        vertices[count++] = 0;
        for (float angdeg = 0; Math.ceil(angdeg) <= 360; angdeg += angdegSpan) {
            double angrad = Math.toRadians(angdeg);// 当前弧度
            // 当前点
            vertices[count++] = (float) (-Constant.UNIT_SIZE * Math.sin(angrad));// 顶点坐标
            vertices[count++] = (float) (Constant.UNIT_SIZE * Math.cos(angrad));
            vertices[count++] = 0;
        }
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

        // 三角形构造索引数据初始化==========begin==========================
        byte indices[] = {//创建索引数组
                0, 1, 2,
                0, 2, 3,
                0, 3, 4,
                0, 4, 5,
                0, 5, 6,
                0, 6, 7,
                0, 7, 8,
                0, 8, 9,
                0, 9, 10,
                0, 10, 1
        };
        iCount = indices.length;//索引数组的长度

        // 创建三角形构造索引数据缓冲
        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);// 向缓冲区中放入三角形构造索引数据
        mIndexBuffer.position(0);// 设置缓冲区起始位置
        // 三角形构造索引数据初始化==========end==============================

        // 顶点着色数据的初始化================begin============================
        // 顶点颜色值数组，每个顶点4个色彩值RGBA
        count = 0;
        float colors[] = new float[vCount * 4];
        colors[count++] = 1;
        colors[count++] = 1;
        colors[count++] = 1;
        colors[count++] = 0;
        for (int i = 4; i < colors.length; i += 4) {
            colors[count++] = 0;
            colors[count++] = 1;
            colors[count++] = 0;
            colors[count++] = 0;
        }
        // 创建顶点着色数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();// 转换为Float型缓冲
        mColorBuffer.put(colors);// 向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);// 设置缓冲区起始位置
        // 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        // 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        // 顶点着色数据的初始化================end============================
    }

    // 初始化着色器
    private void initShader(Context context) {
        // 加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile(context, "vertex.sh");
        // 加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile(context, "fragment.sh");
        // 基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用id
        maPositionLocation = GLES30.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中顶点颜色属性引用id
        maColorLocation = GLES30.glGetAttribLocation(mProgram, "aColor");
        // 获取程序中总变换矩阵引用id
        muMVPMatrixLocation = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixLocation, 1, false,
                MatrixState.getFinalMatrix(), 0);
        //将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(maPositionLocation, 3, GLES30.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        //将顶点颜色数据送入渲染管线
        GLES30.glVertexAttribPointer(maColorLocation, 4, GLES30.GL_FLOAT, false,
                4 * 4, mColorBuffer);
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionLocation);
        //启用顶点颜色数据数组
        GLES30.glEnableVertexAttribArray(maColorLocation);
        // 绘制图形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, iCount,
                GLES30.GL_UNSIGNED_BYTE, mIndexBuffer);//用索引法绘制图形
    }
}
