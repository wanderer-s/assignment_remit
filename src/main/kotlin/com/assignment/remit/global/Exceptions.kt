package com.assignment.remit.global

class PermissionDeniedException(message: String = "권한이 없습니다"): RuntimeException(message)