import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

/// The [AppTheme] defines light and dark themes for the app.
///
/// Theme setup for FlexColorScheme package v8.
/// Use same major flex_color_scheme package version. If you use a
/// lower minor version, some properties may not be supported.
/// In that case, remove them after copying this theme to your
/// app or upgrade the package to version 8.4.0.
///
/// Use it in a [MaterialApp] like this:
///
/// MaterialApp(
///   theme: AppTheme.light,
///   darkTheme: AppTheme.dark,
/// );
abstract final class AppTheme {
  /// The FlexColorScheme defined light mode ThemeData.
  static ThemeData light = FlexThemeData.light(
    /// Using FlexColorScheme built-in FlexScheme enum based colors
    scheme: FlexScheme.deepOrangeM3,

    /// Component theme configurations for light mode.
    subThemesData: const FlexSubThemesData(
      interactionEffects: true,
      tintedDisabledControls: true,
      useMaterial3Typography: true,
      sliderYear2023: false,
      progressIndicatorYear2023: false,
      inputDecoratorSchemeColor: SchemeColor.primary,
      inputDecoratorIsFilled: true,
      inputDecoratorIsDense: true,
      inputDecoratorBackgroundAlpha: 14,
      inputDecoratorBorderSchemeColor: SchemeColor.primary,
      inputDecoratorBorderType: FlexInputBorderType.outline,
      inputDecoratorRadius: 10,
      inputDecoratorUnfocusedHasBorder: false,
      inputDecoratorFocusedBorderWidth: 1,
      inputDecoratorPrefixIconSchemeColor: SchemeColor.onPrimaryFixedVariant,
      cardElevation: 0,
      alignedDropdown: true,
      bottomSheetClipBehavior: Clip.antiAliasWithSaveLayer,
      bottomNavigationBarShowUnselectedLabels: false,
      searchBarElevation: 0,
      searchViewElevation: 0,
      navigationBarLabelBehavior: NavigationDestinationLabelBehavior.onlyShowSelected,
      navigationRailUseIndicator: true,
      navigationRailLabelType: NavigationRailLabelType.selected,
    ),

    /// Direct ThemeData properties.
    visualDensity: FlexColorScheme.comfortablePlatformDensity,
    cupertinoOverrideTheme: const CupertinoThemeData(applyThemeToAll: true),
  );

  /// The FlexColorScheme defined dark mode ThemeData.
  static ThemeData dark = FlexThemeData.dark(
    /// Using FlexColorScheme built-in FlexScheme enum based colors.
    scheme: FlexScheme.deepOrangeM3,

    /// Component theme configurations for dark mode.
    subThemesData: const FlexSubThemesData(
      interactionEffects: true,
      tintedDisabledControls: true,
      blendOnColors: true,
      useMaterial3Typography: true,
      sliderYear2023: false,
      progressIndicatorYear2023: false,
      inputDecoratorSchemeColor: SchemeColor.primary,
      inputDecoratorIsFilled: true,
      inputDecoratorIsDense: true,
      inputDecoratorBackgroundAlpha: 45,
      inputDecoratorBorderSchemeColor: SchemeColor.primary,
      inputDecoratorBorderType: FlexInputBorderType.outline,
      inputDecoratorRadius: 10,
      inputDecoratorUnfocusedHasBorder: false,
      inputDecoratorFocusedBorderWidth: 1,
      inputDecoratorPrefixIconSchemeColor: SchemeColor.primaryFixed,
      cardElevation: 0,
      alignedDropdown: true,
      bottomSheetClipBehavior: Clip.antiAliasWithSaveLayer,
      bottomNavigationBarShowUnselectedLabels: false,
      searchBarElevation: 0,
      searchViewElevation: 0,
      navigationBarLabelBehavior: NavigationDestinationLabelBehavior.onlyShowSelected,
      navigationRailUseIndicator: true,
      navigationRailLabelType: NavigationRailLabelType.selected,
    ),

    /// Direct ThemeData properties.
    visualDensity: FlexColorScheme.comfortablePlatformDensity,
    cupertinoOverrideTheme: const CupertinoThemeData(applyThemeToAll: true),
  );
}
