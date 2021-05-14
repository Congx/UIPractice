# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.


AUX_SOURCE_DIRECTORY(. SRC_LIST)

include_directories(../log include)

set(JNI_LIBS_DIR src/main/jniLibs)

add_library(
        h264
        STATIC
        IMPORTED
)

#set(path ${CMAKE_SOURCE_DIR}/${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libx264.so)
#message("path = ${path}")

set_target_properties(
        h264
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
)

add_library( x264
             SHARED
            ${SRC_LIST}
            h264-lib.cpp)


find_library(log-lib log)

target_link_libraries( x264
#                        h264
                       ${log-lib} )