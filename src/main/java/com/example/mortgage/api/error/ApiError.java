package com.example.mortgage.api.error;

public record ApiError(String code, String message, String traceId) {
}
