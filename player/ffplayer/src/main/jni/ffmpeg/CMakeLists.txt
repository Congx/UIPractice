# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.


AUX_SOURCE_DIRECTORY(. SRC_LIST)

include_directories(../log .)

# ---
add_library(
        libavcodec
        SHARED
        IMPORTED
)

set_target_properties(
        libavcodec
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.so
)

# ---
add_library(
        libavdevice
        SHARED
        IMPORTED
)

set_target_properties(
        libavdevice
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libavdevice.so
)

# ---
add_library(
        libavfilter
        SHARED
        IMPORTED
)

set_target_properties(
        libavfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.so
)

# ---
add_library(
        libavformat
        SHARED
        IMPORTED
)

set_target_properties(
        libavformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libavformat.so
)

# ---
add_library(
        libavutil
        SHARED
        IMPORTED
)

set_target_properties(
        libavutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libavutil.so
)

# ---
add_library(
        libswresample
        SHARED
        IMPORTED
)

set_target_properties(
        libswresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libswresample.so
)

# ---
add_library(
        libswscale
        SHARED
        IMPORTED
)

set_target_properties(
        libswscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${CMAKE_ANDROID_ARCH_ABI}/libswscale.so
)


add_library(ffmpeg
            SHARED
            ${SRC_LIST})


find_library(log-lib log)

target_link_libraries( ffmpeg
#                        x264lib
        libavcodec
        libavdevice
        libavfilter
        libavformat
        libavutil
        libswresample
        libswscale
                       ${log-lib} )