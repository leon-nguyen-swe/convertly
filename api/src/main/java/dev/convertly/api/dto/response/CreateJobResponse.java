package dev.convertly.api.dto.response;

import java.util.UUID;

public record CreateJobResponse(UUID jobId, String uploadUrl) {}
