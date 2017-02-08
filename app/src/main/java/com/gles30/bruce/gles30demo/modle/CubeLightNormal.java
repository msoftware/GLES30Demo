package com.gles30.bruce.gles30demo.modle;

import java.nio.ByteBuffer;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES30;

import com.gles30.bruce.gles30demo.util.Constant;
import com.gles30.bruce.gles30demo.util.MatrixState;
import com.gles30.bruce.gles30demo.util.ShaderUtil;

//立方体
public class CubeLightNormal {
    int mProgram;// 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;// 总变换矩阵引用
    private int muMMatrixHandle;//位置、旋转变换矩阵引用
    private int muRHandle;// 立方体的半径属性引用
    private int maPositionHandle; // 顶点位置属性引用
    private int maNormalHandle; //顶点法向量属性引用
    private int maLightLocationHandle;//光源位置属性引用
    private int maCameraHandle; //摄像机位置属性引用


    FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    int vCount = 0;
    public float yAngle = 0;// 绕y轴旋转的角度
    public float xAngle = 0;// 绕x轴旋转的角度

    public CubeLightNormal(Context context) {
        // 调用初始化顶点数据的方法
        initVertexData();
        // 调用初始化着色器的方法
        initShader(context);
    }

    // 初始化顶点数据的方法
    public void initVertexData() {

        //顶点坐标数据的初始化================begin============================
        vCount = 6 * 6;//顶点数

        float vertices[] = new float[]
                {
                        //立方体前面
                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE, //0
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//1
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE, //2

                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE, //0
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE, //2
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,//3
                        //后面
                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//0
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//1
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2

                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//0
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//3
                        //左面
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//1
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2

                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,//3
                        //右面
                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,//1
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2

                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2
                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//3
                        //上面
                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//1
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2

                        Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2
                        -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,//3
                        //下面
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,//1
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2

                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,//0
                        -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//2
                        Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,//3
                };

        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();// 转换为float型缓冲
        mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);// 设置缓冲区起始位置
        // 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        // 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        float normals[] = new float[]{
                //前面
                0, 0, 1, 0, 0, 1, 0, 0, 1,
                0, 0, 1, 0, 0, 1, 0, 0, 1,
                //后面
                0, 0, -1, 0, 0, -1, 0, 0, -1,
                0, 0, -1, 0, 0, -1, 0, 0, -1,
                //左面
                -1, 0, 0, -1, 0, 0, -1, 0, 0,
                -1, 0, 0, -1, 0, 0, -1, 0, 0,
                //右面
                1, 0, 0, 1, 0, 0, 1, 0, 0,
                1, 0, 0, 1, 0, 0, 1, 0, 0,
                //上面
                0, 1, 0, 0, 1, 0, 0, 1, 0,
                0, 1, 0, 0, 1, 0, 0, 1, 0,
                //下面
                0, -1, 0, 0, -1, 0, 0, -1, 0,
                0, -1, 0, 0, -1, 0, 0, -1, 0,
        };
        //创建绘制顶点法向量缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
    }

    // 初始化着色器
    public void initShader(Context context) {
        // 加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile(context, "vertex_cube_light_normal.sh");
        // 加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile(context, "frag_cube_light_normal.sh");
        // 基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        // 获取程序中立方体半径引用
        muRHandle = GLES30.glGetUniformLocation(mProgram, "uR");
        //获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
        MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
        MatrixState.rotate(0, 0, 0, 1);//绕Z轴转动
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        // 将半径尺寸传入渲染管线
        float r = 1.0f;
        GLES30.glUniform1f(muRHandle, r * Constant.UNIT_SIZE);
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.getLightPositionFloatBuffer());
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.getCameraFloatBuffer());

        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maNormalHandle);// 启用顶点法向量数据数组
        // 绘制立方体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}
