#!/bin/bash

#example before install:
#
#NAME/NODE DRIVER/ENDPOINT STATUS  BUILDKIT PLATFORMS
#default * docker
#  default default         running 23.0.0   linux/amd64, linux/amd64/v2, linux/amd64/v3, linux/386
docker buildx ls
docker run --privileged --rm tonistiigi/binfmt --install all

#example after install:
#
#NAME/NODE DRIVER/ENDPOINT STATUS  BUILDKIT PLATFORMS
#default * docker
#  default default         running 23.0.0   linux/amd64, linux/amd64/v2, linux/amd64/v3, linux/386, linux/arm64, linux/riscv64, linux/ppc64le, linux/s390x, linux/mips64le, linux/mips64, linux/arm/v7, linux/arm/v6
docker buildx ls

docker buildx create --name SpringBootBuild --use

docker buildx ls
# Wait until "docker.sock inactive" is changed to "running":

#docker buildx ls
#NAME/NODE          DRIVER/ENDPOINT             STATUS   BUILDKIT PLATFORMS
#SpringBootBuild *  docker-container
#  springbootbuild0 unix:///var/run/docker.sock inactive
#default            docker
#  default          default                     running  23.0.0   linux/amd64, linux/amd64/v2, linux/amd64/v3, linux/386, linux/arm64, linux/riscv64, linux/ppc64le, linux/s390x, linux/mips64le, linux/mips64, linux/arm/v7, linux/arm/v6

#docker buildx ls
#NAME/NODE          DRIVER/ENDPOINT             STATUS  BUILDKIT PLATFORMS
#SpringBootBuild *  docker-container
#  springbootbuild0 unix:///var/run/docker.sock running v0.11.4  linux/amd64, linux/amd64/v2, linux/amd64/v3, linux/arm64, linux/riscv64, linux/ppc64le, linux/s390x, linux/386, linux/mips64le, linux/mips64, linux/arm/v7, linux/arm/v6
#default            docker
#  default          default                     running 23.0.0   linux/amd64, linux/amd64/v2, linux/amd64/v3, linux/386, linux/arm64, linux/riscv64, linux/ppc64le, linux/s390x, linux/mips64le, linux/mips64, linux/arm/v7, linux/arm/v6

docker buildx inspect --bootstrap

# Output:
#Name:          SpringBootBuild
#Driver:        docker-container
#Last Activity: 2023-03-09 11:51:47 +0000 UTC
#
#Nodes:
#Name:      springbootbuild0
#Endpoint:  unix:///var/run/docker.sock
#Status:    running
#Buildkit:  v0.11.4
#Platforms: linux/amd64, linux/amd64/v2, linux/amd64/v3, linux/arm64, linux/riscv64, linux/ppc64le, linux/s390x, linux/386, linux/mips64le, linux/mips64, linux/arm/v7, linux/arm/v6

# Instructions took from here: https://www.linkedin.com/pulse/building-multi-architecture-spring-boot-app-amazon-ecs-m%C3%B6llering/

# Build as:
# docker buildx build --platform linux/amd64,linux/arm64 --tag <account-id>/<your-repo>:latest --push .
#