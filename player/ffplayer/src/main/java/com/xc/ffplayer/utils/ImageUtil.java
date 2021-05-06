package com.xc.ffplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

@SuppressWarnings("all")
public final class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();

    /**
     * YUV420p I420=YU12
     */
    public static final int YUV420PYU12 = 0;
    public static final int YUV420PI420 = YUV420PYU12;
    public static final int YUV420PYV12 = 3;
    /**
     * YUV420SP NV12
     */
    public static final int YUV420SPNV12 = 1;

    /**
     * YUV420SP NV21
     */
    public static final int YUV420SPNV21 = 2;


    /**
     * 高速变换，减少内存分配的开销和防OOM
     */
    private static byte[] Bytes_y = null;
    private static byte[] uBytes = null;
    private static byte[] vBytes = null;
    private static byte[] Bytes_uv = null;


    /**
     * Image YUV420_888转NV12， NV21， I420（YU12）
     * @param image
     * @param type
     * @param yuvBytes
     */
    public static void getBytesFromImageAsType(Image image, int type, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            //byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

            //临时存储uv数据的
            //byte[] uBytes = new byte[width * height / 4];
            //byte[] vBytes = new byte[width * height / 4];
            if (uBytes == null) {
                uBytes = new byte[width * height / 4];
            }
            if (vBytes == null) {
                vBytes = new byte[width * height / 4];
            }

            int uIndex = 0;
            int vIndex = 0;
            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();
                //int cap = buffer.capacity();


                //如果pixelsStride==2，一般的Y的buffer长度=640*480，UV的长度=640*480/2-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                byte[] bytes;
                if (buffer.capacity() >=(width*height)) {
                    if (Bytes_y == null){
                        Bytes_y = new byte[buffer.capacity()];
                    }
                    bytes = Bytes_y;
                }else if(buffer.capacity() >= (((width*height)/2) - 1)){
                    if (Bytes_uv == null){
                        Bytes_uv = new byte[buffer.capacity()];
                    }
                    bytes = Bytes_uv;
                }else{
                    bytes = new byte[buffer.capacity()];
                }
                buffer.get(bytes);

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[uIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                } else if (i == 2) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[vIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                }
            }

            //   image.close();

            //根据要求的结果类型进行填充
            switch (type) {
                case YUV420PI420:
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SPNV12:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case YUV420SPNV21:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
                case YUV420PYV12:
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex, vBytes.length);
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex + vBytes.length, uBytes.length);
                    break;
            }
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
    }

    /**
     * Image Yuv420_888转Yu12
     * @param image Image
     * @param yuvBytes data
     * 又叫I420,先存Y，再存U，最后存v，四个Y对应一个U，一个V 4X4图像
     * YYYY
     * YYYY
     * YYYY
     * YYYY
     * UUUU
     * VVVV
     */
    @SuppressWarnings("unused")
    public static void getBytes420PYu12(Image image, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            int Y_pixelsStride = planes[0].getPixelStride();
            int Y_rowStride = planes[0].getRowStride();
            int Y_srcIndex = 0;
            int Y_dstIndex = 0;
            ByteBuffer tt = planes[0].getBuffer();
            tt.clear();
            for (int j = 0; j < height; j++) {
                tt.get(yuvBytes, Y_dstIndex, width);
                Y_srcIndex += Y_rowStride;
                Y_dstIndex += width;
                if (Y_srcIndex < tt.capacity()) {
                    tt.position(Y_srcIndex);
                }
            }

            int U_pixelsStride = planes[1].getPixelStride();
            int U_rowStride = planes[1].getRowStride();
            int U_srcIndex = 0;
            int U_dstIndex = 0;
            ByteBuffer tt2 = planes[1].getBuffer();
            tt2.clear();
            for (int j = 0; j < height / 2; j++) {
                for (int k = 0; k < width/2 ; k++) {
                    yuvBytes[Y_dstIndex+U_dstIndex] = tt2.get(U_srcIndex);
                    U_srcIndex += U_pixelsStride;
                    U_dstIndex += 1;
                }
                if (U_pixelsStride == 2) {
                    U_srcIndex += U_rowStride - width;
                } else if (U_pixelsStride == 1) {
                    U_srcIndex += U_rowStride - width / 2;
                }
            }

            int V_pixelsStride = planes[2].getPixelStride();
            int V_rowStride = planes[2].getRowStride();
            int V_srcIndex = 0;
            int V_dstIndex = 0;
            ByteBuffer tt3 = planes[2].getBuffer();
            tt3.clear();
            for (int j = 0; j < height / 2; j++) {
                for (int k = 0; k < width/2 ; k++) {
                    yuvBytes[Y_dstIndex+U_dstIndex+V_dstIndex] = tt3.get(V_srcIndex);
                    V_srcIndex += V_pixelsStride;
                    V_dstIndex += 1;
                }
                if (V_pixelsStride == 2) {
                    V_srcIndex += V_rowStride - width;
                } else if (V_pixelsStride == 1) {
                    V_srcIndex += V_rowStride - width / 2;
                }
            }

        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
    }

    /**
     * Image Yuv420_888转YV12
     * @param image Image
     * @param yuvBytes data
     * 先存Y，再存V，最后存U，四个Y对应一个U，一个V 4X4图像
     * YYYY
     * YYYY
     * YYYY
     * YYYY
     * VVVV
     * UUUU
     */
    @SuppressWarnings("unused")
    public static void getBytes420PYv12(Image image, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            int Y_pixelsStride = planes[0].getPixelStride();
            int Y_rowStride = planes[0].getRowStride();
            int Y_srcIndex = 0;
            int Y_dstIndex = 0;
            ByteBuffer tt = planes[0].getBuffer();
            tt.clear();
            for (int j = 0; j < height; j++) {
                tt.get(yuvBytes, Y_dstIndex, width);
                Y_srcIndex += Y_rowStride;
                Y_dstIndex += width;
                if (Y_srcIndex < tt.capacity()) {
                    tt.position(Y_srcIndex);
                }
            }

            int U_pixelsStride = planes[2].getPixelStride();
            int U_rowStride = planes[2].getRowStride();
            int U_srcIndex = 0;
            int U_dstIndex = 0;
            ByteBuffer tt2 = planes[2].getBuffer();
            tt2.clear();
            for (int j = 0; j < height / 2; j++) {
                for (int k = 0; k < width/2 ; k++) {
                    yuvBytes[Y_dstIndex+U_dstIndex] = tt2.get(U_srcIndex);
                    U_srcIndex += U_pixelsStride;
                    U_dstIndex += 1;
                }
                if (U_pixelsStride == 2) {
                    U_srcIndex += U_rowStride - width;
                } else if (U_pixelsStride == 1) {
                    U_srcIndex += U_rowStride - width / 2;
                }
            }

            int V_pixelsStride = planes[1].getPixelStride();
            int V_rowStride = planes[1].getRowStride();
            int V_srcIndex = 0;
            int V_dstIndex = 0;
            ByteBuffer tt3 = planes[1].getBuffer();
            tt3.clear();
            for (int j = 0; j < height / 2; j++) {
                for (int k = 0; k < width/2 ; k++) {
                    yuvBytes[Y_dstIndex+U_dstIndex+V_dstIndex] = tt3.get(V_srcIndex);
                    V_srcIndex += V_pixelsStride;
                    V_dstIndex += 1;
                }
                if (V_pixelsStride == 2) {
                    V_srcIndex += V_rowStride - width;
                } else if (V_pixelsStride == 1) {
                    V_srcIndex += V_rowStride - width / 2;
                }
            }

        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
    }

    /**
     * Image Yuv420_888转Nu12
     * @param image Image
     * @param yuvBytes data
     * 先存Y，再UV交叉存储，U前V后，四个Y对应一个U，一个V 4X4图像
     * YYYY
     * YYYY
     * YYYY
     * YYYY
     * UVUV
     * UVUV
     */
    public static void getBytes420SPNv12(Image image, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            int Y_pixelsStride = planes[0].getPixelStride();
            int Y_rowStride = planes[0].getRowStride();
            int Y_srcIndex = 0;
            int Y_dstIndex = 0;
            ByteBuffer tt = planes[0].getBuffer();
            tt.clear();
            for (int j = 0; j < height; j++) {
                //System.arraycopy(planes[0].getBuffer().array(), Y_srcIndex, yuvBytes, Y_dstIndex, width);
                tt.get(yuvBytes, Y_dstIndex, width);
                Y_srcIndex += Y_rowStride;
                Y_dstIndex += width;
                if (Y_srcIndex < tt.capacity()) {
                    tt.position(Y_srcIndex);
                }
            }

            int U_pixelsStride = planes[1].getPixelStride();
            int U_rowStride = planes[1].getRowStride();
            int U_srcIndex = 0;
            int U_dstIndex = 0;
            ByteBuffer tt2 = planes[1].getBuffer();
            tt2.clear();
            for (int j = 0; j < height / 2; j++) {
                //System.arraycopy(planes[1].getBuffer().array(), U_srcIndex, yuvBytes, Y_dstIndex+U_dstIndex, width);
                if (j == (height /2 -1)){
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width-1);
                }else {
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width);
                }
                U_srcIndex += U_rowStride;
                U_dstIndex += width;
                if (U_srcIndex < tt2.capacity()) {
                    tt2.position(U_srcIndex);
                }
            }

            //System.arraycopy(planes[0].getBuffer().array(), 0, yuvBytes, 0, width*height);
            //System.arraycopy(planes[2].getBuffer().array(), 0, yuvBytes, width*height, width*height/2);
            int V_pixelsStride = planes[2].getPixelStride();
            int V_rowStride = planes[2].getRowStride();
            int V_srcIndex = 0;
            int V_dstIndex = 0;
            ByteBuffer tt3 = planes[2].getBuffer();
            tt3.clear();
            for (int j = 0; j < height / 2; j++) {
                for (int k = 0; k < width/2 ; k++) {
                    yuvBytes[Y_dstIndex+V_dstIndex+1] = tt3.get(V_srcIndex);
                    V_srcIndex += V_pixelsStride;
                    V_dstIndex += V_pixelsStride;
                }
                if (V_pixelsStride == 2) {
                    V_srcIndex += V_rowStride - width;
                } else if (V_pixelsStride == 1) {
                    V_srcIndex += V_rowStride - width / 2;
                }
            }

        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
    }

    /**
     * Image Yuv420_888转NV21
     * @param image Image
     * @param yuvBytes data
     * 先存Y，再VU交叉存储，V前U后，四个Y对应一个U，一个V 4X4图像
     * YYYY
     * YYYY
     * YYYY
     * YYYY
     * VUVU
     * VUVU
     */
    @SuppressWarnings("unused")
    public static void getBytes420SPNv21(Image image, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            int Y_pixelsStride = planes[0].getPixelStride();
            int Y_rowStride = planes[0].getRowStride();
            int Y_srcIndex = 0;
            int Y_dstIndex = 0;
            ByteBuffer tt = planes[0].getBuffer();
            tt.clear();
            for (int j = 0; j < height; j++) {
                //System.arraycopy(planes[0].getBuffer().array(), Y_srcIndex, yuvBytes, Y_dstIndex, width);
                tt.get(yuvBytes, Y_dstIndex, width);
                Y_srcIndex += Y_rowStride;
                Y_dstIndex += width;
                if (Y_srcIndex < tt.capacity()) {
                    tt.position(Y_srcIndex);
                }
            }

            int U_pixelsStride = planes[2].getPixelStride();
            int U_rowStride = planes[2].getRowStride();
            int U_srcIndex = 0;
            int U_dstIndex = 0;
            ByteBuffer tt2 = planes[2].getBuffer();
            tt2.clear();
            for (int j = 0; j < height / 2; j++) {
                //System.arraycopy(planes[1].getBuffer().array(), U_srcIndex, yuvBytes, Y_dstIndex+U_dstIndex, width);
                if (j == (height /2 -1)){
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width-1);
                }else {
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width);
                }
                U_srcIndex += U_rowStride;
                U_dstIndex += width;
                if (U_srcIndex < tt2.capacity()) {
                    tt2.position(U_srcIndex);
                }
            }

            int V_pixelsStride = planes[1].getPixelStride();
            int V_rowStride = planes[1].getRowStride();
            int V_srcIndex = 0;
            int V_dstIndex = 0;
            ByteBuffer tt3 = planes[1].getBuffer();
            tt3.clear();
            for (int j = 0; j < height / 2; j++) {
                for (int k = 0; k < width/2 ; k++) {
                    yuvBytes[Y_dstIndex+V_dstIndex+1] = tt3.get(V_srcIndex);
                    V_srcIndex += V_pixelsStride;
                    V_dstIndex += V_pixelsStride;
                }
                if (V_pixelsStride == 2) {
                    V_srcIndex += V_rowStride - width;
                } else if (V_pixelsStride == 1) {
                    V_srcIndex += V_rowStride - width / 2;
                }
            }

        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
    }


