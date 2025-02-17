const std = @import("std");
const mem = std.mem;

var alloc = std.heap.ArenaAllocator{};

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    //const strip = b.option(bool, "strip", "Strip debug symbols from the resulting binaries to minimize size");
    const Os = std.Target.Os.Tag;
    const lib = b.addSharedLibrary(.{
        .name = "wooting-analog-sdk-java-glue",
        .target = target,
        .optimize = optimize,
        //.strip = strip
    });
    lib.addCSourceFiles(.{
        .files = &[_][]const u8{
            "native/wrapperglue/glue.cpp",
        }
    });
    lib.linkLibCpp();
    lib.addIncludePath(b.path("native/libs/jni/include/"));
    lib.linkSystemLibrary("wooting_analog_wrapper");
    if (target.result.os.tag == Os.linux) {
        const wrapper = b.dependency("wooting_wrapper_linux_x86_64", .{});
        lib.root_module.addRPathSpecial("$ORIGIN/");
        lib.addLibraryPath(wrapper.path("wrapper/"));
        lib.addIncludePath(wrapper.path("wrapper/includes"));
        b.getInstallStep().dependOn(&b.addInstallLibFile(
            wrapper.path("wrapper/libwooting_analog_wrapper.so"),
            "libwooting_analog_wrapper.so").step);
    } else if (target.result.os.tag == Os.windows) {
        const wrapper = b.dependency("wooting_wrapper_windows_x86_64", .{});
        lib.addLibraryPath(wrapper.path("wrapper/"));
        lib.addIncludePath(wrapper.path("wrapper/includes"));
        b.getInstallStep().dependOn(&b.addInstallBinFile(
            wrapper.path("wrapper/wooting_analog_wrapper.dll"),
            "wooting_analog_wrapper.dll").step);
    } else if (target.result.os.tag == Os.macos) {
        lib.root_module.addRPathSpecial("@loader_path/");
        if (target.result.cpu.arch == std.Target.Cpu.Arch.x86_64) {
            const wrapper = b.dependency("wooting_wrapper_apple_x86_64", .{});
            lib.addLibraryPath(wrapper.path("wrapper/"));
            lib.addIncludePath(wrapper.path("wrapper/includes"));
            b.getInstallStep().dependOn(&b.addInstallLibFile(
                wrapper.path("wrapper/libwooting_analog_wrapper.dylib"),
                "libwooting_analog_wrapper.dylib").step);
        } else {
            const wrapper = b.dependency("wooting_wrapper_apple_aarch64", .{});
            lib.addLibraryPath(wrapper.path("wrapper/"));
            lib.addIncludePath(wrapper.path("wrapper/includes"));
            b.getInstallStep().dependOn(&b.addInstallLibFile(
                wrapper.path("wrapper/libwooting_analog_wrapper.dylib"),
                "libwooting_analog_wrapper.dylib").step);
        }
    }
    b.installArtifact(lib);
}
