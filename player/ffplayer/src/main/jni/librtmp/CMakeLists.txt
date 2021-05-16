# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

ADD_DEFINITIONS(-DNO_CRYPTO)

AUX_SOURCE_DIRECTORY(. SRC_LIST)

include_directories(../log)

add_library( rtmpdump
             SHARED
            ${SRC_LIST})


find_library(log-lib log)

target_link_libraries( rtmpdump
                       ${log-lib} )