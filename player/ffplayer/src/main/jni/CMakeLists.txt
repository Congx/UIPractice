# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(librtmp)
add_subdirectory(x264)

add_library( native-lib
             SHARED
             native-lib.cpp)


find_library(log-lib log)

target_link_libraries( native-lib
                       ${log-lib} )