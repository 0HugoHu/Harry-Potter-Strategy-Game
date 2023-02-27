FROM ubuntu:20.04

MAINTAINER Drew Hilton "0@hugohu.top"

USER root

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get -yq dist-upgrade \
  && apt-get install -yq --no-install-recommends \
     curl \
     wget \
     bzip2 \
     sudo \
     locales \
     ca-certificates \
     git \
     unzip \
     openjdk-17-jdk-headless \
     emacs-nox

RUN echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen

ARG LOCAL_USER_ID=1001
ENV USER_ID ${LOCAL_USER_ID}
RUN adduser --uid ${USER_ID} juser
WORKDIR /home/juser

# Setup a minimal emacs with dcoverage
USER juser
WORKDIR /home/juser
COPY --chown=juser scripts/emacs-bare.sh ./
RUN mkdir -p /home/juser/.emacs.d/dcoverage
COPY --chown=juser scripts/dcoverage.el /home/juser/.emacs.d/dcoverage/
RUN chmod u+x emacs-bare.sh && ./emacs-bare.sh


# we are going to do a bit of gradle first, just to speed
# up future builds
COPY --chown=juser build.gradle gradlew settings.gradle  ./
COPY --chown=juser gradle/wrapper gradle/wrapper


# this will fetch gradle 7.3, and the packages we depend on
RUN ./gradlew resolveDependencies


# Now we copy all our source files in.  Note that
# if we change src, etc, but not our gradle setup,
# Docker can resume from this point
COPY --chown=juser ./ ./

# compile the code
RUN ./gradlew :server:assemble
