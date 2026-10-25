CREATE TABLE conversion_job (
    id UUID PRIMARY KEY,
    status VARCHAR NOT NULL,
    source_format VARCHAR NOT NULL,
    target_format VARCHAR NOT NULL,
    original_file_name VARCHAR NOT NULL,
    input_size_bytes BIGINT NOT NULL,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ
);

CREATE INDEX idx_conversion_job_status
    ON conversion_job (status);