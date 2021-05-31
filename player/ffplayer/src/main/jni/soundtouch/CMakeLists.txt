# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.


include_directories(../log ./include ./SoundTouch)
#include_directories(.)

aux_source_directory(./ st_dir) # 这里的名字一定不要和上层的重名

add_library(
        soundtouchlib
        SHARED
        ${st_dir}
)

find_library(log-lib log)

target_link_libraries( soundtouchlib
                       ${log-lib}
        )