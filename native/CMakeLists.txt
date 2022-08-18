cmake_minimum_required(VERSION 3.20)

project(wooting-analog-sdk-java-glue)

set(CMAKE_CXX_STANDARD 17)

message("System Name " ${CMAKE_SYSTEM_NAME})
message("System Processor " ${CMAKE_SYSTEM_PROCESSOR})
message("Host System Processor " ${CMAKE_HOST_SYSTEM_PROCESSOR})

if ("${CMAKE_BUILD_TYPE}" STREQUAL "")
    set(CMAKE_BUILD_TYPE Release)
endif()

if (${WIN32})
    if (${CMAKE_HOST_SYSTEM_PROCESSOR} STREQUAL x86_64)
        set(LIB_DIR windows/x86_64)
        set(WOOTING_SDK libs/analogsdk/windows)
        set(WOOTING_DLL ${CMAKE_SOURCE_DIR}/${WOOTING_SDK}/wrapper/wooting_analog_wrapper.dll)
    else()
        message(FATAL_ERROR "only x86_64 is suported for windows")
    endif()
elseif(${UNIX})
    if (${APPLE})
        if (${CMAKE_SYSTEM_PROCESSOR} STREQUAL x86_64)
            set(LIB_DIR apple/x86_64)
            set(WOOTING_SDK libs/analogsdk/apple/x86_64)
            set(WOOTING_DLL ${CMAKE_SOURCE_DIR}/${WOOTING_SDK}/wrapper/libwooting_analog_wrapper.dylib)
        else()
            set(LIB_DIR apple/arm64)
            set(WOOTING_SDK libs/analogsdk/apple/arm64)
            set(WOOTING_DLL ${CMAKE_SOURCE_DIR}/${WOOTING_SDK}/wrapper/libwooting_analog_wrapper.dylib)
        endif()
    else()
        if (${CMAKE_HOST_SYSTEM_PROCESSOR} STREQUAL x86_64)
            set(LIB_DIR linux/x86_64)
            set(WOOTING_SDK libs/analogsdk/linux)
            set(WOOTING_DLL ${CMAKE_SOURCE_DIR}/${WOOTING_SDK}/wrapper/libwooting_analog_wrapper.so)
        else()
            message(FATAL_ERROR "only x86_64 is suported for linux")
        endif()
    endif()
else()
    message(FATAL_ERROR "not supported os")
endif()

set(JNI_INCLUDE_DIRS ${CMAKE_SOURCE_DIR}/libs/jni/include)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../src/main/resources/native/${LIB_DIR})
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../src/main/resources/native/${LIB_DIR})

message("lib output dir " ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
message("build type " ${CMAKE_BUILD_TYPE})


if(${MINGW})
    add_link_options(-static-libgcc -static-libstdc++)
endif()

add_library(${PROJECT_NAME} SHARED wrapperglue/glue.cpp)

#add wooting libs
target_link_directories(${PROJECT_NAME} PUBLIC ${WOOTING_SDK}/wrapper/)
target_include_directories(${PROJECT_NAME} PUBLIC ${WOOTING_SDK}/wrapper/includes-cpp)
target_link_libraries(${PROJECT_NAME} PUBLIC wooting_analog_wrapper)

#include winpthreads
if (${MINGW})
    message(mingw)
    SET_TARGET_PROPERTIES(${PROJECT_NAME} PROPERTIES PREFIX "")
    add_link_options(-static-libgcc -static-libstdc++)
    set(MINGW_LIBS_DIR ${CMAKE_SOURCE_DIR}/libs/mingw)
    message(${MINGW_LIBS_DIR})
    add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD COMMAND "${CMAKE_COMMAND}" -E copy
            ${MINGW_LIBS_DIR}/* ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
            COMMENT "copy mingw threads to resources to ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}"
    )
endif()

add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD COMMAND "${CMAKE_COMMAND}" -E copy
        ${WOOTING_DLL} ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
        COMMENT "copy wooting wrapper lib to ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")

#jni include
target_include_directories(${PROJECT_NAME} PRIVATE
        ${JNI_INCLUDE_DIRS})

