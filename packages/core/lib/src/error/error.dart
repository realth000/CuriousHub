import 'package:freezed_annotation/freezed_annotation.dart';

part 'error.freezed.dart';

/// App wide errors.
///
/// All error types are defined here.
@freezed
sealed class AppError with _$AppError {
  /// Network issue.
  const factory AppError.network() = _Network;

  /// Server returned error message.
  const factory AppError.server({int? code, String? message}) = _Server;

  /// Not authorized.
  const factory AppError.unauthorized() = _Unauthorized;

  /// Unknown error, as a fallback.
  const factory AppError.unknown({String? message}) = _Unknown;
}
