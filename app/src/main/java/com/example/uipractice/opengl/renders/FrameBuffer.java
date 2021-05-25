package com.example.uipractice.opengl.renders;

import android.opengl.GLES20;

public class FrameBuffer {
    private int mWidth;
    private int mHeight;
    private int frameBufferId;
    private int textureId;
    public FrameBuffer() {
        mWidth=0;
        mHeight=0;
        frameBufferId=0;
        textureId=0;
    }
    public int getTextureId() {
        return textureId;
    }

    public boolean isInstantiation() {
        return mWidth!=0||mHeight!=0;
    }

    public boolean setup(int width, int height){
        this.mWidth = width;
        this.mHeight = height;

        final int frameBuffers[] = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        if (frameBuffers[0] == 0) {
            int i = GLES20.glGetError();
            throw new RuntimeException("Could not create a new frame buffer object, glErrorString : "+ GLES20.glGetString(i));
        }
        frameBufferId = frameBuffers[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId);

        textureId = createFBOTexture(mWidth, mHeight, GLES20.GL_RGBA);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return true;
    }

    private int createFBOTexture(int width, int height, int format) {
        final int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        if(textureIds[0] == 0){
            int i = GLES20.glGetError();
            throw new RuntimeException("Could not create a new texture buffer object, glErrorString : "+ GLES20.glGetString(i));
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height,
                0, format, GLES20.GL_UNSIGNED_BYTE, null);
        return textureIds[0];
    }

    public void bind(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId);
    }

    public void unbind(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void release(){
        mWidth = 0;
        mHeight = 0;
        GLES20.glDeleteFramebuffers(1, new int[]{frameBufferId}, 0);
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        frameBufferId = 0;
        textureId = 0;
    }
}
