FROM runmymind/docker-android-sdk:alpine-standalone

ENV LANG=en_GB.UTF-8 \
    LANGUAGE=en_GB.UTF-8

RUN apk add --update --no-cache ruby ruby-dev build-base \
  && gem install --no-ri --no-rdoc fastlane

WORKDIR /tmp/sdk
ADD . /tmp/sdk
RUN ./gradlew --no-daemon install

RUN bundle update --bundler

RUN rm -rf /tmp/sdk
