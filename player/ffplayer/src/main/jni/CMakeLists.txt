# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories(. ./log librtmp ./x264/include ./faac/include)

aux_source_directory(. SRC_DIR)

add_subdirectory(librtmp)
add_subdirectory(ffmpeg)
add_subdirectory(soundtouch)

add_library( ffplayer
             SHARED
             ${SRC_DIR})

add_library(
        x264-lib
        STATIC
        IMPORTED
)

set_target_properties(
        x264-lib
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
)

add_library(
        faac-lib
        SHARED
        IMPORTED
)

set_target_properties(
        faac-lib
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libfaac.so
)


find_library(log-lib log)

target_link_libraries( ffplayer
                        rtmpdump
                        x264-lib
                        faac-lib
                       ${log-lib} )