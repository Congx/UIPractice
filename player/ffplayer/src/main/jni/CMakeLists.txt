# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories(./log librtmp ./x264/include)

add_subdirectory(librtmp)
#add_subdirectory(x264)

add_library( ffplayer
             SHARED
             native-lib.cpp)

add_library(
        x264
        STATIC
        IMPORTED
)

set_target_properties(
        x264
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
)


find_library(log-lib log)

target_link_libraries( ffplayer
                        rtmpdump
                        x264
                       ${log-lib} )