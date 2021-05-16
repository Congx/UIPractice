# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.


AUX_SOURCE_DIRECTORY(. SRC_LIST)

include_directories(../log include)

add_library(
        x264lib
        STATIC
        IMPORTED
)

set_target_properties(
        x264lib
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
)


add_library(x264
            STATIC
            ${SRC_LIST})


find_library(log-lib log)

target_link_libraries( x264
#                        x264lib
                       ${log-lib} )