//    @SuppressWarnings("unused")
//    public static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
//        ByteStreamWrapper out = ByteStreamPool.get();
//        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
//        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
//        byte[] result = new by;
//        ByteStreamPool.ret2pool(out);
//        return result;
//    }

    @SuppressWarnings("unused")
    public static void Rgba2Bgr(byte[] src, byte[] dest, boolean isAlpha){
        if (isAlpha) {
            //RGBA TO BGRA
            if (src != null && src.length == dest.length) {
                for (int i = 0; i < src.length / 4; i++) {
                    dest[i * 4] = src[i * 4 + 2];        //B
                    dest[i * 4 + 1] = src[i * 4 + 1];    //G
                    dest[i * 4 + 2] = src[i * 4];        //R
                    dest[i * 4 + 3] = src[i * 4 + 3];        //a
                }
            }else{
            }
        }else{
            //RGBA TO BGR
            if (src != null && src.length == (dest.length/4)*3) {
                for (int i = 0; i < src.length / 4; i++) {
                    dest[i * 3] = src[i * 4 + 2];        //B
                    dest[i * 3 + 1] = src[i * 4 + 1];    //G
                    dest[i * 3 + 2] = src[i * 4];        //R
                }
            }else{
            }
        }
    }

    //bitmap
    @SuppressWarnings("unused")
    public static Bitmap getOriBitmap(byte[] jpgArray){
        return BitmapFactory.decodeByteArray(jpgArray,
                0, jpgArray.length);
    }
    //RGBA
    public static byte[] getOriBitmapRgba(byte[] jpgArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpgArray,
                0, jpgArray.length);
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    @SuppressWarnings("unused")
    public static byte[] getJpegByte(byte[] rgba, int w, int h){
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        ByteBuffer buf = ByteBuffer.wrap(rgba);
        bm.copyPixelsFromBuffer(buf);
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        return fos.toByteArray();
    }
    @SuppressWarnings("unused")
    public static int[] byte2int(byte[] byteArray){
        IntBuffer intBuf =
                ByteBuffer.wrap(byteArray)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);

        return array;
    }
    @SuppressWarnings("unused")
    public byte[] int2byte(int[] intArray){
        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);
        byte[] byteConverted = byteBuffer.array();
        for (int i = 0; i < 840; i++) {
            Log.d("Bytes sfter Insert", ""+byteConverted[i]);
        }

        return byteConverted;
    }
