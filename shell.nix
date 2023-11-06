let
  # fetchGit { url = "/home/hadi/dev/nixpkgs"; rev = "cafee7500dc2f5a26b499598144e2762aa4b6a0f"; }
  pkgs = import
    (
      fetchTarball https://github.com/nixos/nixpkgs/tarball/30cb7ed401952396df992a52813702c9bbf338c3
    )
    {
      config.android_sdk.accept_license = true;
      config.allowUnfree = true;
    };

  android = {
    versions = {
      platformTools = "33.0.3";
      buildTools = [
        "30.0.3"
        "31.0.0"
        "33.0.0"
      ];
      cmdLine = "8.0";
      emulator = "32.1.12";
    };

    platforms = [ "27" "28" "29" "30" "31" "32" "33" ];
    abis = [ "x86" "x86_64" ]; # "armeabi-v7a" "arm64-v8a"
    extras = [ "extras;google;gcm" ];
  };

  sdkArgs = {
    platformToolsVersion = android.versions.platformTools;
    buildToolsVersions = android.versions.buildTools;
    includeEmulator = true;
    emulatorVersion = android.versions.emulator;
    platformVersions = android.platforms;
    includeSources = true;
    includeSystemImages = true;
    systemImageTypes = [ "google_apis_playstore" ];
    abiVersions = android.abis;
    cmdLineToolsVersion = android.versions.cmdLine;
    useGoogleAPIs = false;
    extraLicenses = [
      "android-sdk-preview-license"
      "android-googletv-license"
      "android-sdk-arm-dbt-license"
      "google-gdk-license"
      "intel-android-extra-license"
      "intel-android-sysimage-license"
      "mips-android-sysimage-license"
    ];
  };

  androidComposition = pkgs.androidenv.composeAndroidPackages sdkArgs;
  androidSdk = androidComposition.androidsdk;
  androidSdkHome = "${androidSdk}/libexec/android-sdk";
  userHome = "${builtins.toString ./.user-home}";
  androidUserHome = "${userHome}/.android";
  androidAvdHome = "${androidUserHome}/avd";


  androidEmulator = pkgs.androidenv.emulateApp {
    name = "emulate-android-nix";
    platformVersion = "31";
    abiVersion = "x86_64";
    systemImageType = "google_apis_playstore";
    androidUserHome = androidUserHome;
    androidAvdHome = androidAvdHome;
    sdkExtraArgs = sdkArgs;
  };

  platformTools = androidComposition.platform-tools;
  jdk = pkgs.jdk17;
in
with pkgs;
pkgs.mkShell {
  name = "firefly-mastodon";

  packages = [
    androidEmulator
    platformTools
    androidSdk
    git
    which
    libunwind
    android-studio
    gradle
    glslang
    vulkan-headers
    vulkan-loader
    vulkan-validation-layers
    vulkan-tools
  ];

  JAVA_HOME = "${jdk}";
  ANDROID_HOME = "${androidSdkHome}";
  ANDROID_NDK_ROOT = "${androidSdkHome}/ndk-bundle";

  # Ensures that we don't have to use a FHS env by using the nix store's aapt2.
  GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${androidSdkHome}/build-tools/${lib.last android.versions.buildTools}/aapt2";

  HOME = "${userHome}";
  USER_HOME = "${userHome}";
  GRADLE_USER_HOME = "${userHome}/.gradle";
  IDEA_VM_OPTIONS = "${userHome}/.idea-bin/idea64.vmoptions";
  IDEA_PROPERTIES = "${userHome}/.idea-bin/idea.properties";
  ANDROID_USER_HOME = "${androidUserHome}";
  ANDROID_AVD_HOME = "${androidAvdHome}";

  shellHook = ''
    mkdir -p ${androidAvdHome}
    # Add cmake to the path.
    export PATH="$cmake_root/bin:${jdk}/bin:$PATH"

    # Write out local.properties for Android Studio.
    cat <<EOF > local.properties
      # This file was automatically generated by nix-shell.
      sdk.dir=$ANDROID_HOME
      ndk.dir=$ANDROID_NDK_ROOT
      cmake.dir=$cmake_root
    EOF
  '';
}
