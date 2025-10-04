# Single-stage build using runtime-only image
FROM eclipse-temurin:21-jre-alpine

# Create application user
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Set working directory
WORKDIR /app

# Copy the pre-built jar from local build output
COPY build/libs/restapi-*-SNAPSHOT.jar app.jar

# Change ownership of the application
RUN chown appuser:appuser app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]