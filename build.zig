const std = @import("std");
const mem = std.mem;

var alloc = std.heap.ArenaAllocator{};

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    const strip = b.option(bool, "strip", "Strip debug symbols from the resulting binaries to minimize size");
    const Os = std.Target.Os.Tag;
    const lib_mod = b.createModule(.{
        .target = target,
        .optimize = optimize,
        .strip = strip
    });
    lib_mod.addCSourceFiles(.{
        .files = &[_][]const u8{
            "native/wrapperglue/glue.cpp",
        }
    });

    const lib = b.addLibrary(.{
        .name = "wooting-analog-sdk-java-glue",
        .root_module = lib_mod,
        .linkage = .dynamic,
    });

    lib.linkLibCpp();
    lib_mod.addIncludePath(b.path("native/libs/jni/include/"));
    lib_mod.linkSystemLibrary("wooting_analog_sdk_dist", .{});
    if (target.result.os.tag == Os.linux) {
        if (b.lazyDependency("wooting_wrapper_linux_x86_64", .{})) |wrapper| {
            //Linking against static library, because of missing SONAME in the dynamic one
            lib_mod.addRPathSpecial("$ORIGIN");
            lib_mod.addLibraryPath(wrapper.path("release"));
            lib_mod.addIncludePath(wrapper.path("includes"));
            b.getInstallStep().dependOn(&b.addInstallLibFile(
                wrapper.path("release/libwooting_analog_sdk_dist.so"),
                "libwooting_analog_sdk_dist.so").step);
        }
    } else if (target.result.os.tag == Os.windows) {
        if (b.lazyDependency("wooting_wrapper_windows_x86_64", .{})) |wrapper| {
            lib_mod.addLibraryPath(wrapper.path("release"));
            lib_mod.addIncludePath(wrapper.path("includes"));
            b.getInstallStep().dependOn(&b.addInstallLibFile(
                wrapper.path("release/wooting_analog_sdk_dist.dll"),
                "wooting_analog_sdk_dist.dll").step);
        }
    } else if (target.result.os.tag == Os.macos) {
        lib_mod.addRPathSpecial("@loader_path/");
        if (target.result.cpu.arch == std.Target.Cpu.Arch.x86_64) {
            if (b.lazyDependency("wooting_wrapper_apple_x86_64", .{})) |wrapper| {
                lib_mod.addLibraryPath(wrapper.path("release"));
                lib_mod.addIncludePath(wrapper.path("includes"));
                b.getInstallStep().dependOn(&b.addInstallLibFile(
                    wrapper.path("release/libwooting_analog_sdk_dist.dylib"),
                    "libwooting_analog_sdk_dist.dylib").step);
            }
        } else {
            if (b.lazyDependency("wooting_wrapper_apple_aarch64", .{})) |wrapper| {
                lib_mod.addLibraryPath(wrapper.path("release"));
                lib_mod.addIncludePath(wrapper.path("includes"));
                b.getInstallStep().dependOn(&b.addInstallLibFile(
                    wrapper.path("release/libwooting_analog_sdk_dist.dylib"),
                    "libwooting_analog_sdk_dist.dylib").step);
            }
        }
    }
    const install = b.addInstallArtifact(lib, .{
        .dest_dir = .{ .override = .lib },
    });
    b.getInstallStep().dependOn(&install.step);
}
