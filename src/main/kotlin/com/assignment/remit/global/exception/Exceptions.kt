package com.assignment.remit.global.exception

abstract class CustomException(message: String): RuntimeException(message)
class PermissionDeniedException(message: String = "권한이 없습니다"): CustomException(message)