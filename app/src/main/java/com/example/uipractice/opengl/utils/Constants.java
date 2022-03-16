package com.example.uipractice.opengl.utils;

public class Constants {

    /**
     * 全屏顶点数据
     */
    public static float[] FULL_POINT_DATA = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    /**
     * Android 坐标系的纹理坐标
     */
    public static float[] ANDROID_POINT_DATA = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };

    /**
     * 投影矩阵数组
     */
    public static float[] PROJECTIONMATRIX = {
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
    };
}
