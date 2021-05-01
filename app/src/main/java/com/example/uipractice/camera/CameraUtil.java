package com.example.uipractice.camera;

import android.util.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class CameraUtil {

    //选择sizeMap中大于并且最接近width和height的size
    public static Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() <= height && option.getHeight() <= width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
//                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                    return Long.signum(rhs.getWidth() * rhs.getHeight() - lhs.getWidth() * lhs.getHeight() );
                }
            });
        }
        return sizeMap[0];
    }

    private Size setOptimalPreviewSize(Size[] sizes, int previewViewWidth, int previewViewHeight) {
        List<Size> bigEnoughSizes = new ArrayList<>();
        List<Size> notBigEnoughSizes = new ArrayList<>();

        for (Size size : sizes) {
            if (size.getWidth() >= previewViewWidth && size.getHeight() >= previewViewHeight) {
                bigEnoughSizes.add(size);
            } else {
                notBigEnoughSizes.add(size);
            }
        }

//        if (bigEnoughSizes.size() > 0) {
//            return Collections.min(bigEnoughSizes, new CompareSizesByArea());
//        } else if (notBigEnoughSizes.size() > 0) {
//            return Collections.max(notBigEnoughSizes, new CompareSizesByArea());
//        } else {
//            Log.d(TAG, "未找到合适的预览尺寸");
            return sizes[0];
//        }
    }
}
