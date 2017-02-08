package com.gles30.bruce.gles30demo.modle;

import android.content.Context;
import android.opengl.GLES30;

import com.gles30.bruce.gles30demo.util.Constant;
import com.gles30.bruce.gles30demo.util.MatrixState;
import com.gles30.bruce.gles30demo.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

//球
public class BallAllLight {
    private int mProgram;// 自定义渲染管线着色器程序id
    private int muMVPMatrixLocation;//总变换矩阵引用
    private int maPositionLocation; //顶点位置属性引用
    private int muRLocation;//球的半径属性引用

    private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private int vCount = 0;
    public float yAngle = 0;// 绕y轴旋转的角度
    public float xAngle = 0;// 绕x轴旋转的角度
    private float r = 0.8f;
    private int muMMatrixLocation;
    private int maNormalLocation;
    private int maLightLocationLocation;
    private FloatBuffer mNormalBuffer;
    private int maCameraHandle;

    public BallAllLight(Context context) {
        // 初始化顶点数据的方法
        initVertexData();
        // 初始化着色器的方法
        initShader(context);
    }

    // 初始化顶点数据的方法
    public void initVertexData() {
        // 顶点坐标数据的初始化================begin============================
        ArrayList<Float> alVertix = new ArrayList<>();// 存放顶点坐标的ArrayList
        final int angleSpan = 10;// 将球进行单位切分的角度
        // 垂直方向angleSpan度一份
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan) {
            // 水平方向angleSpan度一份
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan) {
                // 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                float x0 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle)));
                float y0 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle)));
                float z0 = (float) (r * Constant.UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x1 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y1 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z1 = (float) (r * Constant.UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x2 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y2 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z2 = (float) (r * Constant.UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));

                float x3 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.cos(Math.toRadians(hAngle)));
                float y3 = (float) (r * Constant.UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle)));
                float z3 = (float) (r * Constant.UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
                alVertix.add(x0);
                alVertix.add(y0);
                alVertix.add(z0);

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
            }
        }
        vCount = alVertix.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        // 将alVertix中的坐标值转存到一个float数组中
        float vertices[] = new float[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }

        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个整数四个字节
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();// 转换为float型缓冲
        mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);// 设置缓冲区起始位置
        // 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        // 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        //创建绘制顶点法向量缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length * 4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
    }

    // 初始化着色器
    public void initShader(Context context) {
        // 加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile(context, "vertex_all.sh");
        // 加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile(context, "frag_all.sh");
        // 基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用
        maPositionLocation = GLES30.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中总变换矩阵引用
        muMVPMatrixLocation = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 获取程序中球半径引用
        muRLocation = GLES30.glGetUniformLocation(mProgram, "uR");

        //获取位置、旋转变换矩阵引用
        muMMatrixLocation = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中顶点法向量属性引用
        maNormalLocation = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用
        maLightLocationLocation = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置一致变量引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
        MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
        MatrixState.rotate(0, 0, 0, 1);//绕Z轴转动
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixLocation, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixLocation, 1, false, MatrixState.getMMatrix(), 0);
        // 将半径尺寸传入渲染管线
        GLES30.glUniform1f(muRLocation, r * Constant.UNIT_SIZE);
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maLightLocationLocation, 1, MatrixState.getLightPositionFloatBuffer());
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.getCameraFloatBuffer());

        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionLocation, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(maNormalLocation, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionLocation);
        GLES30.glEnableVertexAttribArray(maNormalLocation);// 启用顶点法向量数据数组
        // 绘制球
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}
