# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(. src_dir)
include_directories(./include)

add_library(opencv-so
        SHARED
        IMPORTED)
set_target_properties(
        opencv-so
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${ANDROID_ABI}/libopencv_java4.so )

add_library( opencv-lib
             SHARED
            ${src_dir})


find_library(log-lib log)
#find_library(log-lib log)

target_link_libraries( opencv-lib
                        ${opencv-so}
                        ${android}
                       ${log-lib} )