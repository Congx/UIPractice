# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(cmaketest)

add_library( native-lib
             SHARED
             native-lib.cpp)

add_library( native-lib2
             SHARED
             native-lib2.cpp)


find_library(jnigraphics-lib jnigraphics)
find_library(log-lib log)

include_directories(./cmaketest/math/include)

target_link_libraries( native-lib
                       math
                       ${jnigraphics-lib}
                       ${log-lib} )