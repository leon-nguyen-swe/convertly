package dev.convertly.api.repository;

import dev.convertly.api.entity.ConversionJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionJobRepository extends JpaRepository<ConversionJob, UUID> {}
