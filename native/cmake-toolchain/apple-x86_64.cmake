set(CMAKE_SYSTEM_NAME Darwin)
set(CMAKE_SYSTEM_PROCESSOR x86_64)

SET(TOOL_CHAIN_PREFIX x86_64-apple-darwin22-)

SET(CMAKE_C_COMPILER                   ${TOOL_CHAIN_PREFIX}clang)
SET (CMAKE_C_FLAGS_INIT                "-Wall -std=c99")
SET (CMAKE_C_FLAGS_DEBUG_INIT          "-g")
SET (CMAKE_C_FLAGS_MINSIZEREL_INIT     "-Os -DNDEBUG")
SET (CMAKE_C_FLAGS_RELEASE_INIT        "-O3 -DNDEBUG")
SET (CMAKE_C_FLAGS_RELWITHDEBINFO_INIT "-O2 -g")

SET (CMAKE_CXX_COMPILER                  ${TOOL_CHAIN_PREFIX}clang++)
SET (CMAKE_CXX_FLAGS_INIT                "-Wall")
SET (CMAKE_CXX_FLAGS_DEBUG_INIT          "-g")
SET (CMAKE_CXX_FLAGS_MINSIZEREL_INIT     "-Os -DNDEBUG")
SET (CMAKE_CXX_FLAGS_RELEASE_INIT        "-O3 -DNDEBUG")
SET (CMAKE_CXX_FLAGS_RELWITHDEBINFO_INIT "-O2 -g")

SET (CMAKE_AR                             "/usr/bin/llvm-ar")
SET (CMAKE_LINKER                         "/usr/bin/llvm-ld")
SET (CMAKE_NM                             "/usr/bin/llvm-nm")
SET (CMAKE_OBJDUMP                        "/usr/bin/llvm-objdump")
SET (CMAKE_RANLIB                         "/usr/bin/llvm-ranlib")

SET(CMAKE_TOOLCHAIN_PREFIX                 "llvm-")
set(CMAKE_CROSS_COMPILING TRUE)