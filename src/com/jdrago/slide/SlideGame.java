package com.jdrago.slide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class SlideGame implements GLSurfaceView.Renderer
{
    public SlideGame(Context context)
    {
        context_ = context;
        verts_ = ByteBuffer.allocateDirect(vertData_.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verts_.put(vertData_).position(0);
    }

    public void onDrawFrame(GL10 glUnused)
    {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(shaderProgram_);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID_);

        verts_.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(posHandle_, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, verts_);
        checkGlError("glVertexAttribPointer maPosition");
        verts_.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glEnableVertexAttribArray(posHandle_);
        checkGlError("glEnableVertexAttribArray posHandle");
        GLES20.glVertexAttribPointer(texHandle_, 2, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, verts_);
        checkGlError("glVertexAttribPointer texHandle");
        GLES20.glEnableVertexAttribArray(texHandle_);
        checkGlError("glEnableVertexAttribArray texHandle");

        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
//        Matrix.setRotateM(modelMatrix_, 0, angle, 0, 0, 1.0f);
        Matrix.multiplyMM(viewProjMatrix_, 0, viewMatrix_, 0, modelMatrix_, 0);
        Matrix.multiplyMM(viewProjMatrix_, 0, projMatrix_, 0, viewProjMatrix_, 0);

        GLES20.glUniformMatrix4fv(viewProjMatrixHandle_, 1, false, viewProjMatrix_, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        checkGlError("glDrawArrays");
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projMatrix_, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        shaderProgram_ = createProgram(vertShader_, fragShader_);
        if (shaderProgram_ == 0)
        {
            return;
        }
        posHandle_ = GLES20.glGetAttribLocation(shaderProgram_, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (posHandle_ == -1)
        {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        texHandle_ = GLES20.glGetAttribLocation(shaderProgram_, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (texHandle_ == -1)
        {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        viewProjMatrixHandle_ = GLES20.glGetUniformLocation(shaderProgram_, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (viewProjMatrixHandle_ == -1)
        {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureID_ = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID_);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        InputStream is = context_.getResources().openRawResource(R.raw.robot);
        Bitmap bitmap;
        try
        {
            bitmap = BitmapFactory.decodeStream(is);
        } finally
        {
            try
            {
                is.close();
            } catch (IOException e)
            {
                // Ignore.
            }
        }

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        Matrix.setLookAtM(viewMatrix_, 0,
                0, 0, -5,         // eye
                0f, 0f, 0f,       // center
                0f, 1.0f, 0.0f);  // up
    }

    private int loadShader(int shaderType, String source)
    {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0)
        {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0)
            {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource)
    {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0)
        {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0)
        {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0)
        {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE)
            {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op)
    {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
        {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private final float[] vertData_ = {
            // X, Y, Z, U, V
            -1.0f, -0.5f, 0, -0.5f, 0.0f,
            1.0f, -0.5f, 0, 1.5f, -0.0f,
            0.0f, 1.11803399f, 0, 0.5f, 1.61803399f};

    private FloatBuffer verts_;

    private final String vertShader_ =
            "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = aTextureCoord;\n" +
                    "}\n";

    private final String fragShader_ =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private float[] viewProjMatrix_ = new float[16];
    private float[] projMatrix_ = new float[16];
    private float[] modelMatrix_ = new float[16];
    private float[] viewMatrix_ = new float[16];

    private int shaderProgram_;
    private int textureID_;
    private int viewProjMatrixHandle_;
    private int posHandle_;
    private int texHandle_;

    private Context context_;
    private static String TAG = "SlideGame";
}