//    @SuppressWarnings("unused")
//    public void testXXX(String path){
//        Bitmap bm = BitmapFactory.decodeFile(path);
//        ByteStreamWrapper baos = ByteStreamPool.get();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] bytes = baos.toByteArray();
//        ByteStreamPool.ret2pool(baos);
//        long x = System.currentTimeMillis();
//        System.out.println("jiaXXX"+ "testXXX");
//        getOriBitmapRgba(bytes);
//        System.out.println("jiaXXX"+ "testXXX"+(System.currentTimeMillis()-x));
//    }

    //Image to nv21
    @SuppressWarnings("unused")
    private ByteBuffer imageToByteBuffer(final Image image) {
        final Rect crop = image.getCropRect();
        final int width = crop.width();
        final int height = crop.height();

        final Image.Plane[] planes = image.getPlanes();
        final int bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
        final ByteBuffer output = ByteBuffer.allocateDirect(bufferSize);

        int channelOffset = 0;
        int outputStride = 0;

        for (int planeIndex = 0; planeIndex < planes.length; planeIndex++) {
            if (planeIndex == 0) {
                channelOffset = 0;
                outputStride = 1;
            } else if (planeIndex == 1) {
                channelOffset = width * height + 1;
                outputStride = 2;
            } else if (planeIndex == 2) {
                channelOffset = width * height;
                outputStride = 2;
            }

            final ByteBuffer buffer = planes[planeIndex].getBuffer();
            final int rowStride = planes[planeIndex].getRowStride();
            final int pixelStride = planes[planeIndex].getPixelStride();
            byte[] rowData = new byte[rowStride];

            final int shift = (planeIndex == 0) ? 0 : 1;
            final int widthShifted = width >> shift;
            final int heightShifted = height >> shift;

            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));

            for (int row = 0; row < heightShifted; row++) {
                final int length;

                if (pixelStride == 1 && outputStride == 1) {
                    length = widthShifted;
                    buffer.get(output.array(), channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (widthShifted - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);

                    for (int col = 0; col < widthShifted; col++) {
                        output.array()[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }

                if (row < heightShifted - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }

        return output;
    }

//    @SuppressWarnings("unused")
//    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
//        ByteStreamWrapper out = ByteStreamPool.get();
//        try {
//            YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
//            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
//            return out.toByteArray();
//        }finally{
//            ByteStreamPool.ret2pool(out);
//        }
//    }

//    @SuppressWarnings("unused")
//    private static byte[] NV21toRgba(byte[] nv21, int width, int height) {
//        ByteStreamWrapper out = ByteStreamPool.get();
//        try {
//            YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
//            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
//
//            final Bitmap bitmap = BitmapFactory.decodeStream(out.getInputStream());
//            int bytes = bitmap.getByteCount();
//            ByteBuffer buffer = ByteBuffer.allocate(bytes);
//            bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
//            return buffer.array();
//        }finally{
//            ByteStreamPool.ret2pool(out);
//        }
//    }

    private static Bitmap getBitmapFromByte(byte[] rgba, int w, int h){
        ByteBuffer buffer = ByteBuffer.wrap(rgba);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bm.copyPixelsFromBuffer(buffer);
        return bm;
    }

    //取巧模式
    public static void getBytes420SPNv21_New(Image image, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            int Y_pixelsStride = planes[0].getPixelStride();
            int Y_rowStride = planes[0].getRowStride();
            int Y_srcIndex = 0;
            int Y_dstIndex = 0;
            ByteBuffer tt = planes[0].getBuffer();
            tt.clear();
            for (int j = 0; j < height; j++) {
                //System.arraycopy(planes[0].getBuffer().array(), Y_srcIndex, yuvBytes, Y_dstIndex, width);
                tt.get(yuvBytes, Y_dstIndex, width);
                Y_srcIndex += Y_rowStride;
                Y_dstIndex += width;
                if (Y_srcIndex < tt.capacity()) {
                    tt.position(Y_srcIndex);
                }
            }

            int U_pixelsStride = planes[2].getPixelStride();
            int U_rowStride = planes[2].getRowStride();
            int U_srcIndex = 0;
            int U_dstIndex = 0;
            ByteBuffer tt2 = planes[2].getBuffer();
            tt2.clear();
            for (int j = 0; j < height / 2; j++) {
                //System.arraycopy(planes[1].getBuffer().array(), U_srcIndex, yuvBytes, Y_dstIndex+U_dstIndex, width);
                if (j == (height /2 -1)){
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width-1);
                }else {
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width);
                }
                U_srcIndex += U_rowStride;
                U_dstIndex += width;
                if (U_srcIndex < tt2.capacity()) {
                    tt2.position(U_srcIndex);
                }
            }
            yuvBytes[(int) (width*height*1.5-1)]= planes[1].getBuffer().get(planes[1].getBuffer().capacity());
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
    }

    //取巧模式
    //事实上很多手机的结构类似
    /*
    Y通道
    YYYY
    YYYY
    YYYY
    YYYY
    //U通道
    UVUV
    UVU
    //V通道
    VUVU
    VUV
     */
    public static void getBytes420SPNv12_New(Image image, byte[] yuvBytes) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            int Y_pixelsStride = planes[0].getPixelStride();
            int Y_rowStride = planes[0].getRowStride();
            int Y_srcIndex = 0;
            int Y_dstIndex = 0;
            ByteBuffer tt = planes[0].getBuffer();
            tt.clear();
            for (int j = 0; j < height; j++) {
                //System.arraycopy(planes[0].getBuffer().array(), Y_srcIndex, yuvBytes, Y_dstIndex, width);
                tt.get(yuvBytes, Y_dstIndex, width);
                Y_srcIndex += Y_rowStride;
                Y_dstIndex += width;
                if (Y_srcIndex < tt.capacity()) {
                    tt.position(Y_srcIndex);
                }
            }

            int U_pixelsStride = planes[1].getPixelStride();
            int U_rowStride = planes[1].getRowStride();
            int U_srcIndex = 0;
            int U_dstIndex = 0;
            ByteBuffer tt2 = planes[1].getBuffer();
            tt2.clear();
            for (int j = 0; j < height / 2; j++) {
                if (j == (height /2 -1)){
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width-1);
                }else {
                    tt2.get(yuvBytes, Y_dstIndex + U_dstIndex, width);
                }
                U_srcIndex += U_rowStride;
                U_dstIndex += width;
                if (U_srcIndex < tt2.capacity()) {
                    tt2.position(U_srcIndex);
                }
            }
            yuvBytes[(int) (width*height*1.5-1)]= planes[2].getBuffer().get(planes[2].getBuffer().capacity());
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
        }
    }

    public static byte[] rotateYUV420SP(byte[] src, int width, int height) {
        byte[] dst = new byte[src.length];
        int wh = width * height;
        //旋转Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                dst[k] = src[width * j + i];
                k++;
            }
        }

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        for (int colIndex = 0; colIndex < halfWidth; colIndex++) {
            for (int rowIndex = halfHeight - 1; rowIndex >= 0; rowIndex--) {
                int index = (halfWidth * rowIndex + colIndex) * 2;
                dst[k] = src[wh + index];
                k++;
                dst[k] = src[wh + index + 1];
                k++;
            }
        }
        return dst;
    }



}
