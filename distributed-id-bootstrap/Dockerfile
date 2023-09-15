ARG AGENT_DOCKER_IMAGE_VAR
FROM nexus-gateway.fpdev.tech/p-common/docker-infer:infer-1.1.0
FROM ${AGENT_DOCKER_IMAGE_VAR}
VOLUME ["/tmp"]
ADD target/framework-boot.jar /app/framework-boot.jar
