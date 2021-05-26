#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/native_window_jni.h>
using namespace cv;
ANativeWindow *window = 0;
class A{


};
class MyPtr{
public:
    MyPtr(A *a);
    ~MyPtr(){
        delete a;
    }

private:
    A *a;
};
void  test(){
    A *a=new A;
//    MyPtr ptr(new A());
}
pthread_mutex_t mutex;
DetectionBasedTracker *tracker = 0;

class CascadeDetectorAdapter:public DetectionBasedTracker::IDetector{
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector): IDetector(),Detector(detector){

    }
//    适配器检测到了很多物体，交给适配器，告诉我 这些形状是不是属于这个分类   类似于RecyerView  产生了滑动，通知Adatpter要拿数据过来了。
//    此时 是
    void detect(const cv::Mat &image, std::vector<cv::Rect> &objects){
//        代表最近邻

        Detector->detectMultiScale(image, objects, scaleFactor, minNeighbours, 0, minObjSize,
                                   maxObjSize);
    }

private:
    CascadeDetectorAdapter();
    cv::Ptr<cv::CascadeClassifier> Detector;
};

extern "C"
JNIEXPORT void JNICALL
Java_com_maniu_facedemo2_MainActivity_init(JNIEnv *env, jobject thiz, jstring model_) {
pthread_mutex_init(&mutex, 0);
const char *model = env->GetStringUTFChars(model_, 0);
//    new 一个分类器
//    CascadeClassifier *cascadeClassifier= new CascadeClassifier();
//Ptr  帮助我们释放
if (tracker) {
tracker->stop();
delete tracker;
tracker = 0;
}
//智能指针  分类器  分类器只运行一次   跟踪器运行多次
Ptr<CascadeClassifier> classifier = makePtr<CascadeClassifier>(model);

//    创建跟踪器
//1 mainDetector  建一个跟踪适配器
/**
 * DetectionBasedTracker   相当于RecyclerView
 * mainDetector            相当于Adapter   跟踪谁  ，适配器说了算
 * Parameters              入参出参 携带参数
 */
//创建一个检测器
Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(classifier);

//    创建一个跟踪器
Ptr<CascadeClassifier> classifier1 = makePtr<CascadeClassifier>(model);
//创建一个跟踪适配器
Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(classifier1);

//拿去用的跟踪器
DetectionBasedTracker::Parameters detectorParams;
tracker = new DetectionBasedTracker(mainDetector, trackingDetector, detectorParams);
//开启跟踪器
tracker->run();
env->ReleaseStringUTFChars(model_, model);

}extern "C"
JNIEXPORT void JNICALL
Java_com_maniu_facedemo2_MainActivity_setSurface(JNIEnv *env, jobject thiz, jobject surface) {

if (window) {
ANativeWindow_release(window);
window = 0;
}
window = ANativeWindow_fromSurface(env, surface);
}extern "C"
JNIEXPORT void JNICALL
Java_com_maniu_facedemo2_MainActivity_postData(JNIEnv *env, jobject thiz, jbyteArray data_, jint w,
        jint h, jint cameraId) {


// nv21的数据
jbyte *data = env->GetByteArrayElements(data_, NULL);
//mat  data-》Mat
//1、高 2、宽
Mat src(h + h / 2, w, CV_8UC1, data);
//颜色格式的转换 nv21->RGBA
//将 nv21的yuv数据转成了rgba
cvtColor(src, src, COLOR_YUV2RGBA_NV21);
// 正在写的过程 退出了，导致文件丢失数据
//imwrite("/sdcard/src.jpg",src);
if (cameraId == 1) {
//前置摄像头，需要逆时针旋转90度
rotate(src, src, ROTATE_90_COUNTERCLOCKWISE);
//水平翻转 镜像
flip(src, src, 1);
} else {
//顺时针旋转90度
rotate(src, src, ROTATE_90_CLOCKWISE);
}
Mat gray;
//灰色
cvtColor(src, gray, COLOR_RGBA2GRAY);
//增强对比度 (直方图均衡)
equalizeHist(gray, gray);
std::vector<Rect> faces;
//定位人脸 N个
tracker->process(gray);
tracker->getObjects(faces);
for (Rect face : faces) {
//画矩形
//分别指定 bgra
rectangle(src, face, Scalar(255, 0, 255));
}
//显示
if (window) {
//设置windows的属性
// 因为旋转了 所以宽、高需要交换
//这里使用 cols 和rows 代表 宽、高 就不用关心上面是否旋转了
ANativeWindow_setBuffersGeometry(window, src.cols, src.rows, WINDOW_FORMAT_RGBA_8888);
ANativeWindow_Buffer buffer;
do {
if (!window) {
break;
}
ANativeWindow_setBuffersGeometry(window, src.cols, src.rows, WINDOW_FORMAT_RGBA_8888);
ANativeWindow_Buffer buffer;
if (ANativeWindow_lock(window, &buffer, 0)) {
ANativeWindow_release(window);
window = 0;
break;
}

uint8_t *dstData = static_cast<uint8_t *>(buffer.bits);
int dstlineSize = buffer.stride * 4;

uint8_t *srcData = src.data;
int srclineSize = src.cols * 4;
for (int i = 0; i < buffer.height; ++i) {
memcpy(dstData + i * dstlineSize, srcData + i * srclineSize, srclineSize);
}
ANativeWindow_unlockAndPost(window);
} while (0);
}
//释放Mat
//内部采用的 引用计数
src.release();
gray.release();
env->ReleaseByteArrayElements(data_, data, 0);

}extern "C"
JNIEXPORT void JNICALL
Java_com_maniu_facedemo2_MainActivity_release(JNIEnv *env, jobject thiz) {

if (tracker) {
tracker->stop();
delete tracker;
tracker = 0;
}



}