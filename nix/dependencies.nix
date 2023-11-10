{ localDevelopment ? false }:
let
  # fetchGit { url = "/home/hadi/dev/nixpkgs"; rev = "cafee7500dc2f5a26b499598144e2762aa4b6a0f"; }
  pkgs = import
    (
      fetchTarball https://github.com/nixos/nixpkgs/tarball/1809b328770806f94d306799147cc1a847d5328f
    )
    {
      config.android_sdk.accept_license = true;
      config.allowUnfree = true;
    };

  android = {
    versions = {
      platformTools = "34.0.5";
      buildTools = [
        "33.0.1"
        "34.0.0"
      ];
      cmdLine = "9.0";
      emulator = "32.1.15";
    };

    platforms = [ "33" "34" ];
    abis = [ "x86" "x86_64" ]; # "armeabi-v7a" "arm64-v8a"
    extras = [ "extras;google;gcm" ];
  };

  sdkArgs = {
    platformToolsVersion = android.versions.platformTools;
    buildToolsVersions = android.versions.buildTools;
    includeEmulator = localDevelopment;
    emulatorVersion = android.versions.emulator;
    platformVersions = android.platforms;
    includeSources = localDevelopment;
    includeSystemImages = localDevelopment;
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

  androidEmulator = pkgs.lib.optionalAttrs localDevelopment (pkgs.androidenv.emulateApp {
    name = "emulate-android-nix";
    platformVersion = "31";
    abiVersion = "x86_64";
    systemImageType = "google_apis_playstore";
    androidUserHome = androidUserHome;
    androidAvdHome = androidAvdHome;
    sdkExtraArgs = sdkArgs;
  });

  platformTools = androidComposition.platform-tools;
  jdk = pkgs.jdk17;
in
{
  pkgs = pkgs;
  android = android;
  androidComposition = androidComposition;
  androidSdk = androidSdk;
  androidEmulator = androidEmulator;
  platformTools = platformTools;
  devJdk = jdk;

  androidSdkHome = androidSdkHome;
  userHome = userHome;
  androidUserHome = androidUserHome;
  androidAvdHome = androidAvdHome;
}